package dev.coms4156.project.liveschedclient;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
   * Displays the homepage.
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
   * Displays the task dashboard page.
   * Allows searching for a task by its ID and sorting tasks by priority.
   *
   * @param taskId Optional.   The ID of the task to search for.
   * @param sort Optional.     The sort order for tasks, either {@code "asc"} or {@code "desc"}.
   * @param model              The Model object used to pass data to the view.
   * @return A String containing the name of the HTML file to render the dashboard.
   */
  @GetMapping("/dashboard")
  public String dashboard(@RequestParam(value = "taskId", required = false) String taskId,
                          @RequestParam(value = "sort", required = false) String sort,
                          Model model) {
    List<Map<String, Object>> tasks;

    if (taskId != null && !taskId.isBlank()) {
      // Search by taskId
      Map<String, Object> task = liveSchedService.getTaskById(taskId);
      if (task.containsKey("error")) {
        model.addAttribute("message", task.get("error"));
        return "dashboard"; // Redirect back to task dashboard with error message
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

  /**
   * Displays the details of a specific task.
   *
   * @param taskId The ID of the task to display.
   * @param model  The Model object used to pass data to the view.
   * @return A String containing the name of the HTML file to render the task detail page.
   */
  @GetMapping("/task/{taskId}")
  public String taskDetail(@PathVariable String taskId, Model model) {
    Map<String, Object> task = liveSchedService.getTaskById(taskId);

    if (task.containsKey("error")) {
      model.addAttribute("message", task.get("error"));
      return "dashboard"; // Redirect back to task dashboard with error message
    }

    model.addAttribute("task", task);
    return "taskDetail";
  }

  /**
   * Displays the page for adding a new task.
   *
   * @return A String containing the name of the HTML file to render the add task page.
   */
  @GetMapping("/task/add")
  public String addTask() {
    return "addTask";
  }

  /**
   * Handles the submission of the new task form.
   *
   * @param taskName  The name of the task.
   * @param priority  The priority of the task (1-5).
   * @param startTime The start time of the task in the format "yyyy-MM-dd HH:mm".
   * @param endTime   The end time of the task in the format "yyyy-MM-dd HH:mm".
   * @param latitude  The latitude of the task's location.
   * @param longitude The longitude of the task's location.
   * @param model     The Model object used to pass data to the view.
   * @return A String containing the name of the HTML file to render:
   *         - If the task is added successfully, redirects to the new task's detail page.
   *         - If an error occurs, returns to the add task page with the error message.
   */
  @PostMapping("/task/add")
  public String addTask(@RequestParam(value = "taskName") String taskName,
                        @RequestParam(value = "priority") int priority,
                        @RequestParam(value = "startTime") String startTime,
                        @RequestParam(value = "endTime") String endTime,
                        @RequestParam(value = "latitude") double latitude,
                        @RequestParam(value = "longitude") double longitude,
                        Model model) {
    Map<String, Object> newTask =
        liveSchedService.addTask(taskName, priority, startTime, endTime, latitude, longitude);
    if (newTask.containsKey("error")) {
      model.addAttribute("message", newTask.get("error"));
      return "addTask"; // Stay on the addTask page with the error message
    }
    return "redirect:/task/" + newTask.get("taskId"); // Redirect to the new task's page
  }

  /**
   * Handles the deletion of a task by its ID.
   *
   * @param taskId The ID of the task to be deleted.
   * @param model  The Model object used to pass data to the view.
   * @return A String containing the name of the HTML file to render:
   *         - If the task is deleted successfully, redirects to the task dashboard.
   *         - If an error occurs, stays on the task detail page with an error message.
   */
  @PostMapping("/task/{taskId}/delete")
  public String deleteTask(@PathVariable String taskId, Model model) {
    Map<String, Object> response = liveSchedService.deleteTask(taskId);
    if (response.containsKey("error")) {
      model.addAttribute("message", response.get("error"));
      return "taskDetail"; // Stay on the task page with the error message
    }
    return "redirect:/dashboard"; // Redirect to the dashboard after successful deletion
  }

}
