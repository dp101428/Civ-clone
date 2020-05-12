package code;
//runs the basic game loop
import java.util.ArrayList;
import java.util.Set;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

public class Game extends StateBasedGame{
	
	public static Tile[][] map;
	public static ArrayList<Player> allPlayers;
	
	boolean isMoving, isChangingProd;
	int turnCount, currIndex;
	public static Player currPlayer;
	public static Tech none;
	public static TileType[] tiles;
	
	
	
	
	
	public Game() {
		super("Civilization");
		
	}
	
	public static void main(String[] args){
	      try{
	    	 AppGameContainer appgc = new AppGameContainer(new Game());//weird magic that makes the game work, I'm not questioning it
	         appgc.setDisplayMode(800, 600, false);
	         appgc.setShowFPS(false);
	         appgc.start();
	      }catch(SlickException e){
	         e.printStackTrace();
	      }
	}
	
	static{
		TileType[] tiles = new TileType[3];
		tiles[1] = new TileType(1, 2, 2, 1, Color.orange);
		tiles[0] = new TileType(1, 0, 4, 2, Color.green);
		tiles[2] = new TileType(2, 4, 1, 0, Color.gray);
		map = new Tile[65][65];
		int id = 0;
		for(int i = 0; i < map.length; i++){
			for(int j = 0; j < map[0].length; j++){
				//map[i][j] = new Tile(tiles[(int)(Math.random()*3)], i, j);
				map[i][j] = new Tile(i, j, id);
				id++;
			}
		}
		
		Tile[][] watchableMap = map;
		
		map[0][0].height = (int)(Math.random() * 100);
		map[0][map[0].length-1].height = (int)(Math.random() * 100);
		map[map.length -1][0].height = (int)(Math.random() * 100);
		map[map.length -1][map[0].length -1].height = (int)(Math.random() * 100);
		generateMapHeight(0,0, map.length -1, map[0].length -1, 4);
		map[0][0].rainfall = (int)(Math.random() * 100);
		map[0][map[0].length-1].rainfall = (int)(Math.random() * 100);
		map[map.length -1][0].rainfall = (int)(Math.random() * 100);
		map[map.length -1][map[0].length -1].rainfall = (int)(Math.random() * 100);
		generateMapRainfall(0,0, map.length -1, map[0].length -1, 4);
		for(int i = 0; i < map.length; i++){
			for(int j = 0; j < map[0].length; j++){
				//map[i][j] = new Tile(tiles[(int)(Math.random()*3)], i, j);
				if(map[i][j].height < 0)
					map[i][j].height = 0;
				if(map[i][j].rainfall < 0)
					map[i][j].rainfall = 0;
				map[i][j].rainfall *= 1.0 - map[i][j].height/100.0;
				Color tileColourMaths = new Color(Color.yellow);
				float adjRainfall = (float)map[i][j].rainfall/100;
				tileColourMaths.add(new Color( tileColourMaths.r-adjRainfall, adjRainfall, adjRainfall /2));
				Color tileColour = colourWeighter(map[i][j]);
				map[i][j].type = new TileType(map[i][j].height/50 + 1,map[i][j].height/25 + 1,4 - map[i][j].height/100 * 6,5 - map[i][j].height/25,tileColour);
			}
		}
		
	}
	
	public static void generateMap(int xMin, int yMin, int xMax, int yMax, double mag){
		Tile[][] watchableMap = map;
		//gets the centre point of the square "diamond step"
		Tile diaCentre = map[(xMax - xMin)/2 + xMin][(yMax - yMin)/2 + yMin];
		//averages its value with that of the 4 surrounding it
		diaCentre.genVal = ((map[xMin][yMin].genVal + map[xMin][yMax].genVal + map[xMax][yMin].genVal + map[xMax][yMax].genVal)/4 + (int)(Math.random() * mag * 100)) % 100;
		//then we get the 4 new square corners
		Tile top = map[(xMax-xMin)/2 + xMin][yMin];
		Tile bottom = map[(xMax-xMin)/2 + xMin][yMax];
		Tile left = map[xMin][(yMax - yMin)/2 + yMin];
		Tile right = map[xMax][(yMax - yMin)/2 + yMin];
		
		//generates values for the new corners
		top.genVal = Math.abs(((map[Math.abs((top.x + (xMax - xMin)/2))%map.length][top.y].genVal + map[Math.abs((top.x - (xMax - xMin)/2)%map.length)][top.y].genVal + map[top.x][Math.abs((top.y + (yMax -yMin)/2)%map[0].length)].genVal + map[top.x][Math.abs((top.y - (yMax -yMin)/2)%map[0].length)].genVal)/4+ (int)(Math.random() * mag * 100 - 50 * mag))%100);
		bottom.genVal = Math.abs(((map[Math.abs((bottom.x + (xMax - xMin)/2)%map.length)][bottom.y].genVal + map[Math.abs((bottom.x - (xMax - xMin)/2)%map.length)][bottom.y].genVal + map[bottom.x][Math.abs((bottom.y + (yMax -yMin)/2)%map[0].length)].genVal + map[bottom.x][Math.abs((bottom.y - (yMax -yMin)/2)%map[0].length)].genVal)/4+ (int)(Math.random() * mag * 100- 50 * mag))%100);
		right.genVal = Math.abs(((map[Math.abs((right.x + (xMax - xMin)/2)%map.length)][right.y].genVal + map[Math.abs((right.x - (xMax - xMin)/2)%map.length)][right.y].genVal + map[right.x][Math.abs((right.y + (yMax -yMin)/2)%map[0].length)].genVal + map[right.x][Math.abs((right.y - (yMax -yMin)/2)%map[0].length)].genVal)/4+ (int)(Math.random() * mag * 100- 50 * mag)) %100);
		left.genVal = Math.abs(((map[Math.abs((left.x + (xMax - xMin)/2)%map.length)][left.y].genVal + map[Math.abs((left.x - (xMax - xMin)/2)%map.length)][left.y].genVal + map[left.x][Math.abs((left.y + (yMax -yMin)/2)%map[0].length)].genVal + map[left.x][Math.abs((left.y - (yMax -yMin)/2)%map[0].length)].genVal)/4+ (int)(Math.random() * mag * 100- 50 * mag)) %100);
		
		if(xMax -xMin <= 2 || yMax -yMin <= 2)
			return;
		//System.out.println(map[0][0]);
		//starts the next step
		generateMap(xMin, yMin, xMax - (xMax - xMin)/2, yMax - (yMax-yMin) /2, mag/2);
		generateMap((xMax - xMin)/2 + xMin, yMin, xMax,  yMax -(yMax -yMin)/2, mag/2);
		generateMap(xMin, (yMax-yMin)/2 + yMin, xMax - (xMax - xMin)/2, yMax, mag/2);
		generateMap((xMax - xMin)/2 + xMin, (yMax-yMin)/2 + yMin, xMax, yMax,  mag/2);
	}
	
	public static void generateMapHeight(int xMin, int yMin, int xMax, int yMax, double mag){
		Tile[][] watchableMap = map;
		//gets the centre point of the square "diamond step"
		Tile diaCentre = map[(xMax - xMin)/2 + xMin][(yMax - yMin)/2 + yMin];
		//averages its value with that of the 4 surrounding it
		diaCentre.height = ((map[xMin][yMin].height + map[xMin][yMax].height + map[xMax][yMin].height + map[xMax][yMax].height)/4 + (int)(Math.random() * mag * 100)) % 100;
		//then we get the 4 new square corners
		Tile top = map[(xMax-xMin)/2 + xMin][yMin];
		Tile bottom = map[(xMax-xMin)/2 + xMin][yMax];
		Tile left = map[xMin][(yMax - yMin)/2 + yMin];
		Tile right = map[xMax][(yMax - yMin)/2 + yMin];
		
		//generates values for the new corners
		top.height = ((map[Math.abs((top.x + (xMax - xMin)/2))%map.length][top.y].height + map[Math.abs((top.x - (xMax - xMin)/2)%map.length)][top.y].height + map[top.x][Math.abs((top.y + (yMax -yMin)/2)%map[0].length)].height + map[top.x][Math.abs((top.y - (yMax -yMin)/2)%map[0].length)].height)/4+ (int)(Math.random() * mag * 100 - 50 * mag))%100;
		bottom.height = ((map[Math.abs((bottom.x + (xMax - xMin)/2)%map.length)][bottom.y].height + map[Math.abs((bottom.x - (xMax - xMin)/2)%map.length)][bottom.y].height + map[bottom.x][Math.abs((bottom.y + (yMax -yMin)/2)%map[0].length)].height + map[bottom.x][Math.abs((bottom.y - (yMax -yMin)/2)%map[0].length)].height)/4+ (int)(Math.random() * mag * 100- 50 * mag))%100;
		right.height = ((map[Math.abs((right.x + (xMax - xMin)/2)%map.length)][right.y].height + map[Math.abs((right.x - (xMax - xMin)/2)%map.length)][right.y].height + map[right.x][Math.abs((right.y + (yMax -yMin)/2)%map[0].length)].height + map[right.x][Math.abs((right.y - (yMax -yMin)/2)%map[0].length)].height)/4+ (int)(Math.random() * mag * 100- 50 * mag)) %100;
		left.height = ((map[Math.abs((left.x + (xMax - xMin)/2)%map.length)][left.y].height + map[Math.abs((left.x - (xMax - xMin)/2)%map.length)][left.y].height + map[left.x][Math.abs((left.y + (yMax -yMin)/2)%map[0].length)].height + map[left.x][Math.abs((left.y - (yMax -yMin)/2)%map[0].length)].height)/4+ (int)(Math.random() * mag * 100- 50 * mag)) %100;
		
		if(xMax -xMin <= 2 || yMax -yMin <= 2)
			return;
		//System.out.println(map[0][0]);
		//starts the next step
		generateMapHeight(xMin, yMin, xMax - (xMax - xMin)/2, yMax - (yMax-yMin) /2, mag/2);
		generateMapHeight((xMax - xMin)/2 + xMin, yMin, xMax,  yMax -(yMax -yMin)/2, mag/2);
		generateMapHeight(xMin, (yMax-yMin)/2 + yMin, xMax - (xMax - xMin)/2, yMax, mag/2);
		generateMapHeight((xMax - xMin)/2 + xMin, (yMax-yMin)/2 + yMin, xMax, yMax,  mag/2);
	}
	
	public static void generateMapRainfall(int xMin, int yMin, int xMax, int yMax, double mag){
		Tile[][] watchableMap = map;
		//gets the centre point of the square "diamond step"
		Tile diaCentre = map[(xMax - xMin)/2 + xMin][(yMax - yMin)/2 + yMin];
		//averages its value with that of the 4 surrounding it
		diaCentre.rainfall = ((map[xMin][yMin].rainfall + map[xMin][yMax].rainfall + map[xMax][yMin].rainfall + map[xMax][yMax].rainfall)/4 + (int)(Math.random() * mag * 100)) % 100;
		//then we get the 4 new square corners
		Tile top = map[(xMax-xMin)/2 + xMin][yMin];
		Tile bottom = map[(xMax-xMin)/2 + xMin][yMax];
		Tile left = map[xMin][(yMax - yMin)/2 + yMin];
		Tile right = map[xMax][(yMax - yMin)/2 + yMin];
		
		//generates values for the new corners
		top.rainfall = ((map[Math.abs((top.x + (xMax - xMin)/2))%map.length][top.y].rainfall + map[Math.abs((top.x - (xMax - xMin)/2)%map.length)][top.y].rainfall + map[top.x][Math.abs((top.y + (yMax -yMin)/2)%map[0].length)].rainfall + map[top.x][Math.abs((top.y - (yMax -yMin)/2)%map[0].length)].rainfall)/4+ (int)(Math.random() * mag * 100 - 50 * mag))%100;
		bottom.rainfall = ((map[Math.abs((bottom.x + (xMax - xMin)/2)%map.length)][bottom.y].rainfall + map[Math.abs((bottom.x - (xMax - xMin)/2)%map.length)][bottom.y].rainfall + map[bottom.x][Math.abs((bottom.y + (yMax -yMin)/2)%map[0].length)].rainfall + map[bottom.x][Math.abs((bottom.y - (yMax -yMin)/2)%map[0].length)].rainfall)/4+ (int)(Math.random() * mag * 100- 50 * mag))%100;
		right.rainfall = ((map[Math.abs((right.x + (xMax - xMin)/2)%map.length)][right.y].rainfall + map[Math.abs((right.x - (xMax - xMin)/2)%map.length)][right.y].rainfall + map[right.x][Math.abs((right.y + (yMax -yMin)/2)%map[0].length)].rainfall + map[right.x][Math.abs((right.y - (yMax -yMin)/2)%map[0].length)].rainfall)/4+ (int)(Math.random() * mag * 100- 50 * mag)) %100;
		left.rainfall = ((map[Math.abs((left.x + (xMax - xMin)/2)%map.length)][left.y].rainfall + map[Math.abs((left.x - (xMax - xMin)/2)%map.length)][left.y].rainfall + map[left.x][Math.abs((left.y + (yMax -yMin)/2)%map[0].length)].rainfall + map[left.x][Math.abs((left.y - (yMax -yMin)/2)%map[0].length)].rainfall)/4+ (int)(Math.random() * mag * 100- 50 * mag)) %100;
		
		if(xMax -xMin <= 2 || yMax -yMin <= 2)
			return;
		//System.out.println(map[0][0]);
		//starts the next step
		generateMapRainfall(xMin, yMin, xMax - (xMax - xMin)/2, yMax - (yMax-yMin) /2, mag/2);
		generateMapRainfall((xMax - xMin)/2 + xMin, yMin, xMax,  yMax -(yMax -yMin)/2, mag/2);
		generateMapRainfall(xMin, (yMax-yMin)/2 + yMin, xMax - (xMax - xMin)/2, yMax, mag/2);
		generateMapRainfall((xMax - xMin)/2 + xMin, (yMax-yMin)/2 + yMin, xMax, yMax,  mag/2);
	}
	
	public static Color averageColour(Color a, Color b){
		return new Color((a.r + b.r) /2, (a.b + b.b)/2, (a.g + b.g)/2);
	}
	
	public static Color colourWeighter(Tile tile){
		double whiteWeight = 1;
		whiteWeight = 1.0/Math.sqrt(tile.rainfall/100.0 * tile.rainfall/100.0 + (1.0 - tile.height/100.0) *(1.0-tile.height/100.0))/ 100;
		if(whiteWeight == Double.POSITIVE_INFINITY)
			whiteWeight = 1.0;
		double yellowWeight = 1.0;
		yellowWeight = 1.0/Math.sqrt(tile.rainfall/100.0 * tile.rainfall/100.0 + tile.height/100.0 * tile.height/100.0) / 100;
		if(yellowWeight == Double.POSITIVE_INFINITY)
			yellowWeight = 1.0;
		double greenWeight = 1.0;
		greenWeight = 1.0/Math.sqrt((1.0-tile.rainfall/100.0) * (1.0-tile.rainfall/100.0) + tile.height/100.0 * tile.height/100.0)/ 100;
		if(greenWeight == Double.POSITIVE_INFINITY)
			greenWeight = 1.0;
		double normaliser = greenWeight + yellowWeight + whiteWeight;
		greenWeight /= normaliser;
		yellowWeight /= normaliser;
		whiteWeight /= normaliser;
		//return new Color(Color.black);
		//System.out.println("" + whiteWeight + " " + yellowWeight + " " +  greenWeight);
		return new Color((float)(Color.white.r * whiteWeight + Color.yellow.r * yellowWeight + Color.green.r * greenWeight), (float)(Color.white.g * whiteWeight + Color.green.g * greenWeight + Color.yellow.g * yellowWeight), (float)(Color.white.b * whiteWeight + Color.green.b * greenWeight + Color.yellow.b * yellowWeight));
	}
	
	/*
	//draws everything
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		if(!isChangingResearch){//Basically 2 modes of rendering, rendering map and rendering tech
			for(int i = 0; i < map.length; i++){//renders the tiles
				for(int j = 0; j < map.length; j++)
					map[i][j].render(g);
			}
			for(Player player : allPlayers){//renders the units
				for(Unit unit : player.units)//gives the drawing function for the units everything that it needs
					unit.template.renderModel.draw(g, unit.position.x, unit.position.y, unit.owner, unit.isSelected, unit.totMove, unit.template, unit.health);
				for(City city : player.cities)
					city.render(g);//draws the cities
			}
		}
		
		else{
			for(int i = 0; i < options.length; i++){
				for(int j = 0; j < options[i].length; j++)
					if(options[i][j] != null)//draws all techs that exist in the grid
						options[i][j].render(i * 220 + 40, j * 30 + 40, g);
			}
			g.drawString("First Era", 20, 10);
			g.drawString("Second Era", 20, 190);
			g.drawString("Third Era", 20, 370);
			g.drawString("Fourth Era", 20, 510);
			
		}
		int timeToResearch = 999;
		try{//research per turn will be zero when all they have is a settler, so a try/catch is needed
			timeToResearch = (currPlayer.currentResearch.cost-currPlayer.totResearch)/currPlayer.sciPerTurn;
		}
		catch(ArithmeticException a){
			timeToResearch = 999;
		}
		
		
		//UI buttons and words, nothing interesting here
		g.setColor(Color.yellow);//highlights what buttons they are using
		if(isMoving)
			g.drawRect(600, 20, 100, 20);
		if(isChangingProd)
			g.drawRect(600, 80, 100, 20);
		g.setColor(Color.cyan);
		g.fillRect(600, 580, 100, 20);
		g.fillRect(600, 500, 136, 20);
		g.setColor(Color.white);
		g.drawString("End turn", 600, 580);
		g.drawString("" + turnCount, 700, 580);
		g.drawString("Gold: " + currPlayer.totGold, 600, 560);
		g.drawString("Change research", 600, 500);
		g.drawString("Current Research", 600, 520);
		g.drawString(currPlayer.currentResearch.name + " (" + timeToResearch + " turns)", 600, 540);
		g.drawString(currPlayer.name + "'s turn", 600, 480);
	}
	*/
	
	/*
	//constructs the game
	@Override
	public void init(GameContainer container) throws SlickException {
		tiles = new TileType[3];
		none = new Tech(10000000, "No tech selected", currPlayer -> currPlayer.totResearch = 0, 1);//needed to allow for transitions between techs
		
		tiles[0] = new TileType(1, 1, 2, 0, Color.orange);
		tiles[1] = new TileType(1, 0, 4, 2, Color.green);
		tiles[2] = new TileType(2, 4, 1, 1, Color.gray);
		map = new Tile[15][15];
		for(int i = 0; i < 15; i++){
			for(int j = 0; j < 15; j++)
				map[i][j] = new Tile(tiles[(int)(Math.random()*3)], i, j);
		}
		
		turnCount = 1;
		
		allPlayers = new ArrayList<Player>();
		allPlayers.add(new Player(Color.red, "Russia"));
		allPlayers.add(new Player(Color.blue, "America"));
		
		for(int i = 0; i < allPlayers.size(); i++){
			if(i == 0){
				allPlayers.get(i).addSettler((int)(Math.random() * 15), (int)(Math.random() * 15), null);
				spawnNew(allPlayers.get(i), 0);
			}
			else
				spawnNew(allPlayers.get(i), 1);
		}
		
		currPlayer = allPlayers.get(0);
		currIndex = 0;
		
		
		//this doesn't need to be a variable, but I haven't moved it over to the constructor because doing so doesn't add anything
		UnitTemplate horseRider = new UnitTemplate(2,4,"Horseman",15, 
			new UnitDisp(){ 
			@Override 
			public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
			g.setColor(owner.playerColour);
			g.drawLine(x * 40 + 20, y * 40, x * 40 + 10, (y + 1) * 40);
			g.drawLine(x * 40 + 20, y * 40, x * 40 + 30, (y + 1) * 40);
			if(isSelected){
				g.fillRect(600, 20, 100, 20);
				g.setColor(Color.white);
				g.drawString("Move", 601, 20);
				g.drawString("Movement left: " +totMove, 600, 40);
				g.drawString(owner.name + "'s " + template.typeName, 600, 0);
				g.drawString("Strength : "+template.strength, 600, 60);
				g.drawString("Health" + health, 600, 80);
			}
			}
			
		});
		
		
		
		
		//has to be defined outside of tech because it intereacts with multiple techs
		ProductionOption factory = new ProductionOption(800, "Build Factory", new ProdResolver(){
			@Override
			public void complete(City city){
				if(city.owner.researched.contains(options[1][12]))
					city.locProd += 9;
				else
					city.locProd += 5;
			}
		}, true);
		options = new Tech[3][18];
		//defining all of the researchble techs, is awkward in a large block but having a separate method to define all of them is pointless
		options[0][0] = new Tech(100, "Horse riding", currPlayer -> currPlayer.addOption(new ProductionOption(150, "Train Horseman", city -> city.owner.add(city.x, city.y, horseRider),false)), 1);
		options[0][1] = new Tech(100, "Agriculture", currPlayer -> currPlayer.addOption(new ProductionOption(300, "Farm surroundings", city -> city.locPop++, true)), 1);
		options[1][0] = new Tech(250, "Metalworking", new Tech[]{options[0][0], options[0][1]}, currPlayer -> currPlayer.addOption(new ProductionOption(200, "Train Swordsman", city -> currPlayer.add(city.x, city.y, new UnitTemplate(1,6,"Swordsman",20, new UnitDisp(){ 
			@Override 
			public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
			g.setColor(owner.playerColour);
			g.drawLine(x * 40 + 10, y * 40 + 10, x * 40 + 20, y * 40 + 5);
			g.drawLine(x * 40 + 30, y * 40 + 10, x * 40 + 20, y * 40 + 5);
			g.drawLine(x * 40 + 10, y * 40 + 10, x * 40 + 20, y * 40 + 30);
			g.drawLine(x * 40 + 30, y * 40 + 10, x * 40 + 20, y * 40 + 30);
			if(isSelected){
				g.fillRect(600, 20, 100, 20);
				g.setColor(Color.white);
				g.drawString("Move", 601, 20);
				g.drawString("Movement left: " +totMove, 600, 40);
				g.drawString(owner.name + "'s " + template.typeName, 600, 0);
				g.drawString("Strength: "+template.strength, 600, 60);
				g.drawString("Health: " + health, 600, 80);
				}
			}
			
		})), false)), 1);
		options[1][1] = new Tech(200, "Irrigation", new Tech[]{options[0][1]}, currPlayer -> currPlayer.globFood++, 1); 
		options[0][2] = new Tech(150, "Scholarship", player -> player.globSci += .2, 1);
		options[0][3] = new Tech(125, "Archery", player -> player.options.add(new ProductionOption(125, "Train Archer", city -> city.owner.add(city.x, city.y, new UnitTemplate(1, 3, "Archer", 15, new UnitDisp(){@Override
			public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
				g.setColor(owner.playerColour);
				g.drawLine(x * 40 + 30, y * 40 + 20, x * 40 + 20, y * 40 + 5);
				g.drawLine(x * 40 + 30, y * 40 + 20, x * 40 + 20, y * 40 + 35);
				g.drawLine(x * 40 + 10, y * 40 + 20, x * 40 + 35, y * 40 + 20);
				if(isSelected){
					g.fillRect(600, 20, 100, 20);
					g.setColor(Color.white);
					g.drawString("Move", 601, 20);
					g.drawString("Movement left: " +totMove, 600, 40);
					g.drawString(owner.name + "'s " + template.typeName, 600, 0);
					g.drawString("Strength: "+template.strength, 600, 60);
					g.drawString("Health: " + health, 600, 80);
				}
			}
		})), false)), 1);
		options[1][2] = new Tech(200, "Libraries", new Tech[]{options[0][2]}, player -> player.addOption(new ProductionOption(400, "Build Library", city -> city.locSci++, true)), 1);
		options[1][3] = new Tech(250, "Mathamatics", new Tech[]{options[0][2]}, player -> player.globGold += 0.5, 1);
		options[2][1] = new Tech(300, "Legalism", new Tech[]{options[0][2], options[1][1]}, player -> player.eff += 0.5, 1);//laws improve everything
		options[2][0] = new Tech(300, "Armouring", new Tech[]{options[1][0]}, player -> player.strMulti += 0.5, 1);
		options[2][2] = new Tech(300, "Engineering", new Tech[]{options[1][3]}, player -> player.addOption(new ProductionOption(500, "Build Aquaduct", city -> city.locPop += .5, true)),1);
		//Era 2 techs
		
		options[0][6] = new Tech(500, "Metallurgy", player ->player.addOption(new ProductionOption(250, "Train Knight", city->city.owner.add(city.x, city.y, new UnitTemplate(2, 7, "Knight", 40, new UnitDisp(){
			@Override 
			public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
			g.setColor(owner.playerColour);
			g.drawLine(x * 40 + 10, y * 40 + 10, x * 40 + 20, y * 40 + 5);
			g.drawLine(x * 40 + 30, y * 40 + 10, x * 40 + 20, y * 40 + 5);
			g.drawLine(x * 40 + 10, y * 40 + 10, x * 40 + 20, y * 40 + 30);
			g.drawLine(x * 40 + 30, y * 40 + 10, x * 40 + 20, y * 40 + 30);
			if(isSelected){
				g.fillRect(600, 20, 100, 20);
				g.setColor(Color.white);
				g.drawString("Move", 601, 20);
				g.drawString("Movement left: " +totMove, 600, 40);
				g.drawString(owner.name + "'s " + template.typeName, 600, 0);
				g.drawString("Strength: "+template.strength, 600, 60);
				g.drawString("Health: " + health, 600, 80);
			}
			}
			})) ,false)), 2);
		options[0][7] = new Tech(500, "Hoe" , player -> player.globFood += 1.5 , 2);
		options[0][8] = new Tech(500, "Printing", new Resolver(){
			@Override
			public void resolve(Player player){
				player.globGold += 1;
				player.globSci += 1;
			}
		}, 2);
		options[0][9] = new Tech(500, "Construction", player -> player.globProd += 1, 2);
		
		options[1][6] = new Tech(550, "Gunpowder", new Tech[]{options[0][6], options[0][7]}, new Resolver(){
			@Override
			public void resolve(Player player){
				player.strMulti++;
				player.options.add(new ProductionOption(150, "Train Musketman", city -> city.owner.add(city.x, city.y, new UnitTemplate(1, 9, "Musketman", 30, new UnitDisp(){
					@Override
					public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
						g.setColor(owner.playerColour);
						g.drawLine(x * 40 + 10, y * 40 + 10, x * 40 + 30, y * 40 + 20);
						g.drawLine(x * 40 + 10, y * 40 + 30, x * 40 + 30, y * 40 + 20);
						if(isSelected){
							g.fillRect(600, 20, 100, 20);
							g.setColor(Color.white);
							g.drawString("Move", 601, 20);
							g.drawString("Movement left: " +totMove, 600, 40);
							g.drawString(owner.name + "'s " + template.typeName, 600, 0);
							g.drawString("Strength: "+template.strength, 600, 60);
							g.drawString("Health: " + health, 600, 80);
						}
					}
				})) ,false));
			}
		},2);

		options[1][7] = new Tech(600, "Fuedalism", new Tech[]{options[0][7], options[0][9]}, player -> player.eff++, 2);
		options[1][8] = new Tech(700, "Universities", new Tech[]{options[0][8], options[0][9]}, player -> player.options.add(new ProductionOption(600, "Build University", city -> city.locSci += 2, true)),2);
		
		
		options[2][6] = new Tech(800, "Cannons", new Tech[]{options[1][6], options[0][9]}, player -> player.options.add(new ProductionOption(300, "Train Cannon", city -> city.owner.add(city.x, city.y, new UnitTemplate(1, 12, "Cannon", 50, new UnitDisp(){
			@Override
			public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
				g.setColor(owner.playerColour);
				g.drawLine(x * 40 + 10, y * 40 + 10, x * 40 + 30, y * 40 + 20);
				g.drawLine(x * 40 + 10, y * 40 + 30, x * 40 + 30, y * 40 + 20);
				g.drawLine(x * 40 + 10, y * 40 + 10, x * 40 + 30, y * 40 + 10);
				if(isSelected){
					g.fillRect(600, 20, 100, 20);
					g.setColor(Color.white);
					g.drawString("Move", 601, 20);
					g.drawString("Movement left: " +totMove, 600, 40);
					g.drawString(owner.name + "'s " + template.typeName, 600, 0);
					g.drawString("Strength: "+template.strength, 600, 60);
					g.drawString("Health: " + health, 600, 80);
				}
			}
		})), false)) , 2);
		options[2][8] = new Tech(800, "Enlightenment", new Tech[]{options[1][8]}, player -> player.globSci += 0.5, 2);
		options[2][7] = new Tech(750, "Banking", new Tech[]{options[1][6], options[1][7]}, player -> player.options.add(new ProductionOption(550, "Build Bank", city -> city.locGold += 2, true)), 2); 
		
		options[0][12] = new Tech(1000, "Industrialisation", player -> player.options.add(factory), 3);
		options[0][13] = new Tech(900, "Steam Power", player->player.globFood += 3 , 3);
		options[0][14] = new Tech(1000, "Scientific Method", player -> player.globSci += 3, 3);
		options[1][12] = new Tech(1500, "Electricity", new Tech[]{options[0][12], options[0][13]}, new Resolver(){
			@Override
			public void resolve(Player player){
				for(City city : player.cities){
					if(city.compBuilds.contains(factory))
						city.locProd += 4;
				}
			}
		}, 3);
		
		options[1][14] = new Tech(1200, "Medicine", new Tech[]{options[0][14]}, player -> player.globFood += 3, 3);
		options[1][13] = new Tech(1500, "Democracy", new Tech[]{options[0][14]}, player -> player.eff++, 3);
		options[2][12] = new Tech(2000, "Oil", new Tech[]{options[0][12], options[0][13], options[0][14]}, player -> player.eff += 3, 3);
		options[0][17] = new Tech(10000, "Utopia", new Resolver(){
			@Override
			public void resolve(Player player){
				int i = 0;
				while(allPlayers.size() != 1){
					if(allPlayers.get(i) != player){
						allPlayers.remove(i);
						i--;
					}
					i++;
				}
			}
		}, 4);
	}
	*/
	/*
	//Function that runs every tick, not used
	@Override
	public void update(GameContainer container, int dt) throws SlickException {
		
	}
	*/
	/*
	//input function for mouse
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount){
		if(!isChangingResearch){//don't want to click on things you can't see
			if(selected instanceof Unit && x >= 600 && y <= 40 &&//checks if the unit can move still and the player is correct
					((Unit)selected).totMove > 0 && y>=20 && (selected).owner == currPlayer && x <= 700)
				isMoving = !isMoving;
			else if(isMoving)
				((Unit)selected).move(x,y);//handling for movement clicks off of the grid is dealt with in the move function
			
			//the end turn button
			else if(x >= 600 && y >= 580 && x < 700){
				currPlayer.update();
				currIndex++;
				if(currIndex == allPlayers.size()){//resets back to the first player
					currIndex = 0;
					turnCount++;
				}
				currPlayer = allPlayers.get(currIndex);
				for(int i = 0; i < allPlayers.size(); i++){//checks if each player is eliminated
					boolean hasSettler = false;
					for(Unit unit : allPlayers.get(i).units){
						if(unit instanceof Settler)
							hasSettler = true;
					}
					if(!hasSettler && allPlayers.get(i).cities.size() == 0){
						if(currIndex >= allPlayers.indexOf(allPlayers.get(i)))
							currIndex--;//makes sure that nobody's turn is skipped as a result of elimination
						while(allPlayers.get(i).units.size() > 0)
							allPlayers.get(i).units.get(0).kill();//units are stored in tiles too, so this is needed
						allPlayers.remove(i);
						i--;//fixes the index if something was removed
					}
				}
			}
			else if(selected instanceof City && x >= 600 && y >= 80 && y <= 100 && (selected).owner == currPlayer){
				isChangingProd = !isChangingProd;
				if(isChangingProd == true){
					for(int i = 0; i < 3; i++)
						((City)selected).assignmentPercs[i] = -1;//resets all of the assignments so that the player can change them
				}
				else
					for(int i = 0; i<3; i++)
						((City)selected).assignmentPercs[i] = 33;//ensures that there is always some values for assignment percentages
			}
			
			//how to change production
			else if(selected instanceof City && x >= 600 && y > 140 && y < 160 && (selected).owner == currPlayer){
				((City)selected).displayingOptions = !((City)selected).displayingOptions;
			}
			
			//making a city with settler
			else if(selected instanceof Settler && x >= 700 && y < 40 && y > 20 && (selected).owner == currPlayer){
				selected.owner.addCity(((Settler)selected).position.x, ((Settler)selected).position.y);
				((Unit)selected).kill();
			}
			
			//handles changing the production
			else if(selected instanceof City && ((City)selected).displayingOptions){
				if(x > 600 && y > 160 && y < 160//checks that the mouse is on the list, with the length * number of pixels - number of completed things as the modifier
						+ (currPlayer.options.size()-((City)selected).numRemoved(currPlayer.options.size()-1))*20){
					int listIndex = (y-160)/20;
					City selectedCity = (City)selected;
					selectedCity.currItem = currPlayer.options.get(listIndex //index needs to take into account completed buildings not being visable
							+ selectedCity.numRemoved(listIndex) + selectedCity.numContigue(listIndex));
					selectedCity.displayingOptions = false;
				}
			}
			
			//selection function
			else if(x < 600){
				Tile tileClicked = map[x/40][y/40];
				if(tileClicked.occupiers.size() != 0){
					if(selected != null)
						selected.isSelected = false;
					if(clickCount -1 < tileClicked.occupiers.size()){
						selected = tileClicked.occupiers.get(clickCount- 1);//selects things depending on what was first + how many clicks
						selected.isSelected = true;
					}
				}
			}
		}
		if(x > 600 && y > 500 && y < 520)
			isChangingResearch = !isChangingResearch;
		
		else if(x < 600 && isChangingResearch){
			int xIndex = (x - 40)/220;//converts from pixel positions to indexes
			int yIndex = (y - 40)/30;
			if(options[xIndex][yIndex] != null && options[xIndex][yIndex].researchStatus() == 0){
				currPlayer.currentResearch = options[xIndex][yIndex];
			}
		}
		
		
	}
	*/

	@Override
	public void initStatesList(GameContainer arg0) throws SlickException {
		// TODO Auto-generated method stub
		this.addState(new InGameView(0));
		this.addState(new ResearchView(1));
		
	}
	

	
}