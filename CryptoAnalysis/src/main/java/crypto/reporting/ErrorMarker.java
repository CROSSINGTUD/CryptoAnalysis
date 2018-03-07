package crypto.reporting;

import boomerang.jimple.Statement;

public class ErrorMarker {

	private Statement location;
	private String string;

	public ErrorMarker(Statement location, String string) {
		this.location = location;
		this.string = string;
	}

	public Statement getLocation() {
		return location;
	}


	public String getString() {
		return string;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((string == null) ? 0 : string.hashCode());
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
		ErrorMarker other = (ErrorMarker) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (string == null) {
			if (other.string != null)
				return false;
		} else if (!string.equals(other.string))
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		return string;
	}
}
