package org.ekstep.ep.samza.validators;

public interface IValidator {
    public Boolean isInvalid();
    public String getErrorMessage();
}
