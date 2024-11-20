package dev.coms4156.project.liveschedclient;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * MainController handles routing logic for the LiveSched client application.
 */
@Controller
public class MainController {

  private final LiveSchedService liveSchedService;

  /**
   * Constructor to initialize the controller with LiveSchedService.
   *
   * @param liveSchedService The service class used for API interactions with the LiveSched server.
   */
  @Autowired
  public MainController(LiveSchedService liveSchedService) {
    this.liveSchedService = liveSchedService;
  }

  /**
   * Handles requests to the homepage.
   * Calls the server's index API to establish a connection.
   *
   * @return A String containing the name of the HTML file to render the homepage.
   */
  @GetMapping({"/", "/index", "/home"})
  public String index() {
    liveSchedService.pingServer();
    return "index";
  }

  /**
   * Handles requests to the dashboard page.
   * Retrieves tasks from the LiveSched service and adds them to the model for rendering.
   *
   * @param model The Model object used to pass data to the view.
   * @return A String containing the name of the HTML file to render the dashboard.
   */
  @GetMapping("/dashboard")
  public String dashboard(
      @RequestParam(value = "taskId", required = false) String taskId,
      @RequestParam(value = "sort", required = false) String sort,
      Model model
  ) {
    List<Map<String, Object>> tasks;

    if (taskId != null && !taskId.isBlank()) {
      // Search by taskId
      Map<String, Object> task = liveSchedService.getTaskById(taskId);
      if (task.containsKey("error")) {
        model.addAttribute("message", task.get("error"));
        return "dashboard";
      } else {
        tasks = List.of(task);
      }
    } else {
      // Retrieve all tasks
      tasks = liveSchedService.getAllTasks();

      // Apply sorting if specified
      if (sort != null) {
        if (sort.equalsIgnoreCase("asc")) {
          tasks.sort(Comparator.comparing(
              task -> (Integer) ((Map<String, Object>) task).get("priority")));
        } else if (sort.equalsIgnoreCase("desc")) {
          tasks.sort(Comparator.comparing(
              task -> (Integer) ((Map<String, Object>) task).get("priority")).reversed());
        }
      }
    }

    model.addAttribute("tasks", tasks);
    return "dashboard";
  }

}
