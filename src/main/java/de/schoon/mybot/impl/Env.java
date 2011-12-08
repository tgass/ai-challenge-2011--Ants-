package de.schoon.mybot.impl;


import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.schoon.mybot.Aim;
import de.schoon.mybot.Tile;

public class Env {
	
	private static final Logger LOG = Logger.getLogger(Env.class);
	
	private int node;
	
	private int row;
	
	private int col;
	
	private int depth;
	
	private int maxRows;
	
	private int maxCols;
	
	private Map<Integer, Integer> parent = Maps.newHashMap();
	
	// food / distance
	private Map<Integer, Integer> ants = Maps.newHashMap();
	
	// food / distance
	private Map<Integer, Integer> foods = Maps.newTreeMap();
	
	// enemy / distance
	private Map<Integer, Integer> enemyAnts = Maps.newTreeMap();

	// distance / enemy
	private SortedMap<Integer, Integer> enemyAntsSortedByDistance = Maps.newTreeMap(); 
	
	public Env(int node, int depth, int maxRows, int maxCols) {
		this.node = node;
		this.row = (int)Math.floor(node / maxCols);
		this.col = node % maxCols;
		this.depth = depth;
		this.maxRows = maxRows;
		this.maxCols = maxCols;
	}
	
	public int getNode() {
		return node;
	}
	
	public Tile getAntLoc() {
		return new Tile(row, col);
	}
	
	public Tile getMoveToClosestEnemy() {
		int id = enemyAntsSortedByDistance.get(enemyAntsSortedByDistance.firstKey());
		int row = (int)Math.floor(id / maxCols);
		int col = id % maxCols;
		return getMoveTo(new Tile(row, col));
	}
	
	public boolean hasEnemyAnt() {
		return !enemyAnts.isEmpty();
	}
	
	public Aim getDefaultAim(int depth) {
		return getAim(Sets.newHashSet(ants.keySet()), depth);
	}
	
	protected Aim getAim(Set<Integer> targets, int depth) {
		double dRowSum = 0;
		double dColSum = 0;
		for(Integer a : targets) {
			double[] args = scaleAndAdd(a, depth);
			dRowSum += args[0];
			dColSum += args[1];
		}
		double norm = Math.sqrt(dRowSum*dRowSum + dColSum*dColSum);
		dRowSum = -1.0 * dRowSum / norm;
		dColSum = -1.0 * dColSum / norm;
		//LOG.debug(dRowSum + " / " + dColSum + " : " + norm);
		if(dRowSum > Math.abs(dColSum)) {
			return Aim.SOUTH;
		} else if(dColSum > Math.abs(dRowSum)) {
			return Aim.EAST;
		} else if(Math.abs(dRowSum) > Math.abs(dColSum)) {
			return Aim.NORTH;
		} else {
			return Aim.WEST;
		}
	}
	
	private double[] scaleAndAdd(int id, int depth) {
		double dRow = (int)Math.floor(id / maxCols) - this.row;
		if(Math.abs(dRow) > Math.abs(dRow - maxRows)) {
			dRow = dRow - maxRows;
		}
		double dCol = id % maxCols - this.col;
		if(Math.abs(dCol) > Math.abs(dCol - maxCols)) {
			dCol = dCol - maxCols;
		}
		//LOG.debug("Abstand zu id=" + id + ", " + dRow + "/" + dCol);
		double norm = Math.sqrt(dRow*dRow + dCol*dCol);
		return new double[]{(depth/norm -1) * dRow, (depth/norm -1) * dCol};
	}
	
	public Tile getMoveTo(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		Deque<Integer> d = shortestPath(id, new LinkedList<Integer>());
		d.remove();
		int row = (int)Math.floor(1.0 * d.getFirst() / maxCols);
		int col = d.getFirst() % maxCols;
		return new Tile(row, col);
	}
	
	public Deque<Integer> shortestPath(Integer goal, Deque<Integer> deque) {
		if(parent.containsKey(goal)) {
			deque.push(parent.get(goal));
			shortestPath(parent.get(goal), deque);
		}
		return deque;
	}
	
	public void addParent(int n, int p) {
		this.parent.put(n, p);
	}
	
	public boolean hasParent(int n) {
		return this.parent.containsKey(n);
	}
	
	public int getParent(int n) {
		return this.parent.get(n);
	}
	
	public void addAnt(int ant, int distance) {
		if(ant != node) {
			this.ants.put(ant, distance);
		}
	}
	
	public void addFood(int food, int distance) {
		foods.put(food, distance);
	}
	
	public boolean knowsFood(int food) {
		return foods.containsKey(food);
	}
	
	public int getFoodDistance(int food) {
		return foods.get(food);
	}
	
	public void addEnemyAnt(int ant, int distance) {
		enemyAnts.put(ant, distance);
		enemyAntsSortedByDistance.put(distance, ant);
	}
	
	public int getEnemyAntDistance(int ant) {
		return enemyAnts.get(ant);
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("node", node)
			.add("ants", ants)
			.add("enemyAnts", enemyAnts)
			.add("food", foods)
			.add("aim", getDefaultAim(depth)).toString();
	}

}
