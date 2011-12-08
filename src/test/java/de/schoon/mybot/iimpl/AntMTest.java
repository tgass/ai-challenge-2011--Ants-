package de.schoon.mybot.iimpl;

import org.junit.Test;

import de.schoon.mybot.Tile;
import de.schoon.mybot.impl.Field;
import de.schoon.mybot.impl.Heuristic;
import de.schoon.mybot.impl.Path;
import de.schoon.mybot.impl.PathFactory;

public class AntMTest {
	
	@Test
	public void foo() {
		int maxRows = 10;
		int maxCols = 5;
		Field m = new Field(10, 5, 7);
		m.addLand(new Tile(0, 0));
		m.addLand(new Tile(0, 1));
		m.addLand(new Tile(0, 2));
		m.addLand(new Tile(0, 3));
		m.addLand(new Tile(1, 0));
		m.addLand(new Tile(1, 1));
		m.addLand(new Tile(1, 2));
		m.addLand(new Tile(1, 3));
		m.addLand(new Tile(1, 4));
		m.addLand(new Tile(2, 0));
		m.addLand(new Tile(2, 1));
		m.addLand(new Tile(2, 2));
		m.addLand(new Tile(2, 3));
		m.addLand(new Tile(2, 4));
		m.addLand(new Tile(3, 1));
		m.addLand(new Tile(3, 3));
		m.addLand(new Tile(3, 4));
		m.addLand(new Tile(4, 0));
		m.addLand(new Tile(4, 1));
		m.addLand(new Tile(4, 2));
		m.addLand(new Tile(5, 0));
		m.addLand(new Tile(5, 1));
		m.addLand(new Tile(5, 2));
		m.addLand(new Tile(5, 3));
		m.addLand(new Tile(5, 4));
		
		m.addMyAnt(new Tile(1, 3));
		m.addEnemyAnt(new Tile(1, 4));
		
		m.addFood(new Tile(5, 3));
	
		m.update();
		m.print();
		m.scan();
		
		Path<Integer> p = PathFactory.search(m, true, 8, 20, Heuristic.of(20, maxRows, maxCols));
		System.out.println(p);
		
	}

}
