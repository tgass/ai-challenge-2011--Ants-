import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;


public class EnemyEnvFactoryTest {

	@Test
	public void test() {
		int maxRows = 15;
		int maxCols = 20;
		int viewRadius = 5;
		int attackRadius = 3;
		
		Set<Tile> ants = new HashSet<Tile>(Arrays.asList(new Tile(5, 5),new Tile(5, 6)));
		Set<Tile> enemy = new HashSet<Tile>(Arrays.asList(new Tile(5, 11)));
		
		Field field = new Field();
		field.reset(maxRows, maxCols, viewRadius, attackRadius);
		for(int i = 0; i < maxRows; i++) {
			for(int j = 0; j < maxCols; j++) {
				Tile t = new Tile(i, j);
				if(ants.contains(t)) {
					field.addMyAnt(t);
				} else if(enemy.contains(t)) {
					field.addEnemyAnt(t);
				} else {
					field.addLand(t);
				}
			}
		}
		field.update();
		field.scan();
		System.out.println(field);
		System.out.println();
		System.out.println(field.getInfluenceMapAsString());
		System.out.println(field.getEnvs());
		
		
	}
	
}
