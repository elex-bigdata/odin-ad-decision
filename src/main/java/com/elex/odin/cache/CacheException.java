package com.elex.odin.cache;

public class CacheException extends Exception {
  public CacheException() {
  }

  public CacheException(String message) {
    super(message);
  }

  public CacheException(String message, Throwable cause) {
    super(message, cause);
  }

  public CacheException(Throwable cause) {
    super(cause);
  }
}