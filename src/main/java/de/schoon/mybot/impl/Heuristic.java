package de.schoon.mybot.impl;

import com.google.common.base.Function;

public class Heuristic implements Function<Integer, Integer> {

	private int maxRows;
	
	private int maxCols;
	
	private int row;
	
	private int col;
	
	private Heuristic() { }
	
	public static Heuristic of(int to, int maxRows, int maxCols) {
		Heuristic h = new Heuristic();
		h.row = (int)Math.floor(to / maxCols);
		h.col = to % maxCols;
		h.maxRows = maxRows;
		h.maxCols = maxCols;
		return h;
	}
	
	public Integer apply(Integer from) {
		int fromRow = (int)Math.floor(from / maxCols);
		int fromCol = from % maxCols;
		
		int rowDelta = Math.abs(row - fromRow);
		int colDelta = Math.abs(col - fromCol);
		return Math.min(rowDelta, maxRows - rowDelta) + Math.min(colDelta, maxCols - colDelta);
	}

}
