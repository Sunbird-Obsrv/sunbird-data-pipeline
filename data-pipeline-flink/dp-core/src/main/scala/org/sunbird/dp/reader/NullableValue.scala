package org.sunbird.dp.reader

class NullableValue[T](values: T) {
  def value: T = values

  def isNull: Boolean = values.equals(null)

  def valueOrDefault(defaultValue: T): T = {
    if (isNull) return defaultValue
    value
  }

  override def toString: String = "NullableValue{" + "value=" + value + '}'

  override def equals(o: Any): Boolean = {
    if (this.equals(o)) return true
    if (o == null || (getClass ne o.getClass)) return false
    val that = o.asInstanceOf[NullableValue[_]]
    if (value != null) value == that.value
    else that.value == null
  }

  override def hashCode: Int = if (value != null) value.hashCode
  else 0
}
