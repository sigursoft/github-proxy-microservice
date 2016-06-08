package bel.kozik.github;

import bel.kozik.github.service.GitHubService;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.logging.Logger;

/**
 * GitHub request handler.
 * Created by Anton Kozik on 25.05.16.
 */
class GitHubHttpHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(GitHubHttpHandler.class.getName());

    private GitHubService service = new GitHubService();

    /**
     * Handles requests from client
     *
     * @param exchange - HTTP exchange object
     * @throws Exception
     */
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String[] pathParams = exchange.getRelativePath().split("/");
        if (pathParams.length > 2) {
            String owner = pathParams[1];
            String repo = pathParams[2];
            LOGGER.fine(String.format("Path parameters are %s and %s", owner, repo));
            String response = service.findRepository(owner, repo);
            exchange.getResponseSender().send(response);
        } else {
            throw new IllegalArgumentException("Not all parameters are provided");
        }
    }

}