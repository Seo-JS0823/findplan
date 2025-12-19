package com.findplan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ua_parser.Parser;

@Configuration
public class UserAgentParserConfig {

	@Bean
	Parser uaParser() {
		return new Parser();
	}
}
