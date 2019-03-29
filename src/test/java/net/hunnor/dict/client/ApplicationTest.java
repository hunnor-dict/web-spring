package net.hunnor.dict.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ApplicationTest {

  private static final String SERVER_PORT = "server.port";

  @Test
  public void contextLoads() {

    String port = System.getProperty(SERVER_PORT);
    System.setProperty(SERVER_PORT, "0");

    Application.main(new String[] {});

    if (port == null) {
      System.clearProperty("server.port");
      assertNull(System.getProperty(SERVER_PORT));
    } else {
      System.setProperty(SERVER_PORT, port);
      assertEquals(port, System.getProperty(SERVER_PORT));
    }

  }

}
