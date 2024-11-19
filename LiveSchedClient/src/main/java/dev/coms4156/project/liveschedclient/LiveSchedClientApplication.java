package dev.coms4156.project.liveschedclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class contains the startup logic for the client application.
 */
@SpringBootApplication
public class LiveSchedClientApplication {

  /**
   * The main launcher for the client application.
   * All it does is make a call to the overridden run method.
   *
   * @param args A {@code String[]} of any potential runtime arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(LiveSchedClientApplication.class, args);
  }
}
