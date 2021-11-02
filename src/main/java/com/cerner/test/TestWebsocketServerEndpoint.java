package com.cerner.test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.server.standard.SpringConfigurator;

@Slf4j
@ServerEndpoint(value = "/websocket", configurator = SpringConfigurator.class)
public class TestWebsocketServerEndpoint extends Endpoint {

  private static final AtomicInteger sessionCount = new AtomicInteger();

  public TestWebsocketServerEndpoint() {}

  @Override
  public void onOpen(final Session session, final EndpointConfig config) {
    session.addMessageHandler(new TextHandler(session));
    log.info(
        "Opened websocket sessionId={} with requestParams={}",
        session.getId(),
        session.getRequestParameterMap());
    logCount(sessionCount.incrementAndGet());
  }

  @Override
  @OnClose
  public void onClose(final Session session, final CloseReason closeReason) {
    log.info("Closed websocket sessionId={} with reason={}", session.getId(), closeReason);
    logCount(sessionCount.decrementAndGet());
  }

  @Override
  @OnError
  public void onError(final Session session, final Throwable t) {
    log.warn("Error on websocket sessionId={}", session.getId(), t);
  }

  private static void logCount(final int count) {
    log.info("There are now {} open websocket connections", count);
  }

  static class TextHandler implements MessageHandler.Whole<String> {

    private final Session session;

    TextHandler(final Session session) {
      this.session = session;
    }

    @Override
    public void onMessage(final String message) {
      log.debug("Received message from websocket sessionId={}, data={}", session.getId(), message);
      try {
        final String reply = "Reply to: " + message;
        final Basic endpoint = session.getBasicRemote();
        synchronized (endpoint) {
          endpoint.sendText(reply);
        }
      } catch (final IOException e) {
        log.error("Error sending reply to sessionId={}", session.getId(), e);
      }
    }
  }
}
