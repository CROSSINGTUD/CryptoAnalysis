package crypto.rules;

public class StateNode implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String name;

	private Boolean init = false;
	private Boolean accepting = false;
	private int hopsToAccepting = Integer.MAX_VALUE;

	public StateNode(String _name) {
		name = _name;
	}
	
	public StateNode(String _name, Boolean _init) {
		this(_name);
		init = _init;
	}

	public StateNode(String _name, Boolean _init, Boolean _accepting) {
		this(_name, _init);
		accepting = _accepting;
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
	
	public void setAccepting(Boolean _accepting) {
		accepting = _accepting;
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
