import java.util.ArrayList;
import java.util.List;

public class Path implements Comparable<Path> {

	private List<Edge> moves = new ArrayList<Edge>();
	
	private double costEstimate;
	
	private boolean reverse;
	
	private int head;
	
	public Path() {	}
	
	public Path(Edge e, boolean reverse, double costEstimate) {
		this.moves.add(e);
		this.head = e.getTo();
		this.reverse = reverse;
		this.costEstimate = costEstimate;
	}
	
	public int getHead() {
		return head;
	}
	
	public int getLength() {
		return moves.size();
	}
	
	public int getFirstMove() {
		return moves.get(1).getTo();
	}
	
	public double getCost() {
		return moves.size() - 1 + costEstimate;
	}
	
	public double getCostEstimate() {
		return costEstimate;
	}
	
	public double getCostEstimatePerMove() {
		return costEstimate / (moves.size() - 1);
	}
	
	public double getEvaluation() {
		return reverse ? (-1) * costEstimate : getCost(); 
	}

	public Path add(Edge t, double costEstimate) {
		Path path = new Path();
		path.moves = new ArrayList<Edge>(moves);
		path.moves.add(t);
		path.head = t.getTo();
		path.costEstimate = costEstimate;
		path.reverse = reverse;
		return path;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Path)) {
			return false;
		}
		Path that = (Path)o;
		return this.head == that.head;
	}

	public int compareTo(Path that) {
		return (this.getEvaluation() > that.getEvaluation()) ? 1 : (this.getEvaluation() == that.getEvaluation()) ? 0 : -1;
	}
	
	@Override
	public String toString() {
		return "moves=" + moves + 
			", cost=" + getCost() + 
			", costEst=" + costEstimate + 
			", costEstPerMove=" + getCostEstimatePerMove() + 
			", evaluation=" + getEvaluation();
	}
	
}
