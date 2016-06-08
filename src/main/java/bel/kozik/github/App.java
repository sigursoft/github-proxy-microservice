package bel.kozik.github;

import io.undertow.Undertow;
import io.undertow.server.handlers.BlockingHandler;

import java.util.Optional;
import java.util.logging.Logger;

import static io.undertow.Handlers.path;

/**
 * Simple App with embedded http server.
 * Created by Anton Kozik on 25.05.16.
 */
public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    private static Undertow server;

    /**
     * Applications main entry point. Starts a lightweight HTTP server.
     * @param args - command line arguments
     */
    public static void main(String[] args) {
        start();
    }

    static void start() {
        String hostname = System.getProperty("HOST", "localhost");
        int port = Integer.parseInt(System.getProperty("PORT", Integer.toString(8080)));
        LOGGER.info(String.format("Hostname = %s, port = %d", hostname, port));


        GitHubHttpHandler gitHubHttpHandler = new GitHubHttpHandler();
        BlockingHandler blockingHandler = new BlockingHandler(gitHubHttpHandler);
        ErrorResponseHandler wrapperHandler = new ErrorResponseHandler(blockingHandler);

        server = Undertow.builder()
                .addHttpListener(port, hostname)
                .setHandler(path().addPrefixPath("/repositories", wrapperHandler)).build();

        LOGGER.info("Starting Undertow...");
        server.start();
    }

    static void stop() {
        Optional.ofNullable(server).ifPresent(Undertow::stop);
    }

}
