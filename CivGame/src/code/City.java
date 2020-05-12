//Represents a city on the map

package code;

import java.util.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class City extends Selectable {
	int yields[], pop, currProd, assignmentPercs[], locPop, locProd, locGold, locSci;
	boolean changedAssign[], displayingOptions;
	ProductionOption currItem;
	ArrayList<ProductionOption> compBuilds;
	ArrayList<PotentialAssignment> tasks;
	Task currentTask;
	
	public City(Player player, int x, int y){
		super(player, Game.map[x][y]);
		Set<Tile> surroundings = getSurround(x,y, Game.map);//using a set because method for getting surroundings would result in double-counting otherwise
		yields = new int[3];
		for(Tile tile : surroundings){//sums up the yield from each tile in the area
			yields[0] += tile.type.foodYield;
			yields[1] += tile.type.comYield;
			yields[2] += tile.type.prodYield;
		}
		this.pop = 1000;
		location.occupiers.add(this);//adds itself to the place it lives
		assignmentPercs = new int[3];
		assignmentPercs[0] = 34;//34 because otherwise 1% INEFFICIENCY
		assignmentPercs[1] = 33;
		assignmentPercs[2] = 33;
		changedAssign = new boolean[3];
		currItem = owner.noSelect;
		compBuilds = new ArrayList<ProductionOption>();
		locPop = 1;
		locGold = 1;
		locSci = 1;
		locProd = 1; 
		tasks = new ArrayList<PotentialAssignment>();
	}
	
	//gets the 25 tiles in the first 2 rings around the city
	public static HashSet<Tile> getSurround(int x, int y, Tile[][] map){
		HashSet<Tile> output = new HashSet<Tile>(24);
		ArrayList<Tile> eightRound = new ArrayList<Tile>();//need an arraylist because the set is unsorted and so can't loop over the first 8 of it
		Tile cityTile = map[x][y];
		for(Tile[] row : map){
			for(Tile tile : row){
				if(tile.isAdjacentTo(cityTile)){//loops over the entire map and checks if adj to centre tile, if so, adds to both list and set
					output.add(tile);
					eightRound.add(tile);
				}
			}
		}
		
		for(Tile adj : eightRound){//loops through the arraylist and gets everything adj to the first ring, adds it.
			for(Tile[] row : map){
				for(Tile tile : row){
					if(tile.isAdjacentTo(adj))
						output.add(tile);
				}
			}
		}
		output.remove(cityTile);//takes out the centre
		return output;
	}
	
	public void render(Graphics g){
		Camera camera = Camera.getInstance();
		g.setColor(this.owner.playerColour);
		g.drawOval(location.x * 40 - camera.getX(), location.y * 40 - camera.getY(), 40, 40);
	}
	
	//Runs at end of turn, handles everything
	public void updateCity(){
		pop += (pop * assignmentPercs[0] * yields[0] -Math.pow(pop, 2))/3500 * locPop * owner.globFood * owner.eff;//increases population, people eat
		currProd += pop * assignmentPercs[1] * yields[1]/132000 * locProd * owner.globProd * owner.eff;
		boolean madeSomething = false;
		while(currProd >= currItem.cost){//while loop to allow for multiple completed productions in one turn
			currProd -= currItem.cost;
			currItem.complete.complete(this);
			if(currItem.once){
				this.compBuilds.add(currItem);
				this.currItem = this.owner.noSelect;
				break;//needed to stop exploit allowing for multiple buildings of the same type to be completed if it all happens in one turn
			}
			madeSomething = true;
		}
		if(madeSomething && this.owner instanceof AiPlayer){
			for(Task task : ((AiPlayer)this.owner).taskList){
				if(task.location == this.currentTask.location)
					task.assigned = false;
			}
			((AiPlayer)owner).taskList.remove(this.currentTask);
			for(int i = 0; i < this.tasks.size(); i++){
				if(tasks.get(i).task == this.currentTask){
					tasks.remove(i);
					i--;
				}
			}
			for(Task task : currentTask.dependentTasks){
				task.assigned = false;
			}
			this.currentTask = null;
			this.currItem = owner.noSelect;
		}
		
		owner.totGold += pop * assignmentPercs[2] * yields[2] * 7/3/24/100 * locGold * owner.globGold * owner.eff - 100;//need to make less money
		owner.totResearch += pop/100 * owner.globSci * this.locSci * owner.eff;//science entirely based on population because no other way to measure innovation
		this.owner.sciPerTurn += pop/100 * owner.globSci * this.locSci * owner.eff;//science/turn tracked separately;
	}
	
	//used to calculate how many once-only options were completed out of production options before an index to make display work correctly
	public int numRemoved(int index){
		int output = 0;
		for(int i = 0; i < index; i++){
			if(this.compBuilds.contains(owner.options.get(i)))
				output++;
		}
		return output;
	}
	
	//used to calcualte the number of contiguous once-only options at an index to make clicking work right
	public int numContigue(int index){
		int output = 0;
		for(int i = index; i < owner.options.size(); i++){
			if(this.compBuilds.contains(owner.options.get(i)))
				output++;
			else
				break;
		}
		return output;
	}
}
