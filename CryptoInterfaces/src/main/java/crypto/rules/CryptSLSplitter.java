package crypto.rules;

import java.io.Serializable;

public class CryptSLSplitter implements Serializable {

	private int index = 0;
	private String split = "";
	
	public CryptSLSplitter(int ind, String spl) {
		this.index = ind;
		this.split = spl;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getSplitter() {
		return split;
	}
	
	public String toString() {
		return "( Split with " + split + " at index " + index + ")";
	}
	
}
