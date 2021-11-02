package com.cerner.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

@Configuration
// @ComponentScan("com.cerner.test")
public class WebsocketConnectionLimitTestConfig {

  @Bean
  public ServerEndpointExporter endpointExporter() {
    // https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-web-servers.html#howto-create-websocket-endpoints-using-serverendpoint
    return new ServerEndpointExporter();
  }

  @Bean
  public ServerEndpointRegistration testEndpoint() {
    return new ServerEndpointRegistration("/websocket", TestWebsocketServerEndpoint.class);
  }
}
