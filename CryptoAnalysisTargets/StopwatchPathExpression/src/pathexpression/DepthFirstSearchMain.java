package pathexpression;

import java.util.Set;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;

import test.IntGraph;

public class DepthFirstSearchMain {

	public static void main(String[] args) {
	    IntGraph g = new IntGraph();
	    g.addEdge(1, "a", 2);
	    g.addEdge(2, "v", 4);
	    g.addEdge(1, "c", 3);
	    g.addEdge(3, g.epsilon(), 4);
	    Set<Integer> visited = Sets.newHashSet();
	    Stopwatch watch = Stopwatch.createStarted();
	    dfsFrom(g,1, visited, watch);
	}

	private static void dfsFrom(IntGraph g, int i, Set<Integer> visited, Stopwatch watch) {
		for(Integer j : g.getOutEdges(i)) {
			if(!visited.contains(j)) {
				dfsFrom(g, j, visited, watch);
			}
			visited.add(j);
		}
		watch.stop();
	}
}
