package net.hunnor.dict.client.service;

public class ServiceException extends Exception {

  private static final long serialVersionUID = 8624769933410231739L;

  public ServiceException() {
    super();
  }

  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }

}
