package bel.kozik.github.service;


import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

/**
 * Unit tests for Github Service.
 * Created by Anton Kozik on 28.05.16.
 */
public class GitHubServiceTest {

    @Test
    public void testFindRepository() throws IOException, ExecutionException, InterruptedException {
        // given
        String owner = "vaadin";
        String repo = "vaadin";
        GitHubService service = new GitHubService();
        // when
        String response = service.findRepository(owner, repo);
        // then
        assertNotNull(response);
        assertEquals(JsonPath.read(response, "$.fullName"), "vaadin/vaadin");
    }

}