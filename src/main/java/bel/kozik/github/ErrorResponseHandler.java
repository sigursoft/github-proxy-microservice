package bel.kozik.github;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import static io.undertow.util.Headers.CONTENT_LENGTH;
import static io.undertow.util.Headers.CONTENT_TYPE;


/**
 * Error Response Handler.
 * Created by Anton Kozik on 26.05.16.
 */
class ErrorResponseHandler implements HttpHandler {

    private static final String HTML = "<!doctype html><html><head><title>Error</title></head><body>Internal Server Error</body></html>";

    private static final String TEXT_HTML = "text/html";

    private final HttpHandler next;

    ErrorResponseHandler(HttpHandler next) {
        this.next = next;
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.addDefaultResponseListener(httpServerExchange -> {
           if (!exchange.isResponseChannelAvailable()) {
               return false;
           }
            if (exchange.getResponseCode() == 500) {
                exchange.getResponseHeaders().put(CONTENT_LENGTH, "" + HTML.length());
                exchange.getResponseHeaders().put(CONTENT_TYPE, TEXT_HTML);
                exchange.getResponseSender().send(HTML);
                return true;
            }
            return false;
        });
        next.handleRequest(exchange);
    }
}