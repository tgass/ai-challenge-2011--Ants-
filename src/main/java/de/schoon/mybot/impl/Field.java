package de.schoon.mybot.impl;





import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import de.schoon.mybot.Tile;


public class Field {
	
	private static final Logger LOG = Logger.getLogger(Field.class);

	private int maxRows;
	
	private int maxCols;
	
	private int depth;
	
	private SetMultimap<Integer, Integer> edges = HashMultimap.create(512,4);
	
	private Set<Integer> nodes = Sets.newHashSet();
	
	private Set<Integer> ants = Sets.newHashSet();
	
	private Set<Integer> foods = Sets.newHashSet();
	
	private Set<Integer> enemyAnts = Sets.newHashSet();
	
	private Map<Integer, Env> envs = Maps.newHashMap();
	
	public Field(int maxRows, int maxCols, int depth) {
		this.maxRows = maxRows;
		this.maxCols = maxCols;
		this.depth = depth;
	}

	public Env food(Tile t) {
		int foodId = t.getRow() * maxCols + t.getCol();
		int distance = Integer.MAX_VALUE;
		Env closestAnt = null;
		for(int ant : envs.keySet()) {
			Env env = envs.get(ant);
			if(env.knowsFood(foodId) && env.getFoodDistance(foodId) < distance) {
				distance = env.getFoodDistance(foodId);
				closestAnt = env;
			}
		}
		if(closestAnt != null) envs.remove(closestAnt.getNode());
		return closestAnt;
	}
	
	public boolean hasAnt(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		return envs.containsKey(id);
	}
	
	public Env getEnv(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		return envs.get(id);
	}
	
	public void addLand(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		nodes.add(id);
	}
	
	public void addMyAnt(Tile t) {
		int id = t.getRow() * maxCols + t.getCol();
		nodes.add(id);
		ants.add(id);
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
	
	public boolean hasEnemyAnt(int id) {
		return enemyAnts.contains(id);
	}
	
	public void scan() {
		for(int ant : ants) {
			EnvFactory bfs = new EnvFactory(this, ant);
			Env env = bfs.getEnv(ant, depth);
			envs.put(ant, env);
			LOG.debug("Env: " + env);
		}
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
		}
		LOG.debug("edges: " + edges);
	}
	
	public void print() {
		for(int row = 0; row < maxRows; row++) {
			String out = "";
			for(int col = 0; col < maxCols; col++) {
				if(ants.contains(row*maxCols + col)) {
					out += "X";
				} else if(foods.contains(row*maxCols + col)) {
					out += "%";
				} else if(enemyAnts.contains(row*maxCols + col)) {
					out += "!";
				} else if(edges.containsKey(row*maxCols + col)) {
					out += ".";
				} else {
					out += "0";
				}
			}
			LOG.debug(out);
		}
	}

	
	private void addEdge(int from, int to) {
		edges.put(from, to);
		edges.put(to, from);
	}

	public boolean isDirected() {
		return false;
	}

	public Set<Integer> getEdges(Integer node) {
		return edges.get(node);
	}
	
	public boolean containsFood(int i) {
		return foods.contains(i);
	}
	
	public boolean containsAnt(int i) {
		return ants.contains(i);
	}

	public int getMaxRows() {
		return maxRows;
	}

	public int getMaxCols() {
		return maxCols;
	}

	public int getDepth() {
		return depth;
	}
	
}
