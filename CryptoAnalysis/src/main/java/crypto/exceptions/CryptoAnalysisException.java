package crypto.exceptions;

/**
 * This is an exception that is thrown when something is not working as expected and is explicitly related
 * to the CryptoAnalysis tool.
 *
 */
public class CryptoAnalysisException extends Exception {
	
	private static final long serialVersionUID = -4977113204413613078L;

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message a detail message.
	 */
	public CryptoAnalysisException(String message) {
        super(message);
    }
	
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * @param message a detail message.
	 * @param cause the cause of the exception.
	 */
	public CryptoAnalysisException(String message, Throwable cause) {
		super(message, cause);
	}
}
