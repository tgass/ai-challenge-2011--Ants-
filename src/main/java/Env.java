import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Env {

	private int node;

	private int maxCols;

	private int maxRows;

	private Field field;

	private Map<Integer, Integer> parent = new HashMap<Integer, Integer>();

	// myHill / distance
	private Map<Integer, Integer> myHills = new TreeMap<Integer, Integer>();

	// distance / myHill
	private SortedMap<Integer, Integer> sortedMyHills = new TreeMap<Integer, Integer>();

	// enemyHill / distance
	private Map<Integer, Integer> enemyHills = new HashMap<Integer, Integer>();

	// ant / distance
	private Map<Integer, Integer> myAnts = new HashMap<Integer, Integer>();

	// distance / ant
	private SortedMap<Integer, List<Integer>> sortedMyAnts = new TreeMap<Integer, List<Integer>>(); 

	// food / distance
	private Map<Integer, Integer> foods = new HashMap<Integer, Integer>();

	// distance / food
	private SortedMap<Integer, Integer> sortedFood = new TreeMap<Integer, Integer>();

	// enemy / distance
	private Map<Integer, Integer> enemyAnts = new TreeMap<Integer, Integer>();
	
	// distance / enemy
	private SortedMap<Integer, Integer> sortedEnemyAnts = new TreeMap<Integer, Integer>();
	
	public Map<Integer, Status> weakness = new HashMap<Integer, Status>();
	
	private boolean hasMoved = false;

	public Env(int node, Field field) {
		this.node = node;
		this.maxRows = field.getMaxRows();
		this.maxCols = field.getMaxCols();
		this.field = field;
	}
	
	public void setWeakness(Map<Integer, Integer> weakness) {
		for(Map.Entry<Integer, Integer> entry : weakness.entrySet()) {
			if(entry.getValue() > field.getInfluence(entry.getKey())) {
				this.weakness.put(entry.getKey(), Status.DIE);
			} else if(entry.getValue() == field.getInfluence(entry.getKey())) {
				this.weakness.put(entry.getKey(), Status.FIGHT);
			} else {
				this.weakness.put(entry.getKey(), Status.LIVE);
			}
		}
	}
	
	public int getNode() {
		return node;
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}
	
	public void setHasMovedTo(boolean hasMoved) {
		this.hasMoved = true;
	}
	
	public boolean hasEnemyAnt() {
		return !enemyAnts.isEmpty();
	}
	
	public boolean hasMyHill() {
		return !myHills.isEmpty();
	}

	public int getDefaultMove() {
		Heuristic h = DefaultMoveHeuristic.of(sortedMyAnts, sortedMyHills, 3, maxRows, maxCols);
		Path path = PathFactory.search(field, this, true, node, h, field.getViewRadius()*2);
		if(path == null) {
			return -1;
		}
		return path.getFirstMove();
	}
	
	public int getMoveToFuzzy(int id) {
		List<Integer> nodes = new ArrayList<Integer>();
		nodes.add(id);
		Heuristic h = AttackHeuristik.of(nodes, maxRows, maxCols);
		Path path = PathFactory.search(field, this, false, node, h, 3);
		if(path == null) {
			return -1;
		}
		return path.getFirstMove();
	}
	
	public int getMoveAway(int id) {
		Heuristic h = FlightHeuristic.of(id, maxRows, maxCols);
		Path path = PathFactory.search(field, this, true, node, h, 3);
		if(path == null) {
			return -1;
		}
		return path.getFirstMove();
	}

	public int getMoveTo(int id) {
		Deque<Integer> path = shortestPath(id, new LinkedList<Integer>());
		path.add(id);
		if(path.size() == 1) {
			return -1;
		}
		path.remove();
		return path.remove();
	}
	
	private Deque<Integer> shortestPath(Integer goal, Deque<Integer> deque) {
		if(parent.keySet().contains(goal)) {
			deque.push(parent.get(goal));
			shortestPath(parent.get(goal), deque);
		}
		return deque;
	}

	public void addParent(int n, int p) {
		this.parent.put(n, p);
	}

	public void addAnt(int ant, int distance) {
		if(ant != node) {
			myAnts.put(ant, distance);
			if(!sortedMyAnts.keySet().contains(distance)) {
				sortedMyAnts.put(distance, new ArrayList<Integer>());
			}
			sortedMyAnts.get(distance).add(ant);
		}
	}

	public void addFood(int food, int distance) {
		foods.put(food, distance);
		sortedFood.put(distance, food);
	}

	public void addMyHill(int id, int distance) {
		myHills.put(id, distance);
		sortedMyHills.put(distance, id);
	}

	public void addEnemyHill(int id, int distance) {
		enemyHills.put(id, distance);
	}
	
	public void addEnemyAnt(int ant, int distance) {
		enemyAnts.put(ant, distance);
		sortedEnemyAnts.put(distance, ant);
	}

	public boolean hasEnemyHill(int id) {
		return enemyHills.containsKey(id);
	}
	
	public boolean hasMyAnt() {
		return !myAnts.isEmpty();
	}
	
	public boolean isClosestFood(int foodId) {
		return sortedFood.get(sortedFood.firstKey()) == foodId;
	}

	public boolean knowsFood(int food) {
		return foods.containsKey(food);
	}
	
	public int getClosestEnemyAnt() {
		return sortedEnemyAnts.get(sortedEnemyAnts.firstKey());
	}
	
	public int getMyAntDistance(int id) {
		return myAnts.get(id);
	}
	
	public int getClosestMyAnt() {
		return sortedMyAnts.get(sortedMyAnts.firstKey()).get(0);
	}
	
	public int getFoodDistance(int food) {
		return foods.get(food);
	}
	
	public int getMyHillMinDistance() {
		return sortedMyHills.firstKey();
	}
	
	@Override
	public String toString() {
		String s = "Env" + 
		" node=" + field.makeTile(node) +
		" weakness=" + weakness +
		" hill=";
		for(int h : myHills.keySet()) {
			s += field.makeTile(myHills.get(h)) + "(" + myHills.get(h) + ") ";
		}
		s += "myAnts=" + myAnts +
		" enemyAnts=" + enemyAnts +
		" food=";
		for(int f : foods.keySet()) {
			s += field.makeTile(f) + "(" + foods.get(f) + ") ";
		}

		s += " defaultMove=" + field.makeTile(getDefaultMove());
		return s;
	}

}
