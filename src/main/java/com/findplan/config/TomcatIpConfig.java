package com.findplan.config;

import org.apache.catalina.valves.RemoteIpValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatIpConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

	@Override
	public void customize(TomcatServletWebServerFactory factory) {
		RemoteIpValve valve = new RemoteIpValve();
		valve.setRemoteIpHeader("x-forwarded-for");
		valve.setProtocolHeader("x-forwarded-proto");
		factory.addContextValves(valve);
	}
	
	
}
