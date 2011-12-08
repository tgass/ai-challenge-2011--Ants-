public class FlightHeuristic implements Heuristic {

	private int maxRows;

	private int maxCols;
	
	private int nodeId;
	
	private FlightHeuristic() { }
	
	public static FlightHeuristic of(int nodeId, int maxRows, int maxCols) {
		FlightHeuristic h = new FlightHeuristic();
		h.maxRows = maxRows;
		h.maxCols = maxCols;
		h.nodeId = nodeId;
		return h;
	}
	
	@Override
	public double getCostEstimate(int from) {
		int fromRow = (int)Math.floor(from / maxCols);
		int fromCol = from % maxCols;

		int row = (int)Math.floor(nodeId / maxCols);
		int col = nodeId % maxCols;

		int rowDelta = Math.abs(row - fromRow);
		int colDelta = Math.abs(col - fromCol);

		rowDelta = Math.min(rowDelta, maxRows-rowDelta);
		colDelta = Math.min(colDelta, maxCols-colDelta);

		return Math.sqrt(rowDelta*rowDelta + colDelta*colDelta);
	}

}
