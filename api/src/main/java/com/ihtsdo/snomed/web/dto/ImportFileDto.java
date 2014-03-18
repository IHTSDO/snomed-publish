package com.ihtsdo.snomed.web.dto;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("import")
public class ImportFileDto {

	public enum Type {
		USE_EXTENSION, JSON, XML, RF2;
	}
	
    @NotNull(message="You must select a file")
    private MultipartFile file;
    
    @NotNull(message="You must select the file type")
    private Type fileType;
    	
	
	public boolean isUseExtension(){
		return getFileType() == Type.USE_EXTENSION;
	}
	
	public boolean isRf2(){
		return getFileType() == Type.RF2;
	}

	public boolean isJson(){
		return getFileType() == Type.JSON;
	}	
	
	public boolean isXml(){
		return getFileType() == Type.XML;
	}	

	public ImportFileDto() {}


	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public Type getFileType() {
		return fileType;
	}

	public void setFileType(Type type) {
		this.fileType = type;
	}
}
