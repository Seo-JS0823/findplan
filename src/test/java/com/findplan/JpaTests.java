package com.findplan;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.findplan.model.Member;
import com.findplan.repository.MemberRepository;
import com.findplan.transfer.request.SigninRequest;

@DataJpaTest
class JpaTests {

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private TestEntityManager em;
	
	@Test
	@DisplayName("회원가입 테스트")
	void memberTest() {
		// 회원가입
		SigninRequest request = new SigninRequest();
		request.setEmail("root@root.com");
		request.setPassword("1234");
		request.setNickname("root");
		
		Member signin = request.signinEntity(new BCryptPasswordEncoder());
		em.persist(signin);
		em.flush();
		em.clear();
		
		Member signinAssert = memberRepository.findById(signin.getCode()).orElseThrow();
		assertThat(signinAssert.getEmail()).isEqualTo("root@root.com");
	}
	
	
}
