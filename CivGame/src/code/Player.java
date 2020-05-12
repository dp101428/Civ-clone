//Holds all the units/techs of one player
package code;
import java.util.ArrayList;
import java.util.Set;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
public class Player{
	ArrayList<Unit> units;
	Color playerColour;
	String name;
	ArrayList<City> cities;
	ArrayList<ProductionOption> options;
	int totGold, totResearch, sciPerTurn, eff, strMulti;
	double globFood, globProd, globGold, globSci;
	ArrayList<Tech> researched;
	Tech currentResearch;
	UnitTemplate warrior, settler;
	ProductionOption noSelect;
	Game actGame;
	
	public Player(Color playerColour, String name, Game actGame){
		this.actGame = actGame;
		units = new ArrayList<Unit>();
		this.playerColour = playerColour;
		//crap starting unit definition, could be in game but no point
		warrior = new UnitTemplate(1, 2, "Warrior", 10, new UnitDisp(){ @Override 
			public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
			g.setColor(owner.playerColour);
			g.drawLine(x, y, x + 40, y+40);
			g.drawLine(x , y + 40, x + 40, y);
			}
			
		});
		
		//probably doesn't need to be here, could be moved to the settler class, but no need to
		settler = new UnitTemplate(2,1, "Settler", 5, new UnitDisp(){ @Override 
			public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health){
			g.setColor(owner.playerColour);
			g.drawLine(x, y , x + 40, y+ 40);
			g.drawLine(x, y + 40, x + 40, y );
			g.drawLine(x, y + 20, x+39, y + 20);
			if(isSelected){
				g.fillRect(600, 20, 100, 20);
				g.fillRect(700, 20, 100, 20);
				g.setColor(Color.white);
				g.drawString("Move", 601, 20);
				g.drawString("Movement left: " +totMove, 600, 40);
				g.drawString(owner.name + "'s " + template.typeName, 600, 0);
				g.drawString("Strength : "+template.strength, 600, 60);
				g.drawString("Found city", 700, 20);
				g.drawLine(700, 20, 700, 40);
				g.drawString("Health: " + health, 600, 80);
				}
			}
			
		});
		this.name = name;
		cities = new ArrayList<City>();
		options = new ArrayList<ProductionOption>();
		this.addOption(new ProductionOption(100, "Train Soldier", city -> city.owner.add(city.location.x, city.location.y, warrior), false, 30));
		//need a tech to lock mines behind, but no ideas currently
		this.addOption(new ProductionOption(300, "Mine area", new ProdResolver(){@Override
			public void complete(City city){
			Set<Tile> surroundings = City.getSurround(city.location.x,city.location.y, actGame.map);
			for (Tile tile : surroundings){
				if(tile.type == Game.tiles[2] || tile.height >= 60)
					city.yields[2] += 2;
			}
		}
		} , true, 50));
		this.addOption(new ProductionOption(200, "Gather Settlers", city -> city.owner.addSettler(city.location.x, city.location.y, city), false, 40));
		totGold = 100;
		researched = new ArrayList<Tech>();
		totResearch = 0;
		currentResearch = Game.none;
		noSelect = new ProductionOption(10000, "Nothing Selected", city -> city.currProd = 0, false, 0);
		globFood = 1;
		globProd = 1;
		globGold = 1;
		globSci = 1;
		eff = 1;
		strMulti = 1;
	}
	
	//simplifies adding units
	public void add(UnitTemplate template){
		units.add(new Unit(this, template));
	}
	
	//simplifies adding units
	public void add(int x, int y, UnitTemplate template){
		units.add(new Unit(this, template, x, y));
	}
	
	//simplifies adding cities
	public void addCity(int x, int y){
		cities.add(new City(this,x,y));
	}
	
	//simplifies adding settlers
	public void addSettler(City loc){
		units.add(new Settler(this,this.settler));
		if(loc != null)
			loc.pop-= 1000;
	}
	
	//simplifies adding settlers
	public void addSettler(int x, int y, City loc){
		units.add(new Settler(this,this.settler, x, y));
		if(loc != null)
			loc.pop-= 1000;
	}
	
	//runs at end of turn, resolves everything
	public void update(){
		sciPerTurn = 0;
		for(City city : this.cities)
			city.updateCity();//runs the city version of this code
		for(Unit unit : this.units)
			unit.update();
		for(int i = 0; i < this.units.size(); i++){//have to run the maintenance code outside of the other unit update, because iterating over arraylist that changes length
			if(this.totGold < 0){
				this.totGold = 0;
				units.get(i).kill();
				i--;
			}
		}
		
		//handles technology completing
		if(totResearch >= currentResearch.cost){
			totResearch -= currentResearch.cost;
			researched.add(currentResearch);
			currentResearch.completer.resolve(actGame.currPlayer);
			currentResearch = Game.none;
		}
		
	}
	
	//adds production options, no longer needed but does shorten code in other places
	public void addOption(ProductionOption option){
		this.options.add(option);
		
	}
}