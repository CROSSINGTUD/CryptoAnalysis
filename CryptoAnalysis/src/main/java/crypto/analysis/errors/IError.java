package crypto.analysis.errors;

public interface IError {
	
	public void accept(ErrorVisitor visitor);

}
