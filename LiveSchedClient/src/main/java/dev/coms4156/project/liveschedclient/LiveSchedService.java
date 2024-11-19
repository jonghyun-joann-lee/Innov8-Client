package dev.coms4156.project.liveschedclient;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * LiveSchedService handles API interactions between the client application and the service.
 */
@Service
public class LiveSchedService {

  private final RestTemplate restTemplate;
  private static final String BASE_URL = "https://innov8-livesched.ue.r.appspot.com";

  /**
   * Constructor to initialize the RestTemplate for making HTTP requests.
   *
   * @param restTemplateBuilder A builder for creating RestTemplate instances.
   */
  public LiveSchedService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  /**
   * Pings the server's index endpoint to establish connection.
   */
  public void pingServer() {
    restTemplate.getForEntity(BASE_URL + "/index", String.class);
  }

  /**
   * Retrieves all tasks from the server's retrieveTasks endpoint.
   *
   * @return A message indicating the response from the server.
   */
  public String getAllTasks() {
    try {
      ResponseEntity<String> response = restTemplate.getForEntity(
          BASE_URL + "/retrieveTasks", String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      } else {
        return "Unexpected response status: " + response.getStatusCode();
      }

    } catch (HttpClientErrorException.NotFound e) {
      return "No tasks found.";
    } catch (RestClientException e) {
      return "Error connecting to the service: " + e.getMessage();
    }
  }
}
