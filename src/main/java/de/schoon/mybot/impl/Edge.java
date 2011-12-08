package de.schoon.mybot.impl;

import com.google.common.base.Objects;

public class Edge<T> {
	
	private T from;
	
	private T to;

	public Edge(T from, T to) {
		this.from = from;
		this.to = to;
	}
	
	public T getFrom() {
		return from;
	}

	public T getTo() {
		return to;
	}

	public int hashCode() {
		return Objects.hashCode(from, to);
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Edge)) {
			return false;
		}
		Edge<?> that = (Edge<?>) o;
		return this.from.equals(that.from)&& this.to.equals(that.to); 
	}
	
	@Override
	public String toString() {
		return from + "/" + to;
	}

}
