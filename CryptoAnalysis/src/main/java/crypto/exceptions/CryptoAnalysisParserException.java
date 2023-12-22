package crypto.exceptions;

public class CryptoAnalysisParserException extends CryptoAnalysisException {

	private static final long serialVersionUID = 5931419586323153592L;

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message a detail message.
	 */
	public CryptoAnalysisParserException(String message) {
		super(message);
	}
	
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * @param message a detail message.
	 * @param cause the cause of the exception.
	 */
	public CryptoAnalysisParserException(String message, Throwable cause) {
		super(message, cause);
	}

}
