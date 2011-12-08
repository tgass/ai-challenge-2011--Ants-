package de.schoon.mybot.impl;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PathFactory {
	
	private static final int MOVE_COST = 1; 
	
	public static Path<Integer> search(Field field, boolean reverse, int start, int end, Function<Integer, Integer> h) {
		List<Integer> processed = Lists.newArrayList();
		List<Integer> discovered = Lists.newArrayList();
		
		discovered.add(start);
		PriorityQueue<Path<Integer>> frontier = new PriorityQueue<Path<Integer>>();
		Map<Integer, Integer> costMap = Maps.newHashMap();
		frontier.add(new Path<Integer>(new Edge<Integer>(null, start), reverse, 0, h.apply(start).intValue()));
		costMap.put(start, 0);
		
		while(!frontier.isEmpty()) {
			System.out.println("Frontier:\n" + Joiner.on("\n").join(frontier) + "\n----");
			Path<Integer> p = frontier.poll();
			Integer head = p.getHead();
			System.out.println("Processing vertext early: " + head);
			if(head.equals(end)) {
				System.out.println("Found cheapest path to " + p);
				return p;
			}
			for(Integer c : field.getEdges(head)) {
				int costEstimate = h.apply(c);
				//System.out.println("Cost Estimate for edge=" + c + ", estimate=" + costEstimate);
				if(!processed.contains(c) || field.isDirected()) {
					//System.out.println("Processing edge: " + head + "->" + c);
				}
				Path<Integer> newPath = p.add(new Edge<Integer>(head, c), MOVE_COST, costEstimate);
				if(!discovered.contains(c)) {
					System.out.println("Adding path=" + newPath);
					discovered.add(c);
					frontier.add(newPath);
					costMap.put(c, newPath.getCost());
				}
				else if(discovered.contains(c) && compareCosts(reverse, costMap.get(c), newPath.getCost())) {
					System.out.println("Cheaper path to " + c + ". " + newPath);
					frontier.remove(c);
					frontier.add(newPath);
					costMap.put(c, newPath.getCost());
				}
			}
			processed.add(head);
			System.out.println("Processing vertext late: " + head);
		}
		return null;
	}
	
	private static boolean compareCosts(boolean reverse, int presentCost, int proposedCost) {
		return reverse ? (presentCost < proposedCost) : (presentCost > proposedCost); 
	}

}
