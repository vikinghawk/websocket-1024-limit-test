# websocket-1024-limit-test

## WebsocketConnectionLimitTest

Spring Boot App that accepts websocket connections on the "/websocket" endpoint.


To generate websocket client connection load on the server, you can hit the following REST endpoints:

- GET "/test/websockets/create?count=N" - Creates N websocket client connections. N=1 if count query param isn't set.
- GET "/test/websockets/close" - closes all open websocket client connections

### Configuration:

Configure the websocket server url that the clients should use via spring config prop: `test.websocket.url`

### Recreate steps:

Start 1 instance of the app and try to generate more than 1024 connections to the container.

When coming in thru the go router we can only create up to ~1024 connections per container instance before the client starts getting 502s.

This seems to be a limitation of the envoy proxy inside the container rather than the gorouter as adding additional gorouter instances does not make a difference.

Also, we can NOT recreate the 1024 limit when using container-to-container networking.