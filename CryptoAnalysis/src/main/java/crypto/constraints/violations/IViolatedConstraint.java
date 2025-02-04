package crypto.constraints.violations;

/**
 * Super class for all violated constraints. A subclass has to provide a description of the
 * violation that is included in the reports.
 */
public interface IViolatedConstraint {

    /**
     * Error message for the violated constraint that is included in the reports
     *
     * @return the error message
     */
    String getErrorMessage();
}
