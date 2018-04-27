package pathexpression;

import com.google.common.base.Stopwatch;

import test.IntGraph;

public class Main {

	public static void main(String[] args) {
	    IntGraph g = new IntGraph();
	    g.addEdge(1, "a", 2);
	    g.addEdge(2, "v", 4);
	    g.addEdge(1, "c", 3);
	    g.addEdge(3, g.epsilon(), 4);
	    Stopwatch stopwatch = Stopwatch.createStarted();
	    PathExpressionComputer<Integer, String> expr = new PathExpressionComputer<Integer, String>(g);
	    IRegEx<String> expression1 = expr.getExpressionBetween(1, 4);
	    IRegEx<String> expression2 = expr.getExpressionBetween(2, 4);
	}

}
