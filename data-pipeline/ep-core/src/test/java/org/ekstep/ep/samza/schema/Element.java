package org.ekstep.ep.samza.schema;

public class Element {
    private String id;
    private Object value;

    public Element(String id, Object value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Element other = (Element) obj;
        return id.equalsIgnoreCase(other.id) && value.equals(other.value);
    }
}
