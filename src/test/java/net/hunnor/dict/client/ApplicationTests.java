package net.hunnor.dict.client;

import org.junit.Test;

public class ApplicationTests {

  private static final String SERVER_PORT = "server.port";

  @Test
  public void contextLoads() {

    String port = System.getProperty(SERVER_PORT);
    System.setProperty(SERVER_PORT, "0");

    Application.main(new String[] {});

    if (port == null) {
      System.clearProperty("server.port");
    } else {
      System.setProperty(SERVER_PORT, port);
    }

  }

}
