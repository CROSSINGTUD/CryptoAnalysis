package crypto.rules;

public class CrySLSplitter {

	private final int index;
	private final String split;
	
	public CrySLSplitter(int ind, String spl) {
		this.index = ind;
		this.split = spl;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getSplitter() {
		return split;
	}

	@Override
	public boolean equals(Object other) {
		if(!(other instanceof CrySLSplitter))
			return false;

		CrySLSplitter splitter = (CrySLSplitter) other;
		return
			this.index == splitter.getIndex() &&
			this.split.equals(splitter.getSplitter());
	}
	
	public String toString() {
		return ".split(" + split + ")[" + index + "]";
	}
	
}
