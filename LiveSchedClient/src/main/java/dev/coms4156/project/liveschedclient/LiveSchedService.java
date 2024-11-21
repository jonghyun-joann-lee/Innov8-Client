package dev.coms4156.project.liveschedclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
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

  // Change BASE_URL to "https://innov8-livesched.ue.r.appspot.com" after deployment;
  private static final String BASE_URL = "http://localhost:8080";


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
  public List<Map<String, Object>> getAllTasks() {
    try {
      ResponseEntity<String> response = restTemplate.getForEntity(
          BASE_URL + "/retrieveTasks", String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
            response.getBody(), new TypeReference<List<Map<String, Object>>>() {});
      } else {
        return List.of(Map.of(
            "error", "Unexpected response status: " + response.getStatusCode()));
      }
    } catch (HttpClientErrorException.NotFound e) {
      return List.of(); // Return empty list if no tasks found
    } catch (JsonProcessingException e) {
      return List.of(Map.of("error", "Failed to parse JSON response."));
    } catch (RestClientException e) {
      return List.of(Map.of("error", "Error connecting to the service."));
    }
  }

  /**
   * Retrieves a specific task by its ID from the server's retrieveTask endpoint.
   *
   * @param taskId A {@code String} representing the ID of the task to retrieve.
   *
   * @return A message indicating the response from the server.
   */
  public Map<String, Object> getTaskById(String taskId) {
    try {
      ResponseEntity<String> response = restTemplate.getForEntity(
          BASE_URL + "/retrieveTask?taskId=" + taskId, String.class
      );

      if (response.getStatusCode().is2xxSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.getBody(), new TypeReference<>() {});
      } else {
        return Map.of("error", "Unexpected response status: " + response.getStatusCode());
      }
    } catch (HttpClientErrorException.NotFound e) {
      return Map.of("error", "Task not found.");
    } catch (JsonProcessingException e) {
      return Map.of("error", "Failed to parse JSON response.");
    } catch (RestClientException e) {
      return Map.of("error", "Error connecting to the service.");
    }
  }

}
