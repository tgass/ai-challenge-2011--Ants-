import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;


public class HillTest {

	@Test
	public void test() {
		int maxRows = 10;
		int maxCols = 20;
		int viewRadius = 5;
		int attackRadius = 3;
		
		Set<Tile> ants = new HashSet<Tile>(Arrays.asList(new Tile(3, 11)));
		
		Field field = new Field();
		field.reset(maxRows, maxCols, viewRadius, attackRadius);
		for(int i = 0; i < maxRows; i++) {
			for(int j = 0; j < maxCols; j++) {
				Tile t = new Tile(i, j);
				if(ants.contains(t)) {
					field.addMyAnt(t);
				} else {
					field.addLand(t);
				}
			}
		}
		field.addMyHill(new Tile(4, 11));
		
		field.update();
		field.scan();
		System.out.println(field);
		field.gatherFood();
		Map<Integer, Integer> orders = field.getOrders();
		for(int toId : orders.keySet()) {
			Tile to = field.makeTile(toId);
			Tile from = field.makeTile(orders.get(toId));
			System.out.println(from + "/" + to + "/" + field.getAim(from, to));
		}
		
	}
	
}
