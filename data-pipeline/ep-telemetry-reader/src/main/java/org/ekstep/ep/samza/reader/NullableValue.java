package org.ekstep.ep.samza.reader;

public class NullableValue<T> {
  private T value;

  NullableValue(T value) {
    this.value = value;
  }

  public T value(){
    return value;
  }

  public boolean isNull(){
    return value == null;
  }
}
