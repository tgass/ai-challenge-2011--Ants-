import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

public class DefaultMoveHeuristic implements Heuristic {

	private int maxRows;

	private int maxCols;

	// distance / ant 
	private List<Integer> sortedIds = new ArrayList<Integer>();

	// distance / hills
	private SortedMap<Integer, Integer> myHills;

	private DefaultMoveHeuristic() { }

	public static Heuristic of(SortedMap<Integer, List<Integer>> sortedIds, SortedMap<Integer, Integer> myHillsSorted, 
			int size, int maxRows, int maxCols) {
		DefaultMoveHeuristic h = new DefaultMoveHeuristic();
		h.maxRows = maxRows;
		h.maxCols = maxCols;
		for(List<Integer> ids : sortedIds.values()) {
			h.sortedIds.addAll(ids);
			if(h.sortedIds.size() >= size) {
				break;
			}
		}
		h.myHills = myHillsSorted;
		return h;
	}

	@Override
	public double getCostEstimate(int from) {
		int fromRow = (int)Math.floor(from / maxCols);
		int fromCol = from % maxCols;
		double addedPathLength = 0;
		for(int id : sortedIds) {
			addedPathLength += getDistance(id, fromRow, fromCol);
		}
		if(sortedIds.size() <= 1 && myHills.size() >= 1) {
			int actualDistance = myHills.firstKey();
			addedPathLength += getDistance(myHills.get(actualDistance), fromRow, fromCol);
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
