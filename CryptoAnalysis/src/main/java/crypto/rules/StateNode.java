package crypto.rules;

public class StateNode implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String name;

	private Boolean init;
	private Boolean accepting;
	private int hopsToAccepting = Integer.MAX_VALUE;

	public StateNode(String name) {
		this(name, false, false);
	}
	
	public StateNode(String name, Boolean init) {
		this(name, init, false);
	}

	public StateNode(String name, Boolean init, Boolean accepting) {
		this.name = name;
		this.init = init;
		this.accepting = accepting;
	}

	public String getName() {
		return name;
	}

	public Boolean getInit() {
		return init;
	}

	public Boolean getAccepting() {
		return accepting;
	}
	
	public void makeAccepting() {
		this.accepting = true;
	}

	public void setAccepting(Boolean accepting) {
		this.accepting = accepting;
	}
	
	public String toString() {
		StringBuilder nodeSB = new StringBuilder();
		nodeSB.append("Name: ");
		nodeSB.append(name);
		nodeSB.append(" (");
		if (!accepting) {
			nodeSB.append(hopsToAccepting + "hops to ");
		} 
		nodeSB.append("accepting)");
		return nodeSB.toString();
	}

	public boolean isErrorState() {
		return !accepting;
	}

	public boolean isInitialState() {
		return init;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accepting == null) ? 0 : accepting.hashCode());
		result = prime * result + ((init == null) ? 0 : init.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateNode other = (StateNode) obj;
		if (accepting == null) {
			if (other.accepting != null)
				return false;
		} else if (!accepting.equals(other.accepting))
			return false;
		if (init == null) {
			if (other.init != null)
				return false;
		} else if (!init.equals(other.init))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public void setHopsToAccepting(int hops) {
		hopsToAccepting = hops;
	}

	public int getHopsToAccepting() {
		return hopsToAccepting;
	}

}
