package io.camunda.tasklist;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.camunda.tasklist.auth.Authentication;
import io.camunda.tasklist.auth.JwtAuthentication;
import io.camunda.tasklist.auth.JwtCredential;
import io.camunda.tasklist.auth.SimpleAuthentication;
import io.camunda.tasklist.auth.SimpleCredential;
import io.camunda.tasklist.auth.TokenResponseMapper.JacksonTokenResponseMapper;
import io.camunda.tasklist.dto.TaskList;
import io.camunda.tasklist.dto.TaskSearch;
import io.camunda.tasklist.exception.TaskListException;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import wiremock.com.fasterxml.jackson.databind.node.JsonNodeFactory;

@WireMockTest(httpPort = 14682)
public class CamundaTasklistClientTest {
  public static final int PORT = 14682;
  public static final String BASE_URL = "http://localhost:" + PORT;
  private static final String ACCESS_TOKEN =
      JWT.create().withExpiresAt(Instant.now().plusSeconds(300)).sign(Algorithm.none());

  @Test
  public void shouldThrowIfZeebeClientNullAndUseZeebeUserTasks() {
    CamundaTaskListClientProperties properties = new CamundaTaskListClientProperties();
    properties.setUseZeebeUserTasks(true);
    properties.setTaskListUrl(BASE_URL);
    properties.setAuthentication(new MockAuthentication());
    AssertionError assertionError =
        assertThrows(AssertionError.class, () -> new CamundaTaskListClient(properties, null));
    assertEquals("zeebeClient must not be null", assertionError.getMessage());
  }

  @Test
  public void shouldNotThrowIfZeebeClientNullAndNotUseZeebeUserTasks() throws TaskListException {
    CamundaTaskListClientProperties properties = new CamundaTaskListClientProperties();
    properties.setUseZeebeUserTasks(false);
    properties.setTaskListUrl(BASE_URL);
    properties.setAuthentication(new MockAuthentication());
    CamundaTaskListClient client = new CamundaTaskListClient(properties, null);
    assertNotNull(client);
  }

  @Test
  void shouldAuthenticateUsingSimpleAuth() throws MalformedURLException, TaskListException {
    stubFor(
        post("/api/login")
            .withFormParam("username", equalTo("demo"))
            .withFormParam("password", equalTo("demo"))
            .willReturn(
                ok().withHeader("Set-Cookie", "TASKLIST-SESSION=3205A03818447100591792E774DB8AF6")
                    .withHeader(
                        "Set-Cookie",
                        "TASKLIST-X-CSRF-TOKEN=139196d4-7768-451c-aa66-078e1ed74785")));
    stubFor(post("/v1/tasks/search").willReturn(ok().withBody("[]")));
    CamundaTaskListClientProperties properties = new CamundaTaskListClientProperties();
    properties.setTaskListUrl(BASE_URL);
    properties.setAuthentication(
        new SimpleAuthentication(
            new SimpleCredential(
                "demo", "demo", URI.create(BASE_URL).toURL(), Duration.ofMinutes(10))));
    CamundaTaskListClient client = new CamundaTaskListClient(properties, null);
    assertNotNull(client);
    TaskList tasks = client.getTasks(new TaskSearch());
    assertNotNull(tasks);
    assertEquals(0, tasks.size());
  }

  @Test
  void shouldAuthenticateUsingJwt() throws MalformedURLException, TaskListException {
    stubFor(
        post("/token")
            .withFormParam("client_id", equalTo("abc"))
            .withFormParam("client_secret", equalTo("abc"))
            .withFormParam("audience", equalTo("tasklist-api"))
            .withFormParam("grant_type", equalTo("client_credentials"))
            .willReturn(
                ok().withJsonBody(
                        JsonNodeFactory.instance
                            .objectNode()
                            .put("access_token", ACCESS_TOKEN)
                            .put("expires_in", 300))));
    stubFor(
        post("/v1/tasks/search")
            .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
            .willReturn(ok().withBody("[]")));
    CamundaTaskListClientProperties properties = new CamundaTaskListClientProperties();
    properties.setTaskListUrl(BASE_URL);
    properties.setAuthentication(
        new JwtAuthentication(
            new JwtCredential(
                "abc", "abc", "tasklist-api", URI.create(BASE_URL + "/token").toURL(), null),
            new JacksonTokenResponseMapper(new ObjectMapper())));
    CamundaTaskListClient client = new CamundaTaskListClient(properties, null);
    assertNotNull(client);
    TaskList tasks = client.getTasks(new TaskSearch());
    assertNotNull(tasks);
    assertEquals(0, tasks.size());
  }

  private static class MockAuthentication implements Authentication {
    @Override
    public Map<String, String> getTokenHeader() {
      return Map.of("token", "token");
    }

    @Override
    public void resetToken() {}
  }
}
