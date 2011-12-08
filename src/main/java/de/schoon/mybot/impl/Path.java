package de.schoon.mybot.impl;

import java.util.List;

import com.google.common.collect.Lists;

public class Path<T> implements Comparable<Path<T>> {

	private List<Edge<T>> moves = Lists.newArrayList();
	
	private List<Integer> cost = Lists.newArrayList();
	
	private int costEstimate;
	
	private boolean reverse;
	
	private T head;
	
	public Path() {	}
	
	public Path(Edge<T> t, boolean reverse, int cost, int costEstimate) {
		this.moves.add(t);
		this.head = t.getTo();
		this.cost.add(cost);
		this.reverse = reverse;
		this.costEstimate = costEstimate;
	}
	
	public T getHead() {
		return head;
	}
	
	public int getCost() {
		int sum = 0;
		for(int c : cost) {
			sum += c;
		}
		return reverse ? (-1) * (sum + costEstimate) : (sum + costEstimate);
	}

	public Path<T> add(Edge<T> t, int cost, int costEstimate) {
		Path<T> path = new Path<T>();
		path.moves = Lists.newArrayList(moves);
		path.moves.add(t);
		path.head = t.getTo();
		path.cost = Lists.newArrayList(this.cost);
		path.cost.add(cost);
		path.costEstimate = costEstimate;
		path.reverse = reverse;
		return path;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Path)) {
			return false;
		}
		Path<?> that = (Path<?>)o;
		return this.head.equals(that.head);
	}

	public int compareTo(Path<T> that) {
		return this.getCost() - that.getCost();
	}
	
	@Override
	public String toString() {
		return "moves=" + moves + ", cost=" + cost + ", costEst=" + costEstimate + ", totalCost=" + getCost();
	}
	
}
