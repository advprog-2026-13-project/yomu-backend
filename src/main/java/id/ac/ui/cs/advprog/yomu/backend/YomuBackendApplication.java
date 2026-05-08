package id.ac.ui.cs.advprog.yomu.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {
      "id.ac.ui.cs.advprog.yomu.backend",
      "id.ac.ui.cs.advprog.yomu.backend.auth" // Add the auth package here
    })
public class YomuBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(YomuBackendApplication.class, args);
  }
}
