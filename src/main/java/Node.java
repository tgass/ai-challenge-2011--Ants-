public class Node {

	private int id;
	
	private Tile tile;
	
	private int distance;
	
	public Node(int id, Field field, int distance) {
		this.id = id;
		this.tile = field.makeTile(id);
		this.distance = distance;
	}
	
	public int hashCode() {
		return id;
	}
	
	public int getRow() {
		return tile.getRow();
	}
	
	public int getCol() {
		return tile.getCol();
	}

	public boolean equals(Object o) {
		if(!(o instanceof Node)) {
			return false;
		}
		Node that = (Node) o;
		return this.id == that.id;
	}

	public int getId() {
		return id;
	}

	public int getDistance() {
		return distance;
	}

}