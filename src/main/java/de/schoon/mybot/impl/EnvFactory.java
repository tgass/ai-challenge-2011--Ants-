package de.schoon.mybot.impl;


import java.util.List;
import java.util.Queue;

import com.google.common.collect.Lists;

public class EnvFactory {
	
	private Env env;
	
	private Field graph;
	
	private List<Integer> processed = Lists.newArrayList();
	
	private List<Integer> discovered = Lists.newArrayList();

	public EnvFactory(Field graph, int location) {
		this.graph = graph;
		this.env = new Env(location, graph.getDepth(), graph.getMaxRows(), graph.getMaxCols());
	}
	
	public int distance(int node, int distance) {
		if(this.env.hasParent(node)) {
			return distance(env.getParent(node), ++distance);
		}
		return distance;
	}
	
	public Env getEnv(Integer start, int depth) {
		discovered.add(start);
		Queue<Integer> frontier = Lists.newLinkedList();
		frontier.add(start);
		while(!frontier.isEmpty()) {
			Integer v = frontier.poll();
			int distance = distance(v, 0);
			if(distance > depth) {
				System.out.println("Skipping " + v + ", distance=" + distance);
				continue;
			}
			process(v, distance);
			//System.out.println("Processing vertext: " + v);
			for(Integer c : graph.getEdges(v)) {
				if(!processed.contains(c) || graph.isDirected()) {
					//System.out.println("Processing edge: " + v + "->" + c);
				}
				if(!discovered.contains(c)) {
					frontier.add(c);
					discovered.add(c);
					env.addParent(c, v);
				}
			}
			//System.out.println("Processing vertext late: " + v);
		}
		return env;
	}
	
	private void process(int v, int distance) {
		if(graph.containsAnt(v)) {
			env.addAnt(v, distance);
		} else if(graph.containsFood(v)) {
			env.addFood(v, distance);
		} else if(graph.hasEnemyAnt(v)) {
			env.addEnemyAnt(v, distance);
		}
		processed.add(v);
	}

}
