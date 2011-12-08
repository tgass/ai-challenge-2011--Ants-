import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Field {

	private int maxRows;

	private int maxCols;

	private int viewRadius2;

	private int attackRadius2;

	private Map<Integer, Set<Integer>> edges = new HashMap<Integer, Set<Integer>>();

	private Set<Integer> nodes = new HashSet<Integer>();

	private Set<Integer> myHills = new HashSet<Integer>();

	private Set<Integer> enemyHills = new HashSet<Integer>();

	private Set<Integer> myAnts = new HashSet<Integer>();

	private Set<Integer> foods = new HashSet<Integer>();

	private Set<Integer> enemyAnts = new HashSet<Integer>();

	private Map<Integer, Env> envs = new HashMap<Integer, Env>();

	private Map<Integer, Integer> influence = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> enemyInfluence = new HashMap<Integer, Integer>();

	// wohin / wer
	private Map<Integer, Integer> orders = new HashMap<Integer, Integer>();

	public Field() { }

	public void addInfluence(int id) {
		int i = influence.get(id) + 1;
		influence.put(id, i);
	}

	public void addEnemyInfluence(int id) {
		int i = enemyInfluence.get(id) + 1;
		enemyInfluence.put(id, i);
	}

	public int getEnemyInfluence(int id) {
		return enemyInfluence.get(id);
	}

	public int getInfluence(int id) {
		return influence.get(id);
	}

	public void prepareAttack() {
		for(Env env : envs.values()) {
			if(!env.hasMoved() && env.hasEnemyAnt()) {
				int enemyAnt = env.getClosestEnemyAnt();
				int move = env.getMoveTo(enemyAnt);
				if(env.weakness.get(move) == Status.LIVE) {
					issueOrder(move, env);
				} else if(env.weakness.get(move) == Status.FIGHT) {
					if(env.hasMyAnt()) {
						int id = env.getClosestMyAnt();
						if(env.getMyAntDistance(id) > 1) {
							move = env.getMoveTo(id);
							if(env.weakness.get(move) == Status.LIVE) {
								issueOrder(move, env);
							}
						} else {
							env.setHasMovedTo(true);
						}
					} else {
						env.setHasMovedTo(true);
					}
				} else {
					move = env.getMoveAway(enemyAnt);
					issueOrder(move, env);
				}
				
//				if(move == -1 && (!env.hasMyHill() || env.hasMyHill() && env.getMyHillMinDistance() > 3)) {
//				}
			}
		}
	}

	public void gatherFood() {
		for(int foodId : foods) {
			Env env = getFoodAnt(foodId);
			if(env != null) {
				int move = env.getMoveTo(foodId);
				issueOrder(move, env);
			}
		}
	}

	public void defaultMove() {
		for(Env env : envs.values()) {
			if(!env.hasMoved()) {
				int move = env.getDefaultMove();
				issueOrder(move, env);
			}
		}
	}

	public void huntHill() {
		List<Integer> moves = new ArrayList<Integer>();
		for(int enemyHill : enemyHills) {
			for(Env env : getHillHunter(enemyHill)) {
				int move = env.getMoveTo(enemyHill);
				if(move != -1 && !orders.keySet().contains(move)) {
					orders.put(move, env.getNode());
					env.setHasMovedTo(true);
					moves.add(move);
				}
			}
		}
		for(int move : moves) {
			if(enemyHills.contains(move)) {
				enemyHills.remove(move);
			}
		}
	}

	public List<Env> getHillHunter(int hillId) {
		List<Env> hillHunter = new ArrayList<Env>();
		for(int ant : envs.keySet()) {
			Env env = envs.get(ant);
			if(!env.hasMoved() && env.hasEnemyHill(hillId)) {
				hillHunter.add(env);
			}
		}
		return hillHunter;
	}

	private Env getFoodAnt(int foodId) {
		int distance = Integer.MAX_VALUE;
		Env closestAnt = null;
		for(int ant : envs.keySet()) {
			Env env = envs.get(ant);
			if(!env.hasMoved() && env.knowsFood(foodId) && env.isClosestFood(foodId) && env.getFoodDistance(foodId) < distance) {
				distance = env.getFoodDistance(foodId);
				closestAnt = env;
			}
		}
		return closestAnt;
	}

	public void reset(int maxRows, int maxCols, int viewRadius2, int attackRadius2) {
		this.maxRows = maxRows;
		this.maxCols = maxCols;
		this.viewRadius2 = viewRadius2;
		this.attackRadius2 = attackRadius2;
		myAnts = new HashSet<Integer>();
		foods = new HashSet<Integer>();
		enemyAnts = new HashSet<Integer>();
		envs = new HashMap<Integer, Env>();
		orders = new HashMap<Integer, Integer>();
		influence = new HashMap<Integer, Integer>();
		enemyInfluence = new HashMap<Integer, Integer>();
	}

	public void update() {
		for(Integer node : nodes) {
			int row = (int)Math.floor(node / maxCols);
			int col = node % maxCols;
			if(nodes.contains(row * maxCols + ((col+1) % maxCols))) {
				addEdge(node, row * maxCols + ((col+1) % maxCols));
			} 
			if(nodes.contains(((row+1) % maxRows) * maxCols + col)) {
				addEdge(node, (((row+1) % maxRows) * maxCols + col));
			}
			influence.put(node, 0);
			enemyInfluence.put(node, 0);
		}
	}

	public void scan() {
		for(int enemyAnt : enemyAnts) {
			EnemyInfluenceSetter.createInfluence(this, enemyAnt);
		}
		for(int ant : myAnts) {
			EnvFactory envFactory = new EnvFactory(this, ant);
			Env env = envFactory.getEnv();
			envs.put(ant, env);
		}
		for(int envId : envs.keySet()) {
			List<Integer> ids = new ArrayList<Integer>();
			ids.add(envId);
			ids.addAll(edges.containsKey(envId) ? edges.get(envId) : new ArrayList<Integer>());
			Map<Integer, Integer> weakness = new HashMap<Integer, Integer>();
			for(int id : ids) {
				WeaknessFactory f = new WeaknessFactory();
				weakness.put(id, f.getWeakness(this, id));
			}
			envs.get(envId).setWeakness(weakness);
		}
	}

	private void addEdge(int from, int to) {
		put(from, to);
		put(to, from);
	}

	private void put(int i, int j) {
		if(!edges.containsKey(i)) {
			edges.put(i, new HashSet<Integer>());
		}
		edges.get(i).add(j);
	}

	public void addLand(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		nodes.add(id);
	}

	public void addMyHill(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		// ein Node muss schon da sein / wird von myBot separat eingelesen.
		myHills.add(id);
	}

	public void addEnemyHill(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		enemyHills.add(id);
	}

	public void addMyAnt(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		nodes.add(id);
		myAnts.add(id);
	}

	public void addFood(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		nodes.add(id);
		foods.add(id);
	}

	public void addEnemyAnt(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		nodes.add(id);
		enemyAnts.add(id);
	}

	public boolean containsMyAnt(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		return envs.containsKey(id);
	}

	public boolean containsMyHill(int id) {
		return myHills.contains(id);
	}

	public boolean containsEnemyHill(int id) {
		return enemyHills.contains(id);
	}

	public boolean containsEnemyAnt(int id) {
		return enemyAnts.contains(id);
	}

	public boolean containsFood(int i) {
		return foods.contains(i);
	}

	public boolean containsAnt(int i) {
		return myAnts.contains(i);
	}

	public Set<Integer> getEdges(Integer node) {
		return edges.containsKey(node) ? edges.get(node) : new HashSet<Integer>();
	}

	public int getMaxRows() {
		return maxRows;
	}

	public int getMaxCols() {
		return maxCols;
	}

	public int getViewRadius2() {
		return viewRadius2;
	}

	public int getViewRadius() {
		return (int)Math.floor(Math.sqrt(viewRadius2));
	}

	public int getAttackRadius2() {
		return attackRadius2;
	}

	public int getAttackRadius() {
		return (int)Math.floor(Math.sqrt(attackRadius2));
	}

	/* U T I L S */

	public void issueOrder(int move, Env env) {
		if(move != -1 && !myAnts.contains(move) && !orders.keySet().contains(move)) { 
			orders.put(move, env.getNode());
			myAnts.remove(env.getNode());
			myAnts.add(move);
			env.setHasMovedTo(true);
		}
	}

	public void issueOrders(Ants ants) {
		for(int toId : orders.keySet()) {
			Tile to = makeTile(toId);
			Tile from = makeTile(orders.get(toId));
			Aim aim = getAim(from, to);
			ants.issueOrder(from, aim);
		}
	}

	public Tile makeTile(int id) {
		return new Tile ((int)Math.floor(id / maxCols), id % maxCols);
	}

	public int getDistance2(int id1, int id2) {
		Tile t1 = makeTile(id1);
		Tile t2 = makeTile (id2);
		int rowDelta = Math.abs(t1.getRow() - t2.getRow());
		int colDelta = Math.abs(t1.getCol() - t2.getCol());
		rowDelta = Math.min(rowDelta, maxRows - rowDelta);
		colDelta = Math.min(colDelta, maxCols - colDelta);
		return rowDelta * rowDelta + colDelta * colDelta;
	}

	public Aim getAim(Tile from, Tile to) {
		int fromRow = from.getRow();
		int toRow = to.getRow();
		int fromCol = from.getCol();
		int toCol = to.getCol();
		if (fromRow < toRow) {
			if (toRow == maxRows-1 && fromRow == 0) {
				return Aim.NORTH;
			} else {
				return Aim.SOUTH;
			}
		} else if (toRow < fromRow) {
			if (fromRow == maxRows-1 && toRow == 0) {
				return Aim.SOUTH;
			} else {
				return Aim.NORTH;
			}
		}
		if (fromCol < toCol) {
			if (toCol == maxCols-1 && fromCol == 0) {
				return Aim.WEST;
			} else {
				return Aim.EAST;
			}
		} else if (toCol < fromCol) {
			if (fromCol == maxCols-1 && toCol == 0) {
				return Aim.EAST;
			} else {
				return Aim.WEST;
			}
		}
		return null;
	}

	protected Collection<Env> getEnvs() {
		return envs.values();
	}

	protected Map<Integer, Integer> getOrders() {
		return orders;
	}

	protected String getInfluenceMapAsString() {
		String r = "";
		for(int row = 0; row < maxRows; row++) {
			String e = "";
			String i = "";
			for(int col = 0; col < maxCols; col++) {
				i += influence.get(row*maxCols + col);
				e += enemyInfluence.get(row*maxCols + col);
			}
			r += i + "  " + e + "\n";
		}
		return r;
	}


	@Override
	public String toString() {
		String r = "";
		for(int row = 0; row < maxRows; row++) {
			String out = "";
			for(int col = 0; col < maxCols; col++) {
				if(myAnts.contains(row*maxCols + col)) {
					out += "X";
				} else if(foods.contains(row*maxCols + col)) {
					out += "%";
				} else if(enemyAnts.contains(row*maxCols + col)) {
					out += "!";
				} else if(myHills.contains(row*maxCols + col)) {
					out += "H";
				} else if(nodes.contains(row*maxCols + col)) {
					out += ".";
				} else {
					out += "0";
				}
			}
			r += out + "\n";
		}
		return r;
	}

}
