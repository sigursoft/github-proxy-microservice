package bel.kozik.github.service;

import bel.kozik.github.domain.Repository;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static com.jayway.jsonpath.JsonPath.read;
import static java.lang.String.format;

/**
 * GitHub Service is responsible for sending requests to GitHub API and returning results.
 * <p>
 * Created by Anton Kozik on 27.05.16.
 */
public class GitHubService {

    private static final String GIT_HUB_URL_PATTERN = "https://api.github.com/repos/%s/%s";

    private static final Logger LOGGER = Logger.getLogger(GitHubService.class.getName());

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    private final HttpClient httpClient = HttpClient.newBuilder().executor(executorService).build();

    private final Cache<String, Repository> cache;

    public GitHubService(String config) throws IOException {
        this.cache = new DefaultCacheManager(config).getCache("repos");
    }

    /**
     * @param owner - name of the owner
     * @param repo  - repository name
     * @return String - JSON object with extracted fields : full name, description, clone URI, number of stars and date of creation
     * @throws IOException - HTTP Requests can fail, no joke
     */
    public String findRepository(String owner, String repo) throws IOException {
        String url = format(GIT_HUB_URL_PATTERN, owner, repo);

        Optional<Repository> cachedRepository = Optional.ofNullable(cache.get(url));
        if (cachedRepository.isPresent()) {
            return cachedRepository.get().toJSON();
        } else {
            String responseFromGitHub = null;
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    responseFromGitHub = response.body();
                }
            } catch (InterruptedException e) {
                LOGGER.severe("HTTP Request to GitHub API interrupted.");
            }
            if (responseFromGitHub != null) {
                Repository repository = aRepository(responseFromGitHub);
                cache.putAsync(url, repository);
                return repository.toJSON();
            } else {
                throw new IllegalStateException("Failed on request to GitHub");
            }
        }
    }

    private Repository aRepository(String response) {
        String fullName = read(response, "$.full_name");
        String description = read(response, "$.description");
        String clone_url = read(response, "$.clone_url");
        int stars = read(response, "$.stargazers_count");
        String createdAt = read(response, "$.created_at");
        return new Repository(fullName, description, clone_url, stars, createdAt);
    }

}
