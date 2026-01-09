package com.findplan.domain.member.model;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
	MEMBER(List.of("ROLE_MEMBER")),
	FIND(List.of("ROLE_MEMBER", "ROLE_FIND")),
	ADMIN(List.of("ROLE_MEMBER", "ROLE_FIND", "ROLE_ADMIN"));
	
	private final List<String> roles;
}
