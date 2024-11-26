package dev.coms4156.project.liveschedclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

  /**
   * Retrieves all resource types from the server's database.
   *
   * @param clientId The ID of the client retrieving the resources
   * @return A list of maps containing the resource types
   */
  public List<Map<String, Object>> getAllResourceTypes(String clientId) {
    try {
        ResponseEntity<String> response = restTemplate.getForEntity(
            BASE_URL + "/retrieveResourceTypes?clientId=" + clientId, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), new TypeReference<List<Map<String, Object>>>() {});
        } else {
            return List.of(Map.of("error", "Unexpected response status: " + response.getStatusCode()));
        }
    } catch (HttpClientErrorException.NotFound e) {
        return List.of(); // Return empty list if no resources found
    } catch (JsonProcessingException e) {
        return List.of(Map.of("error", "Failed to parse JSON response."));
    } catch (RestClientException e) {
        return List.of(Map.of("error", "Error connecting to the service."));
    }
  }

  /**
  * Adds a new resource type to the server's database.
  *
  * @param typeName The name of the resource type
  * @param totalUnits The total number of units available
  * @param latitude The latitude of the resource's location
  * @param longitude The longitude of the resource's location
  * @param clientId The ID of the client adding the resource
  * @return A map containing the response from the server
  */
  public Map<String, Object> addResourceType(String typeName, int totalUnits, 
                                        double latitude, double longitude, String clientId) {
    try {
        ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL + "/addResourceType?typeName=" + typeName
                + "&totalUnits=" + totalUnits
                + "&latitude=" + latitude
                + "&longitude=" + longitude
                + "&clientId=" + clientId,
            HttpMethod.PATCH, null, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return Map.of("message", "Resource type added successfully");
        } else {
            return Map.of("error", "Unexpected response status: " + response.getStatusCode());
        }
    } catch (Exception e) {
        return Map.of("error", "Failed to add resource type");
    }
  }
  /**
   * Modifies the quantity of a resource type for a task through the server's
   * modifyResourceType endpoint.
   *
   * @param taskId   The ID of the task to modify the resource for.
   * @param typeName The name of the resource type to modify.
   * @param quantity The new quantity of the resource type.
   * @return A map containing the response from the server.
   */
  public Map<String, Object> modifyResource(String taskId, String typeName, int quantity) {
    try {
      ResponseEntity<String> response = restTemplate.exchange(
          BASE_URL + "/modifyResourceType?taskId=" + taskId
              + "&typeName=" + typeName
              + "&quantity=" + quantity,
          HttpMethod.PATCH, null, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return Map.of("message", response.getBody());
      } else {
        return Map.of("error", "Unexpected response status: " + response.getStatusCode());
      }
    } catch (Exception e) {
      return Map.of("error", "Failed to modify resource");
    }
  }
  
  /**
   * Deletes a resource type from the server's database through the deleteResourceType endpoint.
   * The resource type cannot be deleted if it is currently being used by any tasks.
   *
   * @param typeName A {@code String} representing the name of the resource type to delete
   * @param clientId A {@code String} representing the ID of the client deleting the resource
   * @return A map containing either:
   *         - A success message if deletion was successful
   *         - An error message if:
   *           - The resource type is in use by tasks
   *           - The resource type was not found
   *           - An unexpected error occurred during deletion
   */
  public Map<String, Object> deleteResourceType(String typeName, String clientId) {
      try {
          ResponseEntity<String> response = restTemplate.exchange(
              BASE_URL + "/deleteResourceType?typeName=" + typeName
                  + "&clientId=" + clientId,
              HttpMethod.DELETE, null, String.class);

          if (response.getStatusCode().is2xxSuccessful()) {
              return Map.of("message", "Resource type deleted successfully");
          } else {
              return Map.of("error", "Unexpected response status: " + response.getStatusCode());
          }
      } catch (HttpClientErrorException.BadRequest e) {
          return Map.of("error", "Cannot delete a resource type that is currently in use");
      } catch (HttpClientErrorException.NotFound e) {
          return Map.of("error", "Resource type not found");
      } catch (Exception e) {
          return Map.of("error", "Failed to delete resource type");
      }
  }

}
