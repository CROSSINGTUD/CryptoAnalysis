package crypto.exceptions;

public class CryptoAnalysisException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public CryptoAnalysisException(String message) {
        super(message);
    }
	
	public CryptoAnalysisException(String message, Throwable cause) {
		super(message, cause);
	}
}
