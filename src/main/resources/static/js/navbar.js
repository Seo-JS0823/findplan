// 회원가입요청 체크 클로저 + 상태별 UI 제어
const signupValidator = (setter, targetId, targetId2) => {
	return (isValid, successMsg, failMsg) => {
		setter(isValid);
		
		const $el = FIND.id(targetId);
		const $el2 = FIND.id(targetId2);
		if(isValid) {
			$el.text(successMsg).css('color', 'lightgreen');
			$el2.css('border', '2px solid lightgreen');
		} else {
			$el.text(failMsg).css('color', 'red');
			$el2.css('border', '2px solid red');
		}
	}
}

// 회원가입요청 체크 변수
const signupState = {
	email: false,
	password: false,
	nickname: false
}

// 회원가입 요청 전 유효성 전체 체크
const isSignupValid = () => {
	return Object.values(signupState).every(value => value === true);
}

// 회원가입요청 체크 제어 함수
const validateEmail = signupValidator(v => signupState.email = v, 'signup-email-validate', 'signup-email');
const validatePassword = signupValidator(v => signupState.password = v, 'signup-password-validate', 'signup-password');
const validateNickname = signupValidator(v => signupState.nickname = v, 'signup-nickname-validate', 'signup-nickname');

FIND // 회원가입 이메일 중복 검사 + UI 제어
.id('signup-email')
.on('input', debounce( async (e) => {
	const val = e.target.value;
	if(!val) { validateEmail(false, '', ''); return; }
	
	const res = await new RequestBuilder(`/api/member/dupli/email?value=${encodeURIComponent(val)}`).send();
	
	validateEmail(
		res === false,
		'사용 가능한 이메일 입니다.',
		'이미 사용중인 이메일 입니다.'
	)
	console.log(signupState.email);
}));

FIND // 회원가입 닉네임 중복 검사 + UI 제어
.id('signup-nickname')
.on('input', debounce( async (e) => {
	const val = e.target.value;
	if(!val) { validateNickname(false, '', ''); return; }
	
	const res = await new RequestBuilder(`/api/member/dupli/nickname?value=${encodeURIComponent(val)}`).send();
	
	validateNickname(
		res === false,
		'사용 가능한 닉네임 입니다.',
		'이미 사용중인 닉네임 입니다.'
	)
	console.log(signupState.nickname);
}))

FIND // 패스워드값 유효성 검사 + UI 제어
.id('signup-password')
.on('input', debounce((e) => {
	const val = e.target.value;
	if(!val) { validatePassword(false, '', ''); return; }
	
	const passwordRegex = /^(?=.*[a-z])(?=.*[!@#$%^&*]).{9,}$/;
	
	const passwordValid = passwordRegex.test(val);
	
	validatePassword(
		passwordValid === true,
		'안정적인 패스워드 입니다.',
		'9글자 이상 특수문자를 하나 포함해야합니다.'
	)
	
}))

FIND // 회원가입 영역 버블링 방지
.id('signup-tag')
.toggle('m-open')
.bubbling($.id('signup-modal'));

FIND // 회원가입 요청 보내기
.id('signup-run')
.on('click', async () => {
	if(!isSignupValid()) {
		alert('모든 입력란을 올바르게 입력하세요.');
		return;
	}
	
	const email = $.id('signup-email').value;
	const password = $.id('signup-password').value;
	const nickname = $.id('signup-nickname').value;
	
	const signupReq = {
		email: email,
		password: password,
		nickname: nickname
	}
	
	const res = await new RequestBuilder('/api/member/signup').post().json(signupReq).send();
	
	if(res.success === true) {
		alert('정상적으로 회원가입 되셨습니다.');
		window.location.href = '/';
	}
})

FIND // 로그인 영역 버블링 방지
.id('login-tag')
.toggle('m-open')
.bubbling($.id('login-modal'));

FIND // 로그인 요청 보내기
.id('login-run')
.on('click', async () => {
	const email = $.id('login-email').value;
	const password = $.id('login-password').value;
	
	const loginReq = {
		email: email,
		password: password
	}
	
	if(!loginReq.email || !loginReq.password) {
		alert('아이디와 패스워드를 입력해주세요.');
		return;
	}
	
	const req = await new RequestBuilder('/api/member/login').post().json(loginReq).send();
	console.log(req);
	if(req.success === true) updateNavbar(req.data);
	
})
