package com.findplan.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestBranchController {

	@GetMapping("/")
	public String branch() {
		return "branch";
	}
}
