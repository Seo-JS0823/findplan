package com.findplan.global.auth;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.findplan.domain.member.model.MemberEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberDetails implements UserDetails {
	
	private static final long serialVersionUID = 1L;

	private final MemberEntity member;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return member.getRoles().getRoles().stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
