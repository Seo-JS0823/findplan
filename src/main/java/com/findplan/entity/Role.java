package com.findplan.entity;

import java.util.List;

import lombok.Getter;

@Getter
public enum Role {
	MEMBER(List.of("ROLE_MEMBER")),
	FIND(List.of("ROLE_MEMBER", "ROLE_FIND"));
	
	private final List<String> roles;
	
	Role(List<String> roles) {
		this.roles = roles;
	}
	
}
