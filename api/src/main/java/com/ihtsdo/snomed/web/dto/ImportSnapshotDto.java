package com.ihtsdo.snomed.web.dto;

import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.dto.refset.SnapshotDto;

@XmlRootElement(name="import")
@JsonRootName("import")
public class ImportSnapshotDto extends SnapshotDto {
//
//	public enum Type {
//		USE_EXTENSION, JSON, XML, RF2;
//	}
//
//	public boolean isUseExtension(){
//		return getFileType() == Type.USE_EXTENSION;
//	}
//	
//	public boolean isRf2(){
//		return getFileType() == Type.RF2;
//	}
//
//	public boolean isJson(){
//		return getFileType() == Type.JSON;
//	}	
//	
//	public boolean isXml(){
//		return getFileType() == Type.XML;
//	}	
//
//	@NotNull(message="You must select a file")
//	private MultipartFile file;
//	
//	@NotNull(message="You must select the file type")
//	private Type fileType;
//	
//	public ImportSnapshotDto() {}
//
//	public ImportSnapshotDto(String publicId, String title, String description, Set<MemberDto> members) {
//		super(publicId, title, description, members);
//	}
//
//	public MultipartFile getFile() {
//		return file;
//	}
//
//	public void setFile(MultipartFile file) {
//		this.file = file;
//	}
//
//	public Type getFileType() {
//		return fileType;
//	}
//
//	public void setFileType(Type type) {
//		this.fileType = type;
//	}
}
