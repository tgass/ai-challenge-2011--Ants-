import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class PathFactory {
	
	public static Path search(Field field, Env env, boolean reverse, int startId, Heuristic h, int depth) {
		Set<Integer> discovered = new HashSet<Integer>();
		PriorityQueue<Path> frontier = new PriorityQueue<Path>();
		Map<Integer, Double> costMap = new HashMap<Integer, Double>(); // id / cost
		
		discovered.add(startId);
		frontier.add(new Path(new Edge(-1, startId), reverse, h.getCostEstimate(startId)));
		costMap.put(startId, 0d);

		while (!frontier.isEmpty()) {
			Path p = frontier.poll();
			Integer headId = p.getHead();
			if (p.getLength() >= depth) {
				return p;
			}
			//checkt ob der 1. Schritt blockiert ist
			if(p.getLength() - 1 == 1 && field.containsAnt(headId))	 {
				continue;
			}
			for (Integer edgeId : field.getEdges(headId)) {
				Path newPath = p.add(new Edge(headId, edgeId), h.getCostEstimate(edgeId));
				if (!discovered.contains(edgeId)) {
					discovered.add(edgeId);
					frontier.add(newPath);
					costMap.put(edgeId, newPath.getEvaluation());
				} else if (discovered.contains(edgeId) && costMap.get(edgeId) > newPath.getEvaluation()) {
					frontier.remove(edgeId);
					frontier.add(newPath);
					costMap.put(edgeId, newPath.getEvaluation());
				}
			}
		}
		return null;
	}

}
