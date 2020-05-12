package code;

import java.util.HashMap;

public class TileNode  implements Comparable<TileNode>{
	Tile self;
	HashMap<Tile, Double> fScore;
	//tileNode priorNode;
	public TileNode(Tile self, HashMap<Tile, Double> fScore) {
		this.self = self;
		this.fScore = fScore;
	}
	@Override
	public int compareTo(TileNode other) {
		if(fScore.getOrDefault(self, Double.POSITIVE_INFINITY) == fScore.getOrDefault(other.self, Double.POSITIVE_INFINITY)){
			return self.id - other.self.id;
		}
		return (int)(fScore.getOrDefault(self, 0.0)* 1000000000 + self.id - other.self.id - 1000000000  *  fScore.getOrDefault(other.self, 0.0));
	}
	
	@Override
	public boolean equals(Object other){
		if(fScore.getOrDefault(self, Double.POSITIVE_INFINITY) == fScore.getOrDefault(((TileNode)other).self, Double.POSITIVE_INFINITY))
			return self == ((TileNode)other).self;
		//return true;
		return fScore.getOrDefault(self, 0.0) * 1000000000 + self.id  == fScore.getOrDefault(((TileNode)other).self, 0.0) * 1000000000 + ((TileNode)other).self.id;
	}
	
}
