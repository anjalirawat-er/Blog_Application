package com.blog.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {

	private int id;
	private String content;
	
	public CommentDto(int id, String content) {
		super();
		this.id = id;
		this.content = content;
	}

	//create no argument constructor
	
	public CommentDto() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}	
	
}


