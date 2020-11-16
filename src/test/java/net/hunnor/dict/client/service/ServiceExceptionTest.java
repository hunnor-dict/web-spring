package net.hunnor.dict.client.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ServiceExceptionTest {

  @Test
  public void testException() throws ServiceException {
    assertThrows(ServiceException.class, () -> {
      throw new ServiceException();
    });
  }

}
