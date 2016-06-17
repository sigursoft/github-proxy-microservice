package bel.kozik.github;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;
/**
 *
 * App Integration Tests.
 *
 * Created by Anton Kozik on 27.05.16.
 */
public class AppIntegrationTest {


    @Before
    public void setup() {
        System.setProperty("PORT", "9080");
        try {
            App.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        App.stop();
    }

    @Test
    public void sendInvalidRequestAndGetResponseCode500() throws Exception {
        // given
        String url = "http://localhost:9080/repositories";
        // when
        int response = sendRequest(url);
        // then
        assertEquals(500, response);
    }

    @Test
    public void sendInvalidRequestWithOnlyOwnerAndGetResponseCode500() throws Exception {
        // given
        String url = "http://localhost:9080/repositories/vaadin";
        // when
        int response = sendRequest(url);
        // then
        assertEquals(500, response);
    }

    @Test
    public void sendValidRequestAndGetResponseCode200() throws Exception {
        // given
        String url = "http://localhost:9080/repositories/vaadin/vaadin";
        // when
        int response = sendRequest(url);
        // then
        assertEquals(200, response);
    }

    @Test
    public void sendValidRequestWithAdditionalPathElementAndGetResponseCode200() throws Exception {
        // given
        String url = "http://localhost:9080/repositories/vaadin/vaadin/stars";
        // when
        int response = sendRequest(url);
        // then
        assertEquals(200, response);
    }


    private int sendRequest(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
        connection.setRequestMethod("GET");
        int response = connection.getResponseCode();
        connection.disconnect();
        return response;
    }

}
