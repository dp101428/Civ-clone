package code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class AICore {
	AiPlayer civ;
	int numCities;
	public AICore(AiPlayer civ) {
		this.civ = civ;
	}
	
	public ArrayList<WeightedVal> getCityLocations(){
		ArrayList<WeightedVal> locations = new ArrayList<WeightedVal>();
		ArrayList<Tile> currentCityLocations = new ArrayList<Tile>();
		for(Player player : Game.allPlayers){
			for(City city : player.cities)
				currentCityLocations.add(city.location);
		}
		ArrayList<WeightedVal> values = new ArrayList<WeightedVal>();
		for(Tile[] column : Game.map){
			tileSearch:
			for(Tile targetTile : column){
				double shortestDistance = 0.0;
				for(Tile cityTile: currentCityLocations){
					if(Math.abs(targetTile.x - cityTile.x) < 5 || Math.abs(targetTile.y - cityTile.y) < 5)
						continue tileSearch;
					
				}
				if(civ.cities.size() != 0){
					for(City city: civ.cities){
						double dist = targetTile.getDistance(city.location);
						if(shortestDistance == 0.0 || dist<shortestDistance)
							shortestDistance = dist;
					}
				}
				else{
					for(Unit unit : civ.units)
						if(unit instanceof Settler){
							double dist = targetTile.getDistance(unit.location);
							if(shortestDistance == 0.0 || dist<shortestDistance)
								shortestDistance = dist;
						}
				}
					
				HashSet<Tile> potentialSurround = City.getSurround(targetTile.x, targetTile.y, Game.map);
				double totalWeight = 0.0;
				for(Tile tile:potentialSurround){
					totalWeight += tile.type.foodYield * 2 + tile.type.comYield * 0.2 + tile.type.prodYield;
				}
				shortestDistance -= 5;
				if(shortestDistance < 1)
					shortestDistance = 1.0;
				totalWeight *= 1/(shortestDistance);
				//System.out.println(shortestDistance);
				//System.out.println(totalWeight);
				if(shortestDistance < 50){
					values.add(new WeightedVal(0, totalWeight, targetTile));
					//System.out.println("Added");
				}
			}
		}
		//fun stats stuff starts here
		double averageWeight = 0.0;
		//System.out.println(values.size());
		//System.out.println(values.get(0).weight);
		for(WeightedVal value : values){
			averageWeight += value.weight;
			//System.out.println(value.weight);
		}
		//System.out.println(averageWeight);
		averageWeight /= values.size();
		System.out.println(averageWeight);
		double standardDeviation = 0.0;
		for(WeightedVal value: values)
			standardDeviation += (value.weight - averageWeight) * (value.weight - averageWeight);
		standardDeviation = Math.sqrt(standardDeviation / (values.size()-1));
		System.out.println(standardDeviation);
		WeightedVal highVal = null;
		for(WeightedVal value: values){
			value.tScore =  (value.weight-averageWeight)/standardDeviation;
			if(value.tScore > 5)
				locations.add(value);
			if(highVal == null || value.tScore > highVal.tScore)
				highVal = value;
		}
		if(locations.size() == 0)
			locations.add(highVal);
		//for(WeightedVal val : locations)
			//System.out.println(val);
		locations.sort(new ValComparer());
		AdjacentFinder:
		for(int i = 1; i < locations.size(); i++){
			Tile locationTile = ((Tile)locations.get(i).obj);
			for(int j = 0; j < i; j++){
				Tile priorLocationTile = ((Tile)locations.get(j).obj);
				if(priorLocationTile.x > locationTile.x - 2 && priorLocationTile.x < locationTile.x +2 && priorLocationTile.y > locationTile.y -2 && priorLocationTile.y < locationTile.y +2){
					locations.remove(i);
					i--;
					continue AdjacentFinder;
				}
			}
		}
		return locations;
	}
	
	public ArrayList<Tile> aStar(Unit unit){
		Tile goal = unit.assignment.location;
		HashMap<Tile, Tile> cameFrom = new HashMap<Tile, Tile>(4225);
		HashMap<Tile, Double> costTo = new HashMap<Tile, Double>(4225);
		costTo.put(unit.location, 0.0);
		HashMap<Tile, Double> fScore = new HashMap<Tile, Double>(4225);
		fScore.put(unit.location, hVal(unit.location, goal));
		TreeSet<TileNode> knownTiles = new TreeSet<TileNode>();
		knownTiles.add(new TileNode(unit.location, fScore));
		TreeSet<TileNode> doneTiles = new TreeSet<TileNode>();
		while(knownTiles.size()!=0){
			TileNode current = knownTiles.pollFirst();
			//System.out.println(current);
			if(current.self == goal){
				System.out.println("foundPath");
				return reconstructPath(cameFrom, current.self);
			}
			doneTiles.add(current);
			//if(knownTiles.remove(current))
				//System.out.println("removed");
			//else{
				//System.out.println("failed");
				//knownTiles.remove(current);
			//}
			ArrayList<Tile> adjacentTiles = new ArrayList<Tile>();
			for(int i = current.self.x -1; i < current.self.x +2 && i < Game.map.length; i++){
				for(int j = current.self.y -1; j < current.self.y +2 && j < Game.map[0].length; j++){
					if(i >= 0 && j >= 0 && Game.map[i][j] != current.self)
						adjacentTiles.add(Game.map[i][j]);
				}
			}
			for(Tile adjacent : adjacentTiles){
				TileNode nodeVer = new TileNode(adjacent, fScore);
				if(doneTiles.contains(nodeVer))
					continue;
				knownTiles.add(nodeVer);
				double tentativegScore = costTo.getOrDefault(current.self, Double.POSITIVE_INFINITY) + adjacent.type.moveCost;
				if(tentativegScore > costTo.getOrDefault(adjacent, Double.POSITIVE_INFINITY))
					continue;
				cameFrom.put(adjacent, current.self);
				costTo.put(adjacent, tentativegScore);
				fScore.put(adjacent, tentativegScore + hVal(adjacent, goal));
			}
		}
		System.out.println(goal + " is unreachable");
		return null;
	}
	public static double hVal(Tile start, Tile goal){
		if(Math.abs(start.x - goal.x) > Math.abs(start.y - goal.y))
			return Math.abs(start.x - goal.x);
		else
			return Math.abs(start.y - goal.y);
	}
	
	public ArrayList<Tile> reconstructPath(HashMap<Tile,Tile> cameFrom, Tile current){
		ArrayList<Tile>totalPath = new ArrayList<Tile>();
		totalPath.add(current);
		while(cameFrom.containsKey(current)){
			current = cameFrom.get(current);
			totalPath.add(current);
		}
		return totalPath;
	}
}
