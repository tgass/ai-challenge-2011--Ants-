public class Edge {
	
	private int from;
	
	private int to;

	public Edge(int from, int to) {
		this.from = from;
		this.to = to;
	}
	
	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public int hashCode() {
		return from * from + to;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Edge)) {
			return false;
		}
		Edge that = (Edge)  o;
		return this.from == that.from && this.to == that.to; 
	}
	
	@Override
	public String toString() {
		return from + "/" + to;
	}

}
