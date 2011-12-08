import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MyBot extends Bot {
    
    private Map<Tile, Tile> orders = new HashMap<Tile, Tile>();

    private Field field = new Field();
    
    public static void main(String[] args) throws IOException, InterruptedException {
    	new MyBot().readSystemInput();
    }
	
    @Override
    public void doTurn() {
        Ants ants = getAnts();
        orders.clear();
        field.reset(ants.getRows(), ants.getCols(), ants.getViewRadius2(), ants.getAttackRadius2());

        for(int row = 0; row < ants.getRows(); row++) {
        	for(int col = 0; col < ants.getCols(); col++) {
        		Tile t = new Tile(row, col);
        		if(ants.isVisible(t) && ants.getIlk(t) == Ilk.WATER) {
        			//
        		} else if(ants.isVisible(t) && ants.getIlk(t) == Ilk.MY_ANT) {
        			field.addMyAnt(t);
        		} else if(ants.isVisible(t) && ants.getIlk(t) == Ilk.FOOD) {
        			field.addFood(t);
        		} else if(ants.isVisible(t) && ants.getIlk(t) == Ilk.LAND) {
        			field.addLand(t);
        		} else if(ants.isVisible(t) && ants.getIlk(t) == Ilk.ENEMY_ANT) {
        			field.addEnemyAnt(t);
        		}
        	}
        }
        for(Tile t : ants.getMyHills()) {
        	field.addMyHill(t);
        }
        for(Tile t : ants.getEnemyHills()) {
        	field.addEnemyHill(t);
        }
        
        field.update();
        field.scan();
        
        field.huntHill();
        field.gatherFood();
        field.prepareAttack();
        field.defaultMove();
        
        field.issueOrders(ants);
        
    }
}
