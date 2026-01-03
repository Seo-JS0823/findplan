// 사용자가 입력을 끝마친 후 0.5초(기본값) 뒤 콜백함수 실행되는 함수
function debounce(func, timeout = 500) {
	let timer;
	return (...args) => {
		clearTimeout(timer);
		timer = setTimeout(() => { func.apply(this, args); }, timeout); 
	};
}

// document 유틸 함수 모음
// FIND 객체 권장
const $ = {
	id: (id) => document.getElementById(id),
	className: (className) => document.getElementsByClassName(className)[0],
	on: (target, event, handler) => {
		if(target) {
			target.addEventListener(event, handler);
		}
	},
	toggle: (target, className) => {
		if(target) {
			target.classList.toggle(className);
		}
	}
}

// 단일 유틸 함수
const getId = (id) => {
	return document.getElementById(id);
}

// 단일 유틸 함수
const getClass = (className) => {
	return document.getElementsByClassName(className)[0];
}

// 단일 이벤트 등록 함수
const addEvent = (target, event, handler) => {
	if(target instanceof HTMLElement) {
		target.addEventListener(event, handler);
	}
}

// document 제어 함수 모음
const FIND = {
	// 디버그용
	warn(element) {
		console.log(`${element} 객체를 찾을 수 없습니다.`);
		return;
	},
	// 디버그용
	null(object) {
		console.log(`${object} 는 비어있습니다.`);
		return;
	},
	// 디버그용
	unMatchesByType(type) {
		console.log(`${type} 이 타입 검사에 실패하였습니다.`);
		return;
	},
	// 모달 제어 토글 함수
	toggle(target, className) {
		if(!target) FIND.warn(target);
		if(!className) FIND.null(className);
		
		if(target instanceof HTMLElement) target.classList.toggle(className);
		else FIND.unMatchesByType(target);
		return this;
	},
	// document.getElementById 반환 + 메서드 체이닝 범용
	// 각 설명은 아래
	id(id) {
		const el = document.getElementById(id);
		if(!el) {
			console.warn(`${id} 엘리먼트를 찾을 수 없습니다.`);
			return null;
		}
		return {
			el,
			// 클래스에 등록된 DOM의 ClassList 제어
			toggle(className) {
				if(el) this.on('click', () => { el.classList.toggle(className) })
				else FIND.warn(el);
				
				return this;
			},
			// 클래스에 등록된 DOM의 innerText 변경
			text(text) {
				if(el) el.innerText = text;
				else FIND.warn(el);
				
				return this;
			},
			// 클래스에 등록된 DOM의 CSS 추가
			css(prop, val) {
				if(el) el.style[prop] = val;
				else FIND.warn(el);
				
				return this;
			},
			// 클래스에 등록된 DOM의 CSS 추가 - 한번에
			cssAll(css) {
				if(el) {
					for(const [prop, val] of Object.entries(css)) {
						el.style[prop] = val;
					}
				}
				else FIND.warn(el);
				return this;
			},
			// 클래스에 등록된 DOM에 이벤트 등록
			on(event, handler) {
				if(el) el.addEventListener(event, handler);
				else FIND.warn(el);
				
				return this;
			},
			// 클래스에 등록된 DOM에 여러 이벤트 등록
			onAny(eventMap) {
				if(el) {
					for(const [event, handler] of Object.entries(eventMap)) {
						el.addEventListener(event, handler);
					}
				}
				else FIND.warn(el);
				
				return this;
			},
			// 클래스에 등록된 DOM과 관련있는 태그에 버블링 방지
			// [parameter] target = $.id() or getId()
			bubbling(target) {
				target.addEventListener('click', (e) => {
					e.preventDefault();
					e.stopPropagation();
				})
				return this;
			}
		}
	}
}

// Fetch 요청 유틸 함수
class RequestBuilder {
	constructor(url) {
		this.url = url;
		this.options = {
			method: 'GET',
			headers: {
				'Content-Type':'application/json',
				// CSRF 설정 기본값
				// getCsrfToken() => auth.js
				'X-XSRF-TOKEN': getCsrfToken()
			}
		}
	}
	
	// 사용법 = await new RequestBuilder(url).post()
	post() { this.options.method = 'POST'; return this; }
	
	put() { this.options.method = 'PUT'; return this; }
	
	patch() { this.options.method = 'PATCH'; return this; }
	
	delete() { this.options.method = 'DELETE'; return this; }
	
	credentials() { this.options['credentials'] = 'include'; }
	
	authorization(token) {
		this.options.headers['Authorization'] = `Bearer ${token}`;
		return this;
	}
	
	addHeader(prop, value) {
		this.options.headers[prop] = value;
		return this;
	}
	
	json(data) {
		this.options.body = JSON.stringify(data);
		return this;
	}
	
	async send() {
		const res = await fetch(this.url, this.options)
		return res.json();
	}
}