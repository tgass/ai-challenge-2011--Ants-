import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class EnemyInfluenceSetter {
	
	public static final int DEPTH = 4;
	
	public static void createInfluence(Field field, int locationId) {
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
	}
	
	private static void processNode(Node node, Field field, int locationId) {
		if(field.getDistance2(node.getId(), locationId) <= 10) {
			field.addEnemyInfluence(node.getId());
		}
	}
	
}
