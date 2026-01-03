window.addEventListener('DOMContentLoaded', () => {
	const url = '/api/member/login/state';
	
	const request = {
		credentials: 'include',
		method: 'GET',
		headers: {
			'Content-Type': 'application/json',
			'X-XSRF-TOKEN': getCsrfToken()
		}
	}
	
	const authToken = auth.getToken();
	if(authToken) {
		request.headers['Authorization'] = `Bearer ${authToken}`;
	} 
	
	fetch(url, request)
	.then(response => response.json())
	.then(data => {
		if(data.success === true) {
			updateNavbar(data);
		} 
		else console.log('로그인 상태 X')
	});
})

// ----------------------------------------------------------------------------------------- //
// ------------------------ 상단바 로그인 / 비로그인 UI 상태 제어 -------------------------- //
// ----------------------------------------------------------------------------------------- //

const updateNavbar = (user) => {
	const authSection = $.id('login-state-area');
	
	if(user) {
		auth.setToken(user['ACCESS']);
		
		authSection.innerHTML = `
			<div id="mypage">
				<span>마이페이지</span>
			</div>
			<div id="logout-run">
				<span>로그아웃</span>
			</div>
		`;
		
		FIND.id('logout-run').on('click', async () => {
			const res = await new RequestBuilder('/api/member/logout').send();
			if(res.success === true) {
				alert('정상적으로 로그아웃 되었습니다.');
				location.reload();
				return;
			}
		})
	} else {
		location.reload();
	}
}