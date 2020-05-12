package code;

import java.util.ArrayList;
import java.util.Set;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class InGameView extends BasicGameState {
	Selectable selected;
	boolean isChangingProd, isMovingDown, isMovingUp, isMovingRight, isMovingLeft;
	Game actGame;
	final int id;
	public InGameView(int id) {
		this.id = id;
	}
	//spawns a new unit at the right distance
	public void spawnNew(Player player, int type){// 0 means make a warrior, 1 is a settler
		boolean spawned = false;
		while(!spawned){
			int x = (int)(Math.random() * Game.map.length);
			int y = (int)(Math.random() * Game.map[0].length);
			Set<Tile> area = City.getSurround(x, y, Game.map);
			boolean good = false;//settlers spawn on false, warriors on true
			for(Tile tile : area){
				//setters want to see nothing around them
				if((tile.occupiers.size() != 0 && tile.occupiers.get(0).owner != player && type == 1))
					good = true;
				
				//warriors want to see their settler
				if(tile.occupiers.size() != 0 && tile.occupiers.get(0).owner == player && tile.occupiers.get(0) instanceof Settler && type == 0)
					good = true;
				
			}
			if(good && type == 0){
				player.add(x, y, player.warrior);
				spawned = true;
			}
			else if(!good && type == 1){
				player.addSettler(x, y, null);
				spawnNew(player, 0);
				spawned = true;
			}
		}
	}
	@Override
	public void init(GameContainer arg0, StateBasedGame game) throws SlickException {
		actGame = (Game)game;
		Game.tiles = new TileType[3];
		Game.none = new Tech(10000000, "No tech selected", currPlayer -> currPlayer.totResearch = 0, 1, 0);//needed to allow for transitions between techs
		actGame.turnCount = 1;
		
		Game.allPlayers = new ArrayList<Player>();
		Game.allPlayers.add(new Player(Color.red, "Russia", actGame));
		Game.allPlayers.add(new AiPlayer(Color.blue, "America", actGame));
		
		for(int i = 0; i < Game.allPlayers.size(); i++){
			if(i == 0){
				Game.allPlayers.get(i).addSettler((int)(Math.random() * Game.map.length), (int)(Math.random() * Game.map[0].length), null);
				spawnNew(Game.allPlayers.get(i), 0);
			}
			else
				spawnNew(Game.allPlayers.get(i), 1);
		}
		
		Game.currPlayer = Game.allPlayers.get(0);
		actGame.currIndex = 0;
		
		
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame game, Graphics g) throws SlickException {
		Camera camera = Camera.getInstance();
		for(int i = 0; i < Game.map.length; i++){//renders the tiles
			for(int j = 0; j < Game.map.length; j++)
				Game.map[i][j].render(g);
		}
		for(Player player : Game.allPlayers){//renders the units
			for(Unit unit : player.units)//gives the drawing function for the units everything that it needs
				unit.template.renderModel.draw(g, unit.location.x * 40 - camera.getX(), unit.location.y * 40 - camera.getY(), unit.owner, unit.isSelected, unit.totMove, unit.template, unit.health);
			for(City city : player.cities)
				city.render(g);//draws the cities
		}
		g.setColor(Color.black);
		g.fillRect(600, 0, 200, 600);
		if(selected != null){
			if(selected instanceof Unit){
				Unit selectUnit = (Unit) selected;
				g.setColor(selectUnit.owner.playerColour);
				g.fillRect(600, 20, 100, 20);
				if(selectUnit.template == selectUnit.owner.settler)
					g.fillRect(700, 20, 100, 20);
				g.setColor(Color.white);
				g.drawString("Move", 601, 20);
				g.drawString("Movement left: " +selectUnit.totMove, 600, 40);
				g.drawString(selectUnit.owner.name + "'s " + selectUnit.template.typeName, 600, 0);
				g.drawString("Strength: "+selectUnit.template.strength, 600, 60);
				if(selectUnit.template == selectUnit.owner.settler){
					g.drawString("Found city", 700, 20);
					g.drawLine(700, 20, 700, 40);
				}
				g.drawString("Health: " + selectUnit.health, 600, 80);
				if(selectUnit.assignment != null){
					g.drawString("" + selectUnit.assignment.type, 600, 100);
					g.drawString(selectUnit.assignment.location.toString(), 600, 120);
				}
			}
			else if (selected instanceof City){
				City selectCity = (City) selected;
				g.setColor(selectCity.owner.playerColour);
				g.fillRect(600, 80, 100, 20);
				g.fillRect(600, 140, 160, 20);
				g.setColor(Color.gray);
				g.fillRect(600, 120, selectCity.currProd * 9*selectCity.currItem.name.length()/selectCity.currItem.cost, 20);
				g.setColor(Color.white);
				g.drawString(selectCity.owner.name + "'s city", 600, 0);
				g.drawString("Population: "+selectCity.pop, 600, 20);
				g.drawString("Population assignments", 600, 40);
				g.drawString(selectCity.assignmentPercs[0] + " " + selectCity.assignmentPercs[1] +" " + selectCity.assignmentPercs[2], 600, 60);
				g.drawString("Change jobs", 601, 80);
				g.drawString("Current task", 600, 100);
				g.drawString(selectCity.currItem.name, 600, 120);
				g.drawString("Change production", 600, 140);
				if(selectCity.displayingOptions){
					int pos = 0;
					for(int i = 0; i < selectCity.owner.options.size(); i++){
						if(!selectCity.compBuilds.contains(selectCity.owner.options.get(i))){
							g.setColor(selectCity.owner.playerColour);
							g.fillRect(600, 160 + 20 * pos, 9 * selectCity.owner.options.get(i).name.length(), 20);
							g.setColor(Color.white);
							g.drawString(selectCity.owner.options.get(i).name, 600, 160 + 20 * pos);
							pos++;
						}
					}
				}
				g.drawString("Current Research", 600, 160);
				g.drawString(selectCity.owner.currentResearch.name, 600, 180);
			}
		}
		
		int timeToResearch = 999;
		try{//research per turn will be zero when all they have is a settler, so a try/catch is needed
			timeToResearch = (Game.currPlayer.currentResearch.cost-Game.currPlayer.totResearch)/Game.currPlayer.sciPerTurn + 1;
		}
		catch(ArithmeticException a){
			timeToResearch = 999;
		}
		g.setColor(Color.yellow);//highlights what buttons they are using
		if(actGame.isMoving)
			g.drawRect(600, 20, 100, 20);
		if(isChangingProd)
			g.drawRect(600, 80, 100, 20);
		g.setColor(Color.cyan);
		g.fillRect(600, 580, 100, 20);
		g.fillRect(600, 500, 136, 20);
		g.setColor(Color.white);
		g.drawString("End turn", 600, 580);
		g.drawString("" + actGame.turnCount, 700, 580);
		g.drawString("Gold: " + Game.currPlayer.totGold, 600, 560);
		g.drawString("Change research", 600, 500);
		g.drawString("Current Research", 600, 520);
		if(Game.currPlayer.currentResearch != Game.none)
			g.drawString(Game.currPlayer.currentResearch.name + " (" + timeToResearch + " turns)", 600, 540);
		else
			g.drawString(Game.currPlayer.currentResearch.name, 600, 540);
		g.drawString(Game.currPlayer.name + "'s turn", 600, 480);
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int dT) throws SlickException {
		Camera camera = Camera.getInstance();
		if(isMovingLeft){
			camera.x -= dT/3;
			if(camera.x < 0)
				camera.x = 0;
		}
		else if(isMovingRight){
			camera.x += dT/3;
			if(camera.x > Game.map.length * 40 - 600)
				camera.x = Game.map.length * 40 - 600;
		}
		if(isMovingUp){
			camera.y -= 3;
			if(camera.y < 0)
				camera.y = 0;
		}
		else if(isMovingDown){
			camera.y += 3;
			if(camera.y > Game.map[0].length * 40 - 600)
				camera.y = Game.map[0].length * 40 - 600;
		}
			
	}

	@Override
	public int getID() {
		return id;
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount){
		Camera camera = Camera.getInstance();
		if(selected instanceof Unit && x >= 600 && y <= 40 &&//checks if the unit can move still and the player is correct
				y>=20 && (selected).owner == Game.currPlayer && x <= 700){
			if(((Unit)selected).totMove > 0 && !actGame.isMoving)
				actGame.isMoving = true;
			else
				actGame.isMoving = false;
		}
		else if(actGame.isMoving)
			((Unit)selected).move(x,y);//handling for movement clicks off of the grid is dealt with in the move function
		
		//the end turn button
		else if(x >= 600 && y >= 580 && x < 700){
			endTurn();
		}
		else if(selected instanceof City && x >= 600 && y >= 80 && y <= 100 && (selected).owner == Game.currPlayer){
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
		else if(selected instanceof City && x >= 600 && y > 140 && y < 160 && (selected).owner == Game.currPlayer){
			((City)selected).displayingOptions = !((City)selected).displayingOptions;
		}
		
		//making a city with settler
		else if(selected instanceof Settler && x >= 700 && y < 40 && y > 20 && (selected).owner == Game.currPlayer){
			((Settler)selected).settle();
			selected = Game.currPlayer.cities.get(Game.currPlayer.cities.size() -1);
		}
		
		//handles changing the production
		else if(selected instanceof City && ((City)selected).displayingOptions){
			if(x > 600 && y > 160 && y < 160//checks that the mouse is on the list, with the length * number of pixels - number of completed things as the modifier
					+ (Game.currPlayer.options.size()-((City)selected).numRemoved(Game.currPlayer.options.size()-1))*20){
				int listIndex = (y-160)/20;
				City selectedCity = (City)selected;
				selectedCity.currItem = Game.currPlayer.options.get(listIndex //index needs to take into account completed buildings not being visable
						+ selectedCity.numRemoved(listIndex) + selectedCity.numContigue(selectedCity.numRemoved(listIndex)));
				selectedCity.displayingOptions = false;
			}
		}
		//selection function
		else if(x < 600){
			Tile tileClicked = Game.map[(x + camera.getX())/40][(y +camera.getY())/40];
			if(tileClicked.occupiers.size() != 0){
				if(selected != null)
					selected.isSelected = false;
				if(clickCount -1 < tileClicked.occupiers.size()){
					selected = tileClicked.occupiers.get(clickCount- 1);//selects things depending on what was first + how many clicks
					selected.isSelected = true;
				}
			}
		}
		if(x > 600 && y > 500 && y < 520)
			actGame.enterState(1);
	}
	//input function for keys
	@Override
	public void keyPressed(int key, char c){
		//203 is left, 208 is down 205 is right 200 is up
		//System.out.println("" + key);
		if(key != 203 && key != 208 && key != 205 && key != 200){
			if(isChangingProd){//all this code/function does is handle changing the production percentages
				int i = 0;
				boolean isInputted = false;
				while(!isInputted){//goes through and finds the first index that hasn't been defined yet
					if(!((City)selected).changedAssign[i]){//checks if the index's value has been changed yet
						isInputted = true;
						if(((City)selected).assignmentPercs[i] == -1)//true if no digits inputted
							((City)selected).assignmentPercs[i] = (key - 1) % 10;
						
						else if(((City)selected).assignmentPercs[i] < 10){//true if one digit
							((City)selected).assignmentPercs[i] = ((City)selected).assignmentPercs[i] * 10 + (key-1)%10;
							((City)selected).changedAssign[i] = true;
						}
					}
					i++;
				}
				if(((City)selected).changedAssign[2]){//handles the last digit inputted
					isChangingProd = false;
					for(int j = 0; j < 3; j++)
						((City)selected).changedAssign[j] = false;
					
					//handles ratios rather than percentages
					if(((City)selected).assignmentPercs[0] + ((City)selected).assignmentPercs[1] + ((City)selected).assignmentPercs[2] != 100){
						int totPercs = ((City)selected).assignmentPercs[0] + ((City)selected).assignmentPercs[1] + ((City)selected).assignmentPercs[2];
						for(int k = 0; k < 3; k++)
							((City)selected).assignmentPercs[k] = 100 * ((City)selected).assignmentPercs[k]/totPercs;
					}
				}
			}
		}
		else{
			if(key == 203)
				isMovingLeft = true;
			else if (key == 208)
				isMovingDown = true;
			else if (key == 205)
				isMovingRight = true;
			else if (key == 200)
				isMovingUp = true;
		}
	}
	@Override
	public void keyReleased(int key, char c){
		if(key == 203 || key == 208 || key == 205 || key == 200){
			if(key == 203)
				isMovingLeft = false;
			else if (key == 208)
				isMovingDown = false;
			else if (key == 205)
				isMovingRight = false;
			else if (key == 200)
				isMovingUp = false;
		}
	}
	
	public void endTurn(){
		Camera camera = Camera.getInstance();
		Game.currPlayer.update();
		actGame.currIndex++;
		if(actGame.currIndex == Game.allPlayers.size()){//resets back to the first player
			actGame.currIndex = 0;
			actGame.turnCount++;
		}
		Game.currPlayer = Game.allPlayers.get(actGame.currIndex);
		for(int i = 0; i < Game.allPlayers.size(); i++){//checks if each player is eliminated
			boolean hasSettler = false;
			for(Unit unit : Game.allPlayers.get(i).units){
				if(unit instanceof Settler)
					hasSettler = true;
			}
			if(!hasSettler && Game.allPlayers.get(i).cities.size() == 0){
				if(actGame.currIndex >= Game.allPlayers.indexOf(Game.allPlayers.get(i)))
					actGame.currIndex--;//makes sure that nobody's turn is skipped as a result of elimination
				while(Game.allPlayers.get(i).units.size() > 0)
					Game.allPlayers.get(i).units.get(0).kill();//units are stored in tiles too, so this is needed
				Game.allPlayers.remove(i);
				i--;//fixes the index if something was removed
			}
		}
		//makes sure that the new player's units are in frame, and moves the camera if not
		boolean inFrame = false;
		for(Unit unit : Game.currPlayer.units){
			if(unit.location.x * 40 <= camera.x + 600 && unit.location.x * 40 >= camera.x && unit.location.y * 40 <= camera.y + 600 && unit.location.y * 40 >= camera.y)
				inFrame = true;
		}
		for(City city : Game.currPlayer.cities){
			if(city.location.x * 40 <= camera.x + 600 && city.location.x * 40 >= camera.x && city.location.y * 40 <= camera.y + 600 && city.location.y * 40 >= camera.y)
				inFrame = true;
		}
		if(!inFrame){
			//prioritise jumping to cities first
			if(Game.currPlayer.cities.size() != 0){
				camera.x = Game.currPlayer.cities.get(0).location.x * 40;
				camera.y = Game.currPlayer.cities.get(0).location.y * 40;
				selected = Game.currPlayer.cities.get(0);
			}
			else{
				camera.x = Game.currPlayer.units.get(0).location.x * 40;
				camera.y = Game.currPlayer.units.get(0).location.y * 40;
				selected = Game.currPlayer.units.get(0);
			}
			
			if(camera.x > Game.map.length * 40 - 600)
				camera.x = Game.map.length * 40 - 600;
			if(camera.y > Game.map[0].length * 40 - 600)
				camera.y = Game.map[0].length * 40 - 600;
		}
		if(Game.currPlayer instanceof AiPlayer){
			((AiPlayer)Game.currPlayer).updateAI();
			endTurn();
		}
		System.out.println("Finished Turn");
	}
}
