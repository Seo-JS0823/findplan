(async () => {
	const res = await new FindFetchRequest('/api/member/me').post().credentials().send();
	
	if(res.success === true) {
		window.location.href = res.redirectUrl;
	}
})();

// 로그인 모달창 Open & Close
FIND.id('loginModalToggle').on('click', () => {
	const loginModal = $.id('loginModal');
	const signupModal = $.id('signupModal');
	
	$.toggleChange(loginModal, signupModal, 'open-f');
})

// 회원가입 모달창 Open & Close
FIND.id('signupModalToggle').on('click', () => {
	const signupModal = $.id('signupModal');
	const loginModal = $.id('loginModal');
	
	$.toggleChange(signupModal, loginModal, 'open-f');
})

// 회원가입 이메일 중복 체크
FIND.id('signupEmail').on('input', debounce( async (e) => {
	const email = e.target.value;
	
	const regexEmail = /^(?=.*[a-z])(?=.*[0-9])[a-z0-9]{2,10}$/;
	
	if(!regexEmail.test(email)) {
		FIND.id('email-duplicate')
		    .failText('아이디는 소문자와 숫자만 포함된 10글자 이하로 작성하세요.');
		return;
	}
	
	const res = await new FindFetchRequest(`/api/member/dupli-e?e=${encodeURIComponent(email)}`).send();
	responseHandler(res,
	() => {
		FIND.id('email-duplicate')
		    .successText('사용 가능한 아이디 입니다.');
	},
	() => {
		FIND.id('email-duplicate')
		    .failText('이미 사용중인 아이디 입니다.');
	})
	emailValidator(res.success);
}))

// 회원가입 닉네임 중복 체크
FIND.id('signupNickname').on('input', debounce( async (e) => {
	const nickname = e.target.value;
	
	const res = await new FindFetchRequest(`/api/member/dupli-n?n=${encodeURIComponent(nickname)}`).send();
	responseHandler(res,
	() => {
		FIND.id('nickname-duplicate')
		    .successText('사용 가능한 닉네임 입니다.');
	},
	() => {
		FIND.id('nickname-duplicate')
		    .failText('이미 사용중인 닉네임 입니다.');
	})
	nicknameValidator(res.success);
}))

// 회원가입 요청 전 입력값 검증 객체
const signupState = {
	email: false,
	password: false,
	nickname: false
}

const signupValidator = (setter) => {
	return (isValid) => { setter(isValid); }
}

// 입력값 유효성 검증 객체 제어
const emailValidator = signupValidator(v => signupState.email = v);
const passwordValidator = signupValidator(v => signupState.password = v);
const nicknameValidator = signupValidator(v => signupState.nickname = v);

// 입력값 유효성 체크 --> 통과 = true
const isSignupState = () => {
	if(Object.values(signupState).every(value => value === true) === true) {
		const email = $.id('signupEmail').value;
		const password = $.id('signupPassword').value;
		const nickname = $.id('signupNickname').value;
		
		return {
			email: email,
			password: password,
			nickname: nickname
		}
	}
	else {
		return null;
	}
}

// 회원가입 비밀번호 입력값 체크
FIND.id('signupPassword').on('input', debounce((e) => {
	const password = e.target.value;
	
	const passwordRegex = /^(?=.*[a-z])(?=.*[!@#$%^&*]).{9,}$/;
		
	const passwordValidate = passwordRegex.test(password);
	
	const adapter = {
		success: passwordValidate
	}
	
	responseHandler(adapter,
	() => {
		FIND.id('password-validate')
		    .successText('사용 가능한 비밀번호 입니다.');
	},
	() => {
		FIND.id('password-validate')
		    .failText('특수문자를 하나 이상 포함한 9자 이상으로 지어주세요.');
	})
	passwordValidator(passwordValidate);
}))

// 회원가입 요청
FIND.id('signup').on('click', async () => {
	const member = isSignupState();
	
	if(!member) {
		alert('제대로 입력하고 회원가입 해라 씨바라마!');
		return;
	}
	
	const res = await new FindFetchRequest('/api/member/signup').post().json(member).send();
	responseHandler(res,
	() => {console.log('회원가입 완료')},
	() => {console.log('회원가입 실패')});
})

// 로그인 요청
FIND.id('login').on('click', async () => {
	const email = $.id('loginEmail').value;
	const password = $.id('loginPassword').value;
	const checked = $.id('auto-login');
		
	const member = {
		email: email,
		password: password
	}
	
	if(checked.checked === true) {
		member['rememberMe'] = true;
	}
	
	if(!member.email || !member.password) {
		alert('제대로 입력하고 로그인 해라 씨바라마!');
		return;
	}
	
	const res = await new FindFetchRequest('/api/member/login').post().json(member).send();
	
	responseHandler(res,
	() => {
		auth.setToken(res.data.accessToken);
		window.location.href = res.redirectUrl;
	},
	() => {
		if(res.data === 'RETRACT_USER_LOGIN') {
			if(confirm('탈퇴 처리중인 계정입니다. 철회하시겠습니까?')) {
				alert('철회 짜응');
			}
		}
	})
})