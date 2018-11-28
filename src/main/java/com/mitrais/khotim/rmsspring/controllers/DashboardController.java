package com.mitrais.khotim.rmsspring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller(value = "dashboardController-client")
@RequestMapping("/")
public class DashboardController {
	/**
	 * Shows dashbaord page.
	 *
	 * @return
	 */
	@GetMapping
	public String index(){
		return "index";
	}

	/**
	 * Displays login form.
	 *
	 * @return View template.
	 */
	@GetMapping("/login")
	public String login() {
		return "login";
	}
}
