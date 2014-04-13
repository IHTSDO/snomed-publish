package com.ihtsdo.snomed.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ihtsdo.snomed.dto.refset.TagDto;
import com.ihtsdo.snomed.dto.refset.TagsDto;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.SnapshotNotFoundException;
import com.ihtsdo.snomed.exception.TagNotFoundException;
import com.ihtsdo.snomed.model.refset.Tag;
import com.ihtsdo.snomed.service.Page;
import com.ihtsdo.snomed.service.refset.RefsetService;
import com.ihtsdo.snomed.service.refset.RefsetService.SortOrder;
import com.ihtsdo.snomed.service.refset.TagService;

@Controller
@RequestMapping("/refsets")
public class TagController {

    private static final Logger LOG = LoggerFactory.getLogger(TagController.class);
        
    @Inject 
    TagService tagService;
    
    @Inject
    RefsetService refsetService;    
        
    @RequestMapping(value = "{refsetName}/tags", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public TagsDto getAllTagsForRefset(@PathVariable String refsetName, 
            @RequestParam("sortBy") String sortBy, 
            @RequestParam("sortOrder") SortOrder sortOrder,
            @RequestParam(value="filter", defaultValue="", required=false) String filter,
            @RequestParam("pageIndex") int pageIndex,
            @RequestParam("pageSize") int pageSize) throws RefsetNotFoundException{
        LOG.debug("Received request for all tags for refset {}", refsetName);
        
        //make sure refset exists, or throw exception
        refsetService.findByPublicId(refsetName);
        
        Page<Tag> tagsPage = tagService.findAllTags(refsetName, sortBy, sortOrder, filter, pageIndex, pageSize);
        
        List<TagDto> tagDtos = new ArrayList<>();
        for (Tag t : tagsPage.getContent()){
            tagDtos.add(TagDto.parse(t));
        }
        return new TagsDto(tagDtos, tagsPage.getTotalElements());
    }    

    @RequestMapping(value = "{refsetName}/tags", 
            method = RequestMethod.POST, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)     
    public TagDto createTag(@PathVariable String refsetName, @Valid @RequestBody TagDto tagDto) 
            throws RefsetNotFoundException, NonUniquePublicIdException, SnapshotNotFoundException
    {
        LOG.debug("Received request to create tag of refset [{}]", refsetName);
        return TagDto.parse(tagService.create(refsetName, tagDto));
    } 
    
    @RequestMapping(value = "{refsetName}/tag/{tagName}", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK) 
    public TagDto getTag(@PathVariable String refsetName, @PathVariable String tagName) 
            throws RefsetNotFoundException, TagNotFoundException, SnapshotNotFoundException
    {
        LOG.debug("Received request to retrieve tag {} for refset [{}]", tagName, refsetName);
        
        //make sure refset exists, or throw exception
        refsetService.findByPublicId(refsetName);
        
        return TagDto.parse(tagService.findByPublicId(refsetName, tagName));
    }    
    
    @RequestMapping(value = "{refsetName}/tag/{tagName}", 
            method = RequestMethod.DELETE, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)     
    public TagDto deleteTag(@PathVariable String refsetName, @PathVariable String tagName) 
            throws RefsetNotFoundException, TagNotFoundException, SnapshotNotFoundException
    {
        LOG.debug("Received request to delete tag {} for refset [{}]", tagName, refsetName);
        
        //make sure refset exists, or throw exception
        refsetService.findByPublicId(refsetName);
        
        return TagDto.parse(tagService.delete(refsetName, tagName));
    } 
    
}
