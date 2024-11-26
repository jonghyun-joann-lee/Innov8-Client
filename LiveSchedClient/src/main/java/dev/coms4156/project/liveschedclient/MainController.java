package dev.coms4156.project.liveschedclient;

import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
  
  @Autowired
  private HttpSession session;

  /**
   * Displays the login page if not logged in, otherwise redirects to dashboards.
   *
   * @return A String containing the name of the HTML file to render the login page
   *         or a redirect to the dashboards page.
   */
  @GetMapping("/")
  public String index() {
    if (session.getAttribute("clientId") != null) {
      return "redirect:/dashboards";
    }
    return "index";
  }

  /**
   * Handles the login process by storing the client ID in the session.
   *
   * @param clientId The ID of the client logging in
   * @param redirectAttributes The RedirectAttributes object used to pass data for the next request
   * @return A String redirecting to the dashboards page
   */
  @PostMapping("/login")
  public String login(@RequestParam String clientId, RedirectAttributes redirectAttributes) {
    session.setAttribute("clientId", clientId);
    return "redirect:/dashboards";
  }

  /**
   * Handles the logout process by removing the client ID from the session.
   *
   * @return A String redirecting to the login page
   */
  @GetMapping("/logout")
  public String logout() {
    session.removeAttribute("clientId");
    return "redirect:/";
  }

  /**
   * Displays the dashboard selection page.
   *
   * @param model The Model object used to pass data to the view
   * @return A String containing the name of the HTML file to render the dashboards page
   *         or a redirect to the login page if not logged in
   */
  @GetMapping("/dashboards")
  public String dashboards(Model model) {
    String clientId = (String) session.getAttribute("clientId");
    if (clientId == null) {
      return "redirect:/";
    }
    model.addAttribute("clientId", clientId);
    return "dashboards";
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
  @GetMapping("/taskDashboard")
  public String dashboard(@RequestParam(value = "taskId", required = false) String taskId,
                          @RequestParam(value = "sort", required = false) String sort,
                          Model model) {
    String clientId = (String) session.getAttribute("clientId");
    if (clientId == null) {
      return "redirect:/";
    }

    List<Map<String, Object>> tasks;
    if (taskId != null && !taskId.isBlank()) {
      // Search by taskId
      Map<String, Object> task = liveSchedService.getTaskById(taskId);
      if (task.containsKey("error")) {
        model.addAttribute("message", task.get("error"));
        return "taskDashboard"; // Redirect back to task dashboard with error message
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
    model.addAttribute("clientId", clientId);
    return "taskDashboard";
  }

  /**
   * Displays the details of a specific task.
   *
   * @param taskId The ID of the task to display.
   * @param model  The Model object used to pass data to the view.
   * @return A String containing the name of the HTML file to render the task detail page
   *         or a redirect to the login page if not logged in
   */
  @GetMapping("/task/{taskId}")
  public String taskDetail(@PathVariable String taskId, Model model) {
    String clientId = (String) session.getAttribute("clientId");
    if (clientId == null) {
      return "redirect:/";
    }

    Map<String, Object> task = liveSchedService.getTaskById(taskId);

    if (task.containsKey("error")) {
      model.addAttribute("message", task.get("error"));
      return "dashboard"; // Redirect back to task dashboard with error message
    }

    model.addAttribute("task", task);
    model.addAttribute("clientId", clientId);
    return "taskDetail";
  }

  /**
   * Displays the page for adding a new task.
   *
   * @param model The Model object used to pass data to the view
   * @return A String containing the name of the HTML file to render the add task page
   *         or a redirect to the login page if not logged in
   */
  @GetMapping("/task/add")
  public String addTask(Model model) {
    String clientId = (String) session.getAttribute("clientId");
    if (clientId == null) {
      return "redirect:/";
    }
    model.addAttribute("clientId", clientId);
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
    String clientId = (String) session.getAttribute("clientId");
    if (clientId == null) {
      return "redirect:/";
    }

    Map<String, Object> newTask =
        liveSchedService.addTask(taskName, priority, startTime, endTime, latitude, longitude);
    if (newTask.containsKey("error")) {
      model.addAttribute("message", newTask.get("error"));
      model.addAttribute("clientId", clientId);
      return "addTask"; // Stay on the addTask page with the error message
    }
    return "redirect:/task/" + newTask.get("taskId"); // Redirect to the new task's page
  }

  /**
   * Displays the resource dashboard page with all resource types.
   * Allows searching for resources by type name and sorting by total units.
   *
   * @param typeName Optional. The name of the resource type to search for
   * @param sort Optional. The sort order for resources, either "asc" or "desc"
   * @param model The Model object used to pass data to the view
   * @return A String containing the name of the HTML file to render the resource dashboard
   */
  @GetMapping("/resourceDashboard")
  public String resourceDashboard(@RequestParam(value = "typeName", required = false) 
                                String typeName,
                                @RequestParam(value = "sort", required = false) 
                                String sort, Model model) {
    String clientId = (String) session.getAttribute("clientId");
    if (clientId == null) {
      return "redirect:/";
    }

    List<Map<String, Object>> resources;
    if (typeName != null && !typeName.isBlank()) {
      // Search by typeName
      resources = liveSchedService.getAllResourceTypes(clientId).stream()
              .filter(resource -> resource.get("typeName").toString()
                      .toLowerCase().contains(typeName.toLowerCase()))
              .toList();
      if (resources.isEmpty()) {
        model.addAttribute("message", "No resources found matching: " + typeName);
      }
    } else {
      // Retrieve all resources
      resources = liveSchedService.getAllResourceTypes(clientId);

      // Apply sorting if specified
      if (sort != null) {
        if (sort.equalsIgnoreCase("asc")) {
          resources.sort(Comparator.comparing(
              resource -> (Integer) ((Map<String, Object>) resource).get("totalUnits")));
        } else if (sort.equalsIgnoreCase("desc")) {
          resources.sort(Comparator.comparing(
              resource -> (Integer) ((Map<String, Object>) resource).get("totalUnits"))
              .reversed());
        }
      }
    }

    model.addAttribute("resources", resources);
    model.addAttribute("clientId", clientId);
    model.addAttribute("searchTypeName", typeName); // Maintain search term in form
    return "resourceDashboard";
  }

  /**
   * Displays the page for adding a new resource type.
   *
   * @param model The Model object used to pass data to the view
   * @return A String containing the name of the HTML file to render the add resource page
   */
  @GetMapping("/resource/add")
  public String addResource(Model model) {
    String clientId = (String) session.getAttribute("clientId");
    if (clientId == null) {
      return "redirect:/";
    }
    model.addAttribute("clientId", clientId);
    return "addResource";
  }

  /**
   * Handles the submission of the new resource type form.
   *
   * @param typeName The name of the resource type
   * @param totalUnits The total number of units available
   * @param latitude The latitude of the resource's location
   * @param longitude The longitude of the resource's location
   * @param model The Model object used to pass data to the view
   * @return A String redirecting to the resource dashboard or staying on add resource page if error
   */
  @PostMapping("/resource/add")
  public String addResource(@RequestParam(value = "typeName") String typeName,
                          @RequestParam(value = "totalUnits") int totalUnits,
                          @RequestParam(value = "latitude") double latitude,
                          @RequestParam(value = "longitude") double longitude,
                          Model model) {
    String clientId = (String) session.getAttribute("clientId");
    if (clientId == null) {
      return "redirect:/";
    }

    Map<String, Object> result = 
        liveSchedService.addResourceType(typeName, totalUnits, latitude, longitude, clientId);
    
    if (result.containsKey("error")) {
      model.addAttribute("message", result.get("error"));
      model.addAttribute("clientId", clientId);
      return "addResource";
    }
    return "redirect:/resourceDashboard";
  }

  /**
   * Displays the schedule dashboard page.
   *
   * @param model The Model object used to pass data to the view
   * @return A String containing the name of the HTML file to render the schedule dashboard
   *         or a redirect to the login page if not logged in
   */
  @GetMapping("/scheduleDashboard")
  public String scheduleDashboard(Model model) {
    String clientId = (String) session.getAttribute("clientId");
    if (clientId == null) {
      return "redirect:/";
    }
    model.addAttribute("clientId", clientId);
    return "scheduleDashboard";
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

  /**
   * Handles the modification of a resource for a task.
   *
   * @param taskId            The ID of the task to modify the resource for.
   * @param typeName          The name of the resource type to modify.
   * @param quantity          The new quantity of the resource type.
   * @param redirectAttributes The RedirectAttributes object used to pass data for the next request.
   * @return A String containing the name of the HTML file to render the task detail page.
   */
  @PostMapping("/task/{taskId}/modifyResource")
  public String modifyResource(@PathVariable String taskId,
                               @RequestParam("typeName") String typeName,
                               @RequestParam("quantity") int quantity,
                               RedirectAttributes redirectAttributes) {
    Map<String, Object> response = liveSchedService.modifyResource(taskId, typeName, quantity);
    if (response.containsKey("error")) {
      redirectAttributes.addFlashAttribute("message", response.get("error"));
    }
    return "redirect:/task/" + taskId; // Redirect to the task details page
  }
  
  /**
   * Handles the deletion of a resource type.
   * Verifies the client is logged in, attempts to delete the resource type,
   * and handles any potential errors.
   *
   * @param typeName The name of the resource type to be deleted
   * @param redirectAttributes The RedirectAttributes object used to pass 
   *                          flash messages to the next request
   * @return A String containing the redirect path:
   *         - To login page if client is not logged in
   *         - To resource dashboard after deletion attempt, with appropriate message
   */
  @PostMapping("/resource/delete")
  public String deleteResource(@RequestParam(value = "typeName") String typeName,
                            RedirectAttributes redirectAttributes) {
    String clientId = (String) session.getAttribute("clientId");
    if (clientId == null) {
      return "redirect:/";
    }

    Map<String, Object> result = liveSchedService.deleteResourceType(typeName, clientId);
    if (result.containsKey("error")) {
      redirectAttributes.addFlashAttribute("message", result.get("error"));
    } else {
      redirectAttributes.addFlashAttribute("message", "Resource type deleted successfully");
    }
    return "redirect:/resourceDashboard";
  }

}
