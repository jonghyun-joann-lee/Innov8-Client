package dev.coms4156.project.liveschedclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
  @GetMapping({"/dashboard"})
  public String dashboard(Model model) {
    String tasks = liveSchedService.getAllTasks();
    model.addAttribute("tasks", tasks);
    return "dashboard";
  }

}
