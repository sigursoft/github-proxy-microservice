package bel.kozik.github;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.resource;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.logging.Logger;

import bel.kozik.github.service.GitHubService;
import io.undertow.Undertow;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.cache.DirectBufferCache;
import io.undertow.server.handlers.resource.CachingResourceManager;
import io.undertow.server.handlers.resource.ClassPathResourceManager;


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
        try {
            start();
        } catch (IOException e) {
            LOGGER.severe(String.format("Failed to configure Infinispan cache manager. %s", e.getMessage()));
        }
    }

    static void start() throws IOException {
        String hostname = System.getProperty("HOST", "localhost");
        int port = Integer.parseInt(System.getProperty("PORT", Integer.toString(8080)));
        LOGGER.info(String.format("Hostname = %s, port = %d", hostname, port));

        // GIT HUB SERVICE
        GitHubService service = new GitHubService("cache/infinispan.xml");
        GitHubHttpHandler gitHubHttpHandler = new GitHubHttpHandler(service);
        BlockingHandler blockingHandler = new BlockingHandler(gitHubHttpHandler);
        ErrorResponseHandler errorResponseHandler = new ErrorResponseHandler(blockingHandler);

        // STATIC RESOURCES
        ClassPathResourceManager cprm = new ClassPathResourceManager(App.class.getClassLoader(), "html");
        CachingResourceManager crm = new CachingResourceManager(100, 65536, new DirectBufferCache(1024, 10, 10480),
                                                                cprm, (int) Duration.ofDays(7).getSeconds());

        server = Undertow.builder()
                .addHttpListener(port, hostname)
                .setHandler(path().addPrefixPath("/", resource(crm)).addPrefixPath("/repositories", errorResponseHandler))
                .build();

        server.start();
        LOGGER.info("Undertow started.");
    }

    static void stop() {
        Optional.ofNullable(server).ifPresent(Undertow::stop);
    }

}
