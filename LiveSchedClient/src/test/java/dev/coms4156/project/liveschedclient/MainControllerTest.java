package dev.coms4156.project.liveschedclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MainController.class)
class MainControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LiveSchedService liveSchedService;

  private MockHttpSession session;

  private static final String TEST_CLIENT_ID = "testClient123";

  @BeforeEach
  void setUp() {
    session = new MockHttpSession();
    doNothing().when(liveSchedService).pingServer();
  }

  @Test
  void indexPageShowsLoginWhenNotLoggedIn() throws Exception {
    mockMvc.perform(get("/")
            .session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));
  }

  @Test
  void indexRedirectsToDashboardsWhenLoggedIn() throws Exception {
    session.setAttribute("clientId", TEST_CLIENT_ID);
    
    mockMvc.perform(get("/")
            .session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/dashboards"));
  }

  @Test
  void loginSuccessfullyRedirectsToDashboard() throws Exception {
    mockMvc.perform(post("/login")
            .session(session)
            .param("clientId", TEST_CLIENT_ID))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/dashboards"));

    assertEquals(TEST_CLIENT_ID, session.getAttribute("clientId"));
  }

  @Test
  void logoutClearsSessionAndRedirectsToIndex() throws Exception {
    session.setAttribute("clientId", TEST_CLIENT_ID);
    
    mockMvc.perform(get("/logout")
            .session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));

    assertNull(session.getAttribute("clientId"));
  }

  @Test
  void taskDashboardRequiresLogin() throws Exception {
    mockMvc.perform(get("/taskDashboard")
            .session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
  }

  @Test
  void taskDashboardShowsTasksWhenLoggedIn() throws Exception {
    // Setup
    session.setAttribute("clientId", TEST_CLIENT_ID);
    List<Map<String, Object>> mockTasks = new ArrayList<>();
    Map<String, Object> task = new HashMap<>();
    task.put("taskId", "task1");
    task.put("name", "Test Task");
    mockTasks.add(task);
    
    when(liveSchedService.getAllTasks(TEST_CLIENT_ID)).thenReturn(mockTasks);

    mockMvc.perform(get("/taskDashboard")
            .session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("taskDashboard"))
            .andExpect(model().attribute("tasks", mockTasks));
  }

  @Test
  void addTaskSuccessfullyCreatesNewTask() throws Exception {
    // Setup
    session.setAttribute("clientId", TEST_CLIENT_ID);
    Map<String, Object> mockResponse = new HashMap<>();
    mockResponse.put("taskId", "newTask123");
    
    when(liveSchedService.addTask(
        anyString(), anyInt(), anyString(), anyString(), 
        anyDouble(), anyDouble(), anyString()
    )).thenReturn(mockResponse);

    mockMvc.perform(post("/task/add")
            .session(session)
            .param("taskName", "New Task")
            .param("priority", "3")
            .param("startTime", "2024-01-01 10:00")
            .param("endTime", "2024-01-01 11:00")
            .param("latitude", "40.7128")
            .param("longitude", "-74.0060"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/task/newTask123"));
  }

  @Test
  void addTaskShowsErrorOnInvalidInput() throws Exception {
    // Setup
    session.setAttribute("clientId", TEST_CLIENT_ID);
    Map<String, Object> mockResponse = new HashMap<>();
    mockResponse.put("error", "Invalid task data");
    
    when(liveSchedService.addTask(
        anyString(), anyInt(), anyString(), anyString(), 
        anyDouble(), anyDouble(), anyString()
    )).thenReturn(mockResponse);

    mockMvc.perform(post("/task/add")
            .session(session)
            .param("taskName", "New Task")
            .param("priority", "3")
            .param("startTime", "2024-01-01 10:00")
            .param("endTime", "2024-01-01 11:00")
            .param("latitude", "40.7128")
            .param("longitude", "-74.0060"))
            .andExpect(status().isOk())
            .andExpect(view().name("addTask"))
            .andExpect(model().attributeExists("message"));
  }

  @Test
  void deleteTaskSuccessfullyRemovesTask() throws Exception {
    // Setup
    session.setAttribute("clientId", TEST_CLIENT_ID);
    Map<String, Object> mockResponse = new HashMap<>();
    mockResponse.put("message", "Task deleted successfully");
    
    when(liveSchedService.deleteTask(anyString(), anyString()))
            .thenReturn(mockResponse);

    mockMvc.perform(post("/task/123/delete")
            .session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/taskDashboard"));
  }

}