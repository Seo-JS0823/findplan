// 사용자 입력 이후 지정된 시간 이후에 콜백 함수 실행
function debounce(func, timeout = 500) {
	let timer;
	return (...args) => {
		clearTimeout(timer);
		timer = setTimeout(() => { func.apply(this, args); }, timeout);
	}
}

// 유틸 함수
// id : DOM - id 가져오기
// className : DOM - class 첫 번째 가져오기
// className : DOM - class 인덱스 지정 가져오기
// on : 이벤트 등록
const $ = {
	id: (id) => document.getElementById(id),
	className: (className) => document.getElementsByClassName(className)[0],
	classNameIndex: (className, index) => document.getElementByClassName(className)[index],
	on: (target, event, handler) => {
		if(target instanceof HTMLElement) {
			target.addEventListener(event, handler);
		}
	}
}

// 유틸 함수 DOM - id 가져오기
const getId = (id) => {
	return document.getElementById(id);
}

// 유틸 함수 DOM - class 첫 번째 가져오기
const getClass = (className) => {
	return document.getElementsByClassName(className)[0];
}

// 유틸 함수 DOM - class 인덱스 지정 가져오기
const getClassIndex = (className, index) => {
	return document.getElementByClassName(className)[index];
}

// 이벤트 등록 함수
const addEvent = (target, event, handler) => {
	if(target instanceof HTMLElement) {
		target.addEventListener(event, handler);
	}
}

// 종합 유틸 함수 시작 -> FIND.id
const FIND = {
	id(id) {
		const el = document.getElementById(id);
		if(!el) {
			console.warn(`${id} DOM ID 엘리먼트 X`);
			return;
		}
		return {
			el,
			
			on(event, handler) {
				if(el) el.addEventListener(event, handler);
				
				return this;
			}
		}
	}
}

// Fetch 요청 종합 유틸 클래스
class FindFetchRequest {
	constructor(url) {
		this.url = url;
		this.options = {
			method: 'GET',
			headers: {
				'Content-Type':'application/json',
				'X-XSRF-TOKEN': getCsrfToken()
			}
		}
	}
	
	post() { this.options.method = 'POST'; return this; }
	
	put() { this.options.method = 'PUT'; return this; }
	
	patch() { this.options.method = 'PATCH'; return this; }
	
	delete() { this.options.method = 'DELETE'; return this; }
	
	credentials() { this.options['credentials'] = 'include'; return this; }
	
	authorization(token) {
		this.options.headers['Authorization'] = `Bearer ${token}`;
		return this;
	}
	
	addHeader(prop, value) {
		this.options.headers[prop] = value;
		return this;
	}
	
	json(data) {
		this.options['body'] = JSON.stringify(data);
		return this;
	}
	
	async send() {
		const res = await fetch(this.url, this.options);
		return res.json();
	}
}































