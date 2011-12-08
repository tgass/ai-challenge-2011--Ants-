package de.schoon.mybot;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.schoon.mybot.impl.Env;
import de.schoon.mybot.impl.Field;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {
    
	private static final Logger LOG = Logger.getLogger(MyBot.class);
	
    private Map<Tile, Tile> orders = new HashMap<Tile, Tile>();

    private Set<Tile> unseenTiles;
    
    private Set<Tile> enemyHills = new HashSet<Tile>();
	
	/**
     * Main method executed by the game engine for starting the bot.
     * 
     * @param args command line arguments
     * 
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        new MyBot().readSystemInput();
    }
	
	private boolean doMoveDirection(Tile antLoc, Aim direction) {
        Ants ants = getAnts();
        // Track all moves, prevent collisions
        Tile newLoc = ants.getTile(antLoc, direction);
        if (ants.getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc)) {
            ants.issueOrder(antLoc, direction);
            orders.put(newLoc, antLoc);
            return true;
        } else {
            return false;
        }
    }
	
    private boolean doMoveLocation(Tile antLoc, Tile destLoc) {
        Ants ants = getAnts();
        // Track targets to prevent 2 ants to the same location
        List<Aim> directions = ants.getDirections(antLoc, destLoc);
        for (Aim direction : directions) {
            if (doMoveDirection(antLoc, direction)) {
                return true;
            }
        }
        return false;
    }	
    
    /**
     * For every ant check every direction in fixed order (N, E, S, W) and move it if the tile is
     * passable.
     */
    @Override
    public void doTurn() {
        Ants ants = getAnts();
        orders.clear();
        int viewRadius = ants.getViewRadius2();
        
        
        //Set<Tile> sortedAnts = new HashSet<Tile>(ants.getMyAnts());
       LOG.debug("rows=" + ants.getRows() + ", cols=" + ants.getCols() + ", viewRadius2=" + ants.getViewRadius2());
        Field field = new Field(ants.getRows(), ants.getCols(), ants.getViewRadius2());
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
        field.update();
        //field.print();
        field.scan();
        
        for(Tile food : ants.getFoodTiles()) {
        	Env env = field.food(food);
        	if(env != null) {
        		Tile move = env.getMoveTo(food);
        		LOG.debug("Food=" + food + ", ant=" + env.getAntLoc() + ", move=" + move);
        		doMoveLocation(env.getAntLoc(), move);
        	}
        }
        
        for(Tile ant : ants.getMyAnts()) {
        	if(field.hasAnt(ant)) {
        		Env env = field.getEnv(ant);
        		if(env.hasEnemyAnt()) {
        			doMoveLocation(env.getAntLoc(), env.getMoveToClosestEnemy());
        		} else {
        			doMoveDirection(env.getAntLoc(), env.getDefaultAim(20));
        		}
        	}
        }
    }
}
