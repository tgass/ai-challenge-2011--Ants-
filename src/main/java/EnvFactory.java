import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class EnvFactory {
	
	private final int DEPTH;
	
	private Env env;
	
	private Field field;
	
	private int locationId;
	
	public EnvFactory(Field field, int locationId) {
		this.field = field;
		this.DEPTH = field.getViewRadius() * 2;
		this.env = new Env(locationId, field);
		this.locationId = locationId;
	}
	
	public Env getEnv() {
		Set<Node> discovered = new HashSet<Node>();
		Node start = new Node(locationId, field, 0);
		discovered.add(start);
		Queue<Node> frontier = new LinkedList<Node>();
		frontier.add(start);
		while(!frontier.isEmpty()) {
			Node node = frontier.poll();
			int distance = node.getDistance();
			if(distance > DEPTH) {
				continue;
			}
			process(node);
			for(Integer c : field.getEdges(node.getId())) {
				Node cNode = new Node(c, field, node.getDistance()+1);
				if(!discovered.contains(cNode)) {
					frontier.add(cNode);
					discovered.add(cNode);
					env.addParent(cNode.getId(), node.getId());
				}
			}
		}
		return env;
	}
	
	private void process(Node n) {
		int id = n.getId();
		int distance = n.getDistance();
		if(field.containsAnt(id)) {
			env.addAnt(id, distance);
		} else if(field.containsFood(id)) {
			env.addFood(id, distance);
		} else if(field.containsEnemyAnt(id)) {
			env.addEnemyAnt(id, distance);
		}
		if(field.containsMyHill(id)) {
			env.addMyHill(id, distance);
		} else if(field.containsEnemyHill(id)) {
			env.addEnemyHill(id, distance);
		}
		if(distance <= 4 && field.getDistance2(id, locationId) <= 10) {
			field.addInfluence(id);
		}
	}
	
}
