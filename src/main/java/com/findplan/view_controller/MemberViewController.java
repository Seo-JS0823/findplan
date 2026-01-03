package com.findplan.view_controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberViewController {

	@GetMapping("/")
	public String index() {
		return "index";
	}
	
}
