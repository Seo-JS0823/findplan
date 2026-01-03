(function() {
	let accessToken = null;
	
	window.auth = {
		setToken(val) { accessToken = val; },
		getToken() { return accessToken; }
	}
})();

function getCsrfToken() {
	if(document.cookie) {
		const cookieValue = `; ${document.cookie}`;
		const parts = cookieValue.split(`; XSRF-TOKEN=`);
		
		if(parts.length === 2) {
			return parts.pop().split(';').shift();
		}
	}
	return '';
};