package net.hunnor.dict.client.service;

import org.junit.Test;

public class ServiceExceptionTest {

  @Test(expected = ServiceException.class)
  public void testException() throws ServiceException {
    throw new ServiceException();
  }

}
