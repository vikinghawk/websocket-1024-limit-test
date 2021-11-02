package com.cerner.test;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableConfigurationProperties(WebsocketConnectionLimitTestProperties.class)
public class ClientConnectionController {

  private final WebsocketConnectionLimitTestProperties props;

  private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();

  public ClientConnectionController(final WebsocketConnectionLimitTestProperties props) {
    this.props = props;
  }

  @GetMapping("/test/websockets/create")
  public String createConnections(
      @RequestParam(value = "count", required = false, defaultValue = "1") final int count) {
    int successCount = 0;
    int failureCount = 0;
    for (int i = 0; i < count; i++) {
      try {
        final Session session =
            ContainerProvider.getWebSocketContainer()
                .connectToServer(
                    TestClientEndpoint.class, URI.create(props.getUrl() + "/websocket"));
        sessions.add(session);
        successCount++;
      } catch (final DeploymentException | IOException e) {
        log.error("Error creating client websocket connection", e);
        failureCount++;
      }
    }
    return "Created "
        + successCount
        + " connections. Failed to create "
        + failureCount
        + " connections. There are now "
        + sessions.size()
        + " active connections.";
  }

  @GetMapping("/test/websockets/close")
  public String closeAllConnections() {
    final int count = sessions.size();
    sessions.forEach(
        s -> {
          try {
            s.close();
          } catch (final IOException e) {
            log.debug("Error closing sessionId={}", s.getId(), e);
          }
        });
    return "Closed " + count + " connections";
  }

  @ClientEndpoint
  public static class TestClientEndpoint {

    public TestClientEndpoint() {}

    @OnOpen
    public void onOpen(final Session session) {
      log.debug("opened websocket client sessionId={}", session.getId());
    }

    @OnClose
    public void onClose(final Session session) {
      sessions.remove(session);
      log.debug("closed websocket client sessionId={}", session.getId());
    }
  }
}
