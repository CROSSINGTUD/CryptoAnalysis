package crypto.rules;

/**
 * Helper Class to store an {@link Exception} as a String.
 */
public class CrySLException  {
	

	private final String exception;

	/**
	 * Construct a {@link CrySLException} from the fully qualified classname
	 * of the {@link Exception} to store.
	 */
	public CrySLException(String exception) {
		this.exception = exception;
	}

	/**
	 * @return The fully qualified classname of the stored {@link Exception}.
	 */
	public String getException() {
		return exception;
	}

	public String toString() {
		return String.format("%s(%s)", this.getClass().getName(), this.exception);
	}
}
