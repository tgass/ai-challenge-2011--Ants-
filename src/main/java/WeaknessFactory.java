import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class WeaknessFactory {
	
	public static final int DEPTH = 3;
	
	private int weakness = 0;
	
	public int getWeakness(Field field, int locationId) {
		Set<Node> discovered = new LinkedHashSet<Node>();
		Node start = new Node(locationId, field, 0);
		discovered.add(start);
		Queue<Node> frontier = new LinkedList<Node>();
		frontier.add(start);
		while(!frontier.isEmpty()) {
			Node head = frontier.poll();
			if(head.getDistance() > DEPTH) {
				continue;
			}
			processNode(head, field, locationId);
			for(Integer edgeId : field.getEdges(head.getId())) {
				Node edgeNode = new Node(edgeId, field, head.getDistance()+1);
				if(!discovered.contains(edgeNode)) {
					frontier.add(edgeNode);
					discovered.add(edgeNode);
				}
			}
		}
		return weakness;
	}
	
	private void processNode(Node node, Field field, int locationId) {
		if(field.getDistance2(node.getId(), locationId) <= 5) {
			int enemyInfluence = field.getEnemyInfluence(node.getId());
			if(enemyInfluence > weakness) {
				weakness = enemyInfluence;
			}
		}
	}
	
}
