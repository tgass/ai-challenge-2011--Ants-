import java.util.List;

public class AttackHeuristik implements Heuristic {

	private int maxRows;

	private int maxCols;

	private List<Integer> nodes;
	
	private AttackHeuristik() { }

	public static Heuristic of(List<Integer> nodes, int maxRows, int maxCols) {
		AttackHeuristik h = new AttackHeuristik();
		h.maxRows = maxRows;
		h.maxCols = maxCols;
		h.nodes = nodes;
		return h;
	}

	@Override
	public double getCostEstimate(int from) {
		int fromRow = (int)Math.floor(from / maxCols);
		int fromCol = from % maxCols;
		double addedPathLength = 0;
		for(int node : nodes) {
			addedPathLength += getDistance(node, fromRow, fromCol);
		}
		return addedPathLength;
	}

	public double getDistance(int id, int fromRow, int fromCol) {
		int row = (int)Math.floor(id / maxCols);
		int col = id % maxCols;

		int rowDelta = Math.abs(row - fromRow);
		int colDelta = Math.abs(col - fromCol);

		rowDelta = Math.min(rowDelta, maxRows-rowDelta);
		colDelta = Math.min(colDelta, maxCols-colDelta);

		return Math.sqrt(rowDelta*rowDelta + colDelta*colDelta);
	}

}
