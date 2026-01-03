package com.findplan.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.findplan.member.entity.MemberEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomDetails implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	
	private final MemberEntity member;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return member.getRole().getRoles().stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return member.getMemberPassword();
	}

	@Override
	public String getUsername() {
		return member.getMemberEmail();
	}

	public String getNickname() {
		return member.getMemberNickname();
	}
	
	@Override
	public boolean isAccountNonExpired() { return true; }

	@Override
	public boolean isAccountNonLocked() { return true; }

	@Override
	public boolean isCredentialsNonExpired() { return true; }

	@Override
	public boolean isEnabled() { return true; }
	
}
