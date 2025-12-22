package com.findplan.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestView {

	@GetMapping("/")
	public String branchView() {
		return "branch";
	}
	
	@GetMapping("/test/member")
	public String memberTest() {
		return "test/member";
	}
	
}
