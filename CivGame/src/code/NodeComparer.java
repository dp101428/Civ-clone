package code;

import java.util.Comparator;
import java.util.HashMap;

public class NodeComparer implements Comparator<Tile> {
	HashMap<Tile, Double> fScore;
	Tile goal;
	public NodeComparer(HashMap<Tile, Double> fScore, Tile goal) {
		this.fScore = fScore;
		this.goal = goal;
	}

	@Override
	public int compare(Tile sorter, Tile stable) {
		return (int)(fScore.getOrDefault(sorter, Double.POSITIVE_INFINITY) - fScore.getOrDefault(stable, Double.POSITIVE_INFINITY));
		//return (int)(AICore.hVal(sorter, goal) + costTo.get(sorter) - AICore.hVal(stable, goal) - costTo.get(stable));
	}
	
}
