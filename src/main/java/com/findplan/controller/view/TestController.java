package com.findplan.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

	@GetMapping("/member")
	public String memberTest() {
		return "test/member";
	}
	
}
