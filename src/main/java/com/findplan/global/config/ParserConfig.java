package com.findplan.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ua_parser.Parser;

@Configuration
public class ParserConfig {

	@Bean
	Parser parser() {
		return new Parser();
	}
	
}
