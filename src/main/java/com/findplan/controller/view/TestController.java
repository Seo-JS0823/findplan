package com.findplan.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
	
	@GetMapping("/member/test")
	public String memberTest() {
		return "memberTest";
	}
	
}
