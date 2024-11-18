package dev.coms4156.project.liveschedclient;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MainController is responsible for handling the routing logic
 * and making calls to the service API.
 */
@Controller
public class MainController {

  /**
   * Redirects to the homepage.
   *
   * @return A String containing the name of the html file to be loaded.
   */
  @GetMapping({"/", "/index", "/home"})
  public String index() {
    return "index";
  }

}
