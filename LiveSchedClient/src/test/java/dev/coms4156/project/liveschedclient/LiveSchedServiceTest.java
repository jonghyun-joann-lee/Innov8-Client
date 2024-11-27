package dev.coms4156.project.liveschedclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class LiveSchedServiceTest {

  private LiveSchedService liveSchedService;
  private RestTemplate restTemplate;
  private static final String TEST_CLIENT_ID = "testClient123";

  @BeforeEach
  void setUp() {
    restTemplate = mock(RestTemplate.class);
    RestTemplateBuilder builder = mock(RestTemplateBuilder.class);
    when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);
    when(builder.build()).thenReturn(restTemplate);
    liveSchedService = new LiveSchedService(builder);
  }

  @Test
  void addTask_Success() {
    // Prepare test data
    Map<String, Object> expectedResponse = Map.of("taskId", "newTask123");
    when(restTemplate.exchange(
        contains("/addTask"),
        eq(HttpMethod.PATCH),
        isNull(),
        eq(String.class)
    )).thenReturn(new ResponseEntity<>("""
        {"taskId": "newTask123"}
        """, HttpStatus.OK));

    // Execute test
    Map<String, Object> result = liveSchedService.addTask(
        "New Task", 3, "2024-01-01 10:00",
        "2024-01-01 11:00", 40.7128, -74.0060, TEST_CLIENT_ID
    );

    // Verify results
    assertNotNull(result);
    assertEquals("newTask123", result.get("taskId"));
  }

  @Test
  void deleteTask_Success() {
    // Prepare test data
    when(restTemplate.exchange(
        contains("/deleteTask"),
        eq(HttpMethod.DELETE),
        isNull(),
        eq(String.class)
    )).thenReturn(new ResponseEntity<>("Task deleted successfully", HttpStatus.OK));

    // Execute test
    Map<String, Object> result = liveSchedService.deleteTask("task123", TEST_CLIENT_ID);

    // Verify results
    assertNotNull(result);
    assertEquals("Task deleted successfully", result.get("message"));
  }

  @Test
  void getTaskById_ConnectionError() {
    // Prepare test data
    when(restTemplate.getForEntity(anyString(), eq(String.class)))
        .thenThrow(new RestClientException("Connection error"));

    // Execute test
    Map<String, Object> result = liveSchedService.getTaskById("task1", TEST_CLIENT_ID);

    // Verify results
    assertNotNull(result);
    assertEquals("Error connecting to the service.", result.get("error"));
  }

  @Test
  void getAllResourceTypes_Success() {
    // Prepare test data
    String jsonResponse = """
        [
            {
                "typeName": "Laptop",
                "totalUnits": 10,
                "latitude": 40.7128,
                "longitude": -74.0060
            }
        ]
        """;
    when(restTemplate.getForEntity(
        contains("/retrieveResourceTypes"),
        eq(String.class)
    )).thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));

    // Execute test
    List<Map<String, Object>> result = liveSchedService.getAllResourceTypes(TEST_CLIENT_ID);

    // Verify results
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Laptop", result.get(0).get("typeName"));
    assertEquals(10, result.get(0).get("totalUnits"));
  }

  @Test
  void addResourceType_Success() {
    // Prepare test data
    when(restTemplate.exchange(
        contains("/addResourceType"),
        eq(HttpMethod.PATCH),
        isNull(),
        eq(String.class)
    )).thenReturn(new ResponseEntity<>("Resource type added successfully", HttpStatus.OK));

    // Execute test
    Map<String, Object> result = liveSchedService.addResourceType(
        "Printer", 5, 40.7128, -74.0060, TEST_CLIENT_ID
    );

    // Verify results
    assertNotNull(result);
    assertEquals("Resource type added successfully", result.get("message"));
  }

  @Test
  void modifyResource_Success() {
    // Prepare test data
    when(restTemplate.exchange(
        contains("/modifyResourceType"),
        eq(HttpMethod.PATCH),
        isNull(),
        eq(String.class)
    )).thenReturn(new ResponseEntity<>("Resource modified successfully", HttpStatus.OK));

    // Execute test
    Map<String, Object> result = liveSchedService.modifyResource(
        "task123", "Laptop", 2, TEST_CLIENT_ID
    );

    // Verify results
    assertNotNull(result);
    assertEquals("Resource modified successfully", result.get("message"));
  }

  @Test
  void unscheduleTask_Success() {
    // Prepare test data
    when(restTemplate.exchange(
        contains("/unscheduleTask"),
        eq(HttpMethod.PATCH),
        isNull(),
        eq(String.class)
    )).thenReturn(new ResponseEntity<>("Task unscheduled successfully", HttpStatus.OK));

    // Execute test
    Map<String, Object> result = liveSchedService.unscheduleTask("task123", TEST_CLIENT_ID);

    // Verify results
    assertNotNull(result);
    assertEquals("Task unscheduled successfully", result.get("message"));
  }

}