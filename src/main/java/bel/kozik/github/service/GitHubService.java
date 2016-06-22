package bel.kozik.github.service;

import bel.kozik.github.domain.Repository;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import static com.jayway.jsonpath.JsonPath.read;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * GitHub Service is responsible for sending requests to GitHub API and returning results.
 * <p>
 * Created by Anton Kozik on 27.05.16.
 */
public class GitHubService {

    private static final String GIT_HUB_URL_PATTERN = "https://api.github.com/repos/%s/%s";

    private final Cache<String, Repository> cache;

    private final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

    public GitHubService(String config) throws IOException {
        this.cache = new DefaultCacheManager(config).getCache("repos");
        this.cm.setMaxTotal(100);
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
            HttpGet request = new HttpGet(url);
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(cm).build();
            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    try (Scanner s = new Scanner(response.getEntity().getContent(), UTF_8.toString()).useDelimiter("\\A")) {
                        responseFromGitHub = s.hasNext() ? s.next() : "";
                    }
                }
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
