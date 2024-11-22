package dev.coms4156.project.liveschedclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  /**
   * Constructor to initialize the RestTemplate for making HTTP requests.
   *
   * @param restTemplateBuilder A builder for creating RestTemplate instances.
   */
  public LiveSchedService(RestTemplateBuilder restTemplateBuilder) {
    // Use Apache HttpClient for PATCH requests
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory(httpClient);

    this.restTemplate = restTemplateBuilder
        .requestFactory(() -> requestFactory)
        .build();
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
   * @return A list of map containing the response from the server.
   */
  public List<Map<String, Object>> getAllTasks() {
    try {
      ResponseEntity<String> response = restTemplate.getForEntity(
          BASE_URL + "/retrieveTasks", String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> tasks = mapper.readValue(
            response.getBody(), new TypeReference<List<Map<String, Object>>>() {});

        // Format startTime and endTime for each task
        for (Map<String, Object> task : tasks) {
          formatTime(task);
        }

        return tasks;
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
   * @return A map containing the response from the server.
   */
  public Map<String, Object> getTaskById(String taskId) {
    try {
      ResponseEntity<String> response = restTemplate.getForEntity(
          BASE_URL + "/retrieveTask?taskId=" + taskId, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> task = mapper.readValue(response.getBody(), new TypeReference<>() {});
        formatTime(task);
        return task;
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

  /**
   * Helper method to format the timestamps in a task map.
   *
   * @param task A map containing task details, including startTime and endTime.
   */
  private void formatTime(Map<String, Object> task) {
    String startTime = (String) task.get("startTime");
    String endTime = (String) task.get("endTime");

    if (startTime != null) {
      task.put("startTime", LocalDateTime.parse(startTime).format(FORMATTER));
    }
    if (endTime != null) {
      task.put("endTime", LocalDateTime.parse(endTime).format(FORMATTER));
    }
  }

  /**
   * Adds a new task to the server's database through the addTask endpoint.
   *
   * @param taskName  The name of the task.
   * @param priority  The priority of the task (1-5).
   * @param startTime The start time of the task in the format "yyyy-MM-dd HH:mm".
   * @param endTime   The end time of the task in the format "yyyy-MM-dd HH:mm".
   * @param latitude  The latitude of the task's location.
   * @param longitude The longitude of the task's location.
   *
   * @return A map containing the response from the server.
   */
  public Map<String, Object> addTask(String taskName, int priority, String startTime,
                                     String endTime, double latitude, double longitude) {
    try {
      ResponseEntity<String> response = restTemplate.exchange(
          BASE_URL + "/addTask?taskName=" + taskName
              + "&priority=" + priority
              + "&startTime=" + startTime
              + "&endTime=" + endTime
              + "&latitude=" + latitude
              + "&longitude=" + longitude,
          HttpMethod.PATCH, null, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.getBody(), new TypeReference<>() {});
      } else {
        return Map.of("error", "Unexpected response status: " + response.getStatusCode());
      }
    } catch (Exception e) {
      return Map.of("error", "Failed to add task");
    }
  }

  /**
   * Deletes a task from the server's database through the deleteTask endpoint.
   *
   * @param taskId The ID of the task to be deleted.
   *
   * @return A map containing the response from the server.
   */
  public Map<String, Object> deleteTask(String taskId) {
    try {
      ResponseEntity<String> response = restTemplate.exchange(
          BASE_URL + "/deleteTask?taskId=" + taskId,
          HttpMethod.DELETE, null, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return Map.of("message", response.getBody());
      } else {
        return Map.of("error", "Unexpected response status: " + response.getStatusCode());
      }
    } catch (Exception e) {
      return Map.of("error", "Failed to delete task");
    }
  }

}
