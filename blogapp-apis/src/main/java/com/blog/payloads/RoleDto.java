package com.blog.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {
	
	private int id;
	private String name;
	
	
	public RoleDto() {
		super();
	}
	public RoleDto(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	

}
