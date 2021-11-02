package com.cerner.test;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("test.websocket")
public class WebsocketConnectionLimitTestProperties {

  private String url = "ws://localhost:8080";

  public void setUrl(final String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }
}
