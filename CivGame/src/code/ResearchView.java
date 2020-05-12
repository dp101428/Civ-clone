package code;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class ResearchView extends BasicGameState {
	Game actGame;
	public static Tech options[][];
	final int id;
	public ResearchView(int id) {
		this.id = id;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame actGame) throws SlickException {
		// TODO Auto-generated method stub
		this.actGame = (Game)actGame;
		final Game finGame = (Game)actGame;
		//this doesn't need to be a variable, but I haven't moved it over to the constructor because doing so doesn't add anything
				UnitTemplate horseRider = new UnitTemplate(2,4,"Horseman",15, 
					new UnitDisp(){ 
					@Override 
					public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
					g.setColor(owner.playerColour);
					g.drawLine(x + 20, y, x + 10, y + 40);
					g.drawLine(x + 20, y, x + 30, y + 40);
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
				}, true, 150);
				options = new Tech[3][18];
				//defining all of the researchble techs, is awkward in a large block but having a separate method to define all of them is pointless
				options[0][0] = new Tech(100, "Horse riding", currPlayer -> currPlayer.addOption(new ProductionOption(150, "Train Horseman", city -> city.owner.add(city.location.x, city.location.y, horseRider),false, 40)), 1, 20);
				options[0][1] = new Tech(100, "Agriculture", currPlayer -> currPlayer.addOption(new ProductionOption(300, "Farm surroundings", city -> city.locPop++, true, 60)), 1, 30);
				options[1][0] = new Tech(250, "Metalworking", new Tech[]{options[0][0], options[0][1]}, currPlayer -> currPlayer.addOption(new ProductionOption(200, "Train Swordsman", city -> currPlayer.add(city.location.x, city.location.y, new UnitTemplate(1,6,"Swordsman",20, new UnitDisp(){ 
					@Override 
					public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
					g.setColor(owner.playerColour);
					g.drawLine(x + 10, y + 10, x + 20, y + 5);
					g.drawLine(x + 30, y + 10, x + 20, y + 5);
					g.drawLine(x + 10, y + 10, x + 20, y + 30);
					g.drawLine(x + 30, y + 10, x + 20, y + 30);
					}
					
				})), false, 60)), 1, 40);
				options[1][1] = new Tech(200, "Irrigation", new Tech[]{options[0][1]}, currPlayer -> currPlayer.globFood++, 1, 50); 
				options[0][2] = new Tech(150, "Scholarship", player -> player.globSci += .2, 1, 60);
				options[0][3] = new Tech(125, "Archery", player -> player.options.add(new ProductionOption(125, "Train Archer", city -> city.owner.add(city.location.x, city.location.y, new UnitTemplate(1, 3, "Archer", 15, new UnitDisp(){@Override
					public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
						g.setColor(owner.playerColour);
						g.drawLine(x + 30, y + 20, x + 20, y + 5);
						g.drawLine(x + 30, y + 20, x + 20, y + 35);
						g.drawLine(x + 10, y + 20, x + 35, y + 20);
					}
				})), false, 40)), 1, 20);
				options[1][2] = new Tech(200, "Libraries", new Tech[]{options[0][2]}, player -> player.addOption(new ProductionOption(400, "Build Library", city -> city.locSci++, true, 60)), 1, 50);
				options[1][3] = new Tech(250, "Mathamatics", new Tech[]{options[0][2]}, player -> player.globGold += 0.5, 1, 40);
				options[2][1] = new Tech(300, "Legalism", new Tech[]{options[0][2], options[1][1]}, player -> player.eff += 0.5, 1, 100);//laws improve everything
				options[2][0] = new Tech(300, "Armouring", new Tech[]{options[1][0]}, player -> player.strMulti += 0.5, 1, 50);
				options[2][2] = new Tech(300, "Engineering", new Tech[]{options[1][3]}, player -> player.addOption(new ProductionOption(500, "Build Aquaduct", city -> city.locPop += .5, true, 80)),1, 70);
				//Era 2 techs
				
				options[0][6] = new Tech(500, "Metallurgy", player ->player.addOption(new ProductionOption(250, "Train Knight", city->city.owner.add(city.location.x, city.location.y, new UnitTemplate(2, 7, "Knight", 40, new UnitDisp(){
					@Override 
					public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
					g.setColor(owner.playerColour);
					g.drawLine(x + 10, y + 10, x + 20, y + 5);
					g.drawLine(x + 30, y + 10, x + 20, y + 5);
					g.drawLine(x + 10, y + 10, x + 20, y + 30);
					g.drawLine(x + 30, y + 10, x + 20, y + 30);
					}
					})) ,false, 60)), 2, 50);
				options[0][7] = new Tech(500, "Hoe" , player -> player.globFood += 1.5 , 2, 60);
				options[0][8] = new Tech(500, "Printing", new Resolver(){
					@Override
					public void resolve(Player player){
						player.globGold += 1;
						player.globSci += 1;
					}
				}, 2, 100);
				options[0][9] = new Tech(500, "Construction", player -> player.globProd += 1, 2, 110);
				
				options[1][6] = new Tech(550, "Gunpowder", new Tech[]{options[0][6], options[0][7]}, new Resolver(){
					@Override
					public void resolve(Player player){
						player.strMulti++;
						player.options.add(new ProductionOption(150, "Train Musketman", city -> city.owner.add(city.location.x, city.location.y, new UnitTemplate(1, 9, "Musketman", 30, new UnitDisp(){
							@Override
							public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
								g.setColor(owner.playerColour);
								g.drawLine(x + 10, y + 10, x + 30, y + 20);
								g.drawLine(x + 10, y + 30, x + 30, y + 20);
							}
						})) ,false, 70));
					}
				},2, 80);

				options[1][7] = new Tech(600, "Fuedalism", new Tech[]{options[0][7], options[0][9]}, player -> player.eff++, 2, 120);
				options[1][8] = new Tech(700, "Universities", new Tech[]{options[0][8], options[0][9]}, player -> player.options.add(new ProductionOption(600, "Build University", city -> city.locSci += 2, true, 80)),2, 110);
				
				
				options[2][6] = new Tech(800, "Cannons", new Tech[]{options[1][6], options[0][9]}, player -> player.options.add(new ProductionOption(300, "Train Cannon", city -> city.owner.add(city.location.x, city.location.y, new UnitTemplate(1, 12, "Cannon", 50, new UnitDisp(){
					@Override
					public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
						g.setColor(owner.playerColour);
						g.drawLine(x + 10, y + 10, x + 30, y + 20);
						g.drawLine(x + 10, y + 30, x + 30, y + 20);
						g.drawLine(x + 10, y + 10, x + 30, y + 10);
					}
				})), false, 80)) , 2, 80);
				options[2][8] = new Tech(800, "Enlightenment", new Tech[]{options[1][8]}, player -> player.globSci += 0.5, 2, 120);
				options[2][7] = new Tech(750, "Banking", new Tech[]{options[1][6], options[1][7]}, player -> player.options.add(new ProductionOption(550, "Build Bank", city -> city.locGold += 2, true, 40)), 2, 80); 
				
				options[0][12] = new Tech(1000, "Industrialisation", player -> player.options.add(factory), 3, 130);
				options[0][13] = new Tech(900, "Steam Power", player->player.globFood += 3 , 3, 150);
				options[0][14] = new Tech(1000, "Scientific Method", player -> player.globSci += 3, 3, 180);
				options[1][12] = new Tech(1500, "Electricity", new Tech[]{options[0][12], options[0][13]}, new Resolver(){
					@Override
					public void resolve(Player player){
						for(City city : player.cities){
							if(city.compBuilds.contains(factory))
								city.locProd += 4;
						}
					}
				}, 3, 170);
				
				options[1][14] = new Tech(1200, "Medicine", new Tech[]{options[0][14]}, player -> player.globFood += 3, 3, 200);
				options[1][13] = new Tech(1500, "Democracy", new Tech[]{options[0][14]}, player -> player.eff++, 3, 220);
				options[2][12] = new Tech(2000, "Oil", new Tech[]{options[0][12], options[0][13], options[0][14]}, player -> player.eff += 3, 3, 250);
				options[0][17] = new Tech(10000, "Utopia", new Resolver(){
					@Override
					public void resolve(Player player){
						int i = 0;
						while(finGame.allPlayers.size() != 1){
							if(finGame.allPlayers.get(i) != player){
								finGame.allPlayers.remove(i);
								i--;
							}
							i++;
						}
					}
				}, 4, 300);
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics g) throws SlickException {
		// TODO Auto-generated method stub
		for(int i = 0; i < options.length; i++){
			for(int j = 0; j < options[i].length; j++)
				if(options[i][j] != null)//draws all techs that exist in the grid
					options[i][j].render(i * 220 + 40, j * 30 + 40, g);
		}
		g.drawString("First Era", 20, 10);
		g.drawString("Second Era", 20, 190);
		g.drawString("Third Era", 20, 370);
		g.drawString("Fourth Era", 20, 510);
		int timeToResearch = 999;
		try{//research per turn will be zero when all they have is a settler, so a try/catch is needed
			timeToResearch = (Game.currPlayer.currentResearch.cost-Game.currPlayer.totResearch)/Game.currPlayer.sciPerTurn + 1;
		}
		catch(ArithmeticException a){
			timeToResearch = 999;
		}
		
		
		//UI buttons and words, nothing interesting here
		g.setColor(Color.yellow);//highlights what buttons they are using
		/*if(isMoving)
			g.drawRect(600, 20, 100, 20);
		if(isChangingProd)
			g.drawRect(600, 80, 100, 20);*/
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
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount){
		if(x < 600){
			int xIndex = (x - 40)/220;//converts from pixel positions to indexes
			int yIndex = (y - 40)/30;
			if(options[xIndex][yIndex] != null && options[xIndex][yIndex].researchStatus() == 0){
				Game.currPlayer.currentResearch = options[xIndex][yIndex];
			}
		}
		if(x > 600 && y > 500 && y < 520)
			actGame.enterState(0);
	}

}
