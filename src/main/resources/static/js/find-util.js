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
	},
	toggle: (target, className) => {
		if(target instanceof HTMLElement) {
			target.classList.toggle(className);
		}
	},
	addClass: (target, className) => {
		if(target instanceof HTMLElement) target.classList.add(className);
	},
	removeClass: (target, className) => {
		if(target instanceof HTMLElement) target.classList.remove(className);
	},
	clear: (target) => {
		if(target instanceof HTMLElement) {
			const inputs = target.querySelectorAll('input');
			inputs.forEach(input => input.value = '');
		}
	},
	toggleChange: (openTarget, closeTarget, className) => {
		if(openTarget instanceof HTMLElement && closeTarget instanceof HTMLElement) {
			$.removeClass(closeTarget, className);
			$.clear(closeTarget);
			
			$.toggle(openTarget, className);
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
	// 디버깅
	warn(el) {
		console.warn(`${el} 객체를 찾을 수 없습니다.`);
		return;
	},
	// 디버깅
	null(el) {
		console.log(`${el} 은/는 비어있습니다.`);
		return;
	},
	// id 가져와서 이것 저것 체이닝 메서드 작업
	id(id) {
		const el = document.getElementById(id);
		if(!el) {
			console.warn(`${id} DOM ID 엘리먼트 X`);
			return;
		}
		return {
			el,
			failText(failMessage) {
				if(el) {
					el.style['color'] = 'var(--fail-text)';
					el.innerText = failMessage;
				}
				else FIND.warn(el);
				
				return this;
			},
			successText(successMessage) {
				if(el) {
					el.style['color'] = 'var(--success-text)';
					el.innerText = successMessage;
				}
				else FIND.warn(el);
				
				return this;
			},
			// 해당 id에 css 속성 추가
			css(prop, value) {
				if(el) el.style[prop] = value;
				else FIND.warn(el);
				
				return this;
			},
			// 해당 id에 css 속성 한 번에 추가
			cssAll(css) {
				if(el) {
					for(const [prop, value] of Object.entries(css)) {
						el.style[prop] = value;
					}
				}
				else FIND.warn(el);
				
				return this;
			},
			// 해당 id에 텍스트 변경
			text(text) {
				if(el) el.innerText = text;
				else FIND.warn(el);
				
				return this; 
			},
			// 해당 id에 이벤트 추가
			on(event, handler) {
				if(el) el.addEventListener(event, handler);
				
				return this;
			},
			// 해당 id에 여러 이벤트 추가
			onAny(events) {
				if(el) {
					for(const [event, handler] of Object.entires(events)) {
						el.addEventListener(event, handler);
					}
				}
				else FIND.warn(el);
				
				return this;
			},
			// 타겟 id에 버블링 방지
			bubbling(target) {
				if(target) {
					target.addEventListener('click', (e) => {
						e.stopPropagation();
						e.preventDefault();
					})
				}
				else FIND.warn(target)
				
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
	// POST 요청으로 변경
	post() { this.options.method = 'POST'; return this; }
	
	// PUT 요청으로 변경
	put() { this.options.method = 'PUT'; return this; }
	
	// PATCH 요청으로 변경
	patch() { this.options.method = 'PATCH'; return this; }
	
	// DELETE 요청으로 변경
	delete() { this.options.method = 'DELETE'; return this; }
	
	// 쿠키 첨부 허용
	credentials() { this.options['credentials'] = 'include'; return this; }
	
	// 최근에 서버로부터 받은 AccessToken 첨부
	authorization() {
		this.options.headers['Authorization'] = `Bearer ${auth.getToken()}`;
		return this;
	}
	
	// 헤더 추가
	addHeader(prop, value) {
		this.options.headers[prop] = value;
		return this;
	}
	
	// Body Request 데이터 추가
	json(data) {
		this.options['body'] = JSON.stringify(data);
		return this;
	}
	
	// 최종 요청 보내기
	async send() {
		const res = await fetch(this.url, this.options);
		return res.json();
	}
}

// ApiResponse 공통 응답 Success 분기 핸들러
// successHandler Parameter : 성공 콜백
// failHandler Parameter    : 실패 콜백
function responseHandler(res, successHandler, failHandler) {
	if(res.success === true) {
		successHandler();
	} else {
		failHandler();
	}
}





























