package code;

import java.util.ArrayList;

public class Unit extends Selectable{
	int totMove, health;
	UnitTemplate template;
	ArrayList<PotentialAssignment> tasks;
	Task assignment;
	boolean hasAssignment;
	
	public Unit(Player owner, UnitTemplate template){
		this(owner, template, (int)(Math.random() * 15), (int)(Math.random() * 15));
	}
	
	public Unit(Player owner, UnitTemplate template, int x, int y){
		super(owner, Game.map[x][y]);
		this.template = template;
		location.occupiers.add(this);
		totMove = template.movement;
		health = 100;
		tasks = new ArrayList<PotentialAssignment>();
		assignment = null;
	}
	
	
	public void move(int x, int y){
		Camera camera = Camera.getInstance();
		if(x > 600)
			return;
		Tile selectedTile = Game.map[(x + camera.getX())/40][(y + camera.getY())/40];
		if(selectedTile.isAdjacentTo(location) && this.totMove > 0){
			if(selectedTile.occupiers.size() != 0 && unitIndex(selectedTile.occupiers) >= 0 
					&& ((Unit)selectedTile.occupiers.get(unitIndex(selectedTile.occupiers))).owner != this.owner)
				this.combat((Unit)selectedTile.occupiers.get(unitIndex(selectedTile.occupiers)));

			
			this.totMove -= selectedTile.type.moveCost;
			if(this.totMove <= 0)
				owner.actGame.isMoving = false;
			if(selectedTile.occupiers.size() == 0 ||  unitIndex(selectedTile.occupiers) == -1 || 
					((Unit)selectedTile.occupiers.get(unitIndex(selectedTile.occupiers))).owner == this.owner){
				if(selectedTile.occupiers.size() == 1 &&  unitIndex(selectedTile.occupiers) == -1){
					selectedTile.occupiers.get(0).owner.cities.remove((City)selectedTile.occupiers.get(0));
					selectedTile.occupiers.get(0).owner = this.owner;
					this.owner.cities.add((City)selectedTile.occupiers.get(0));
				}
				location.occupiers.remove(this);
				selectedTile.occupiers.add(this);
				this.location = selectedTile;
			}
		}
	}
	
	public void combat(Unit other){
		double ratio = (double)this.template.strength * owner.strMulti * this.health/(other.template.strength*2 * other.owner.strMulti * other.health);
		if(Math.random() < ratio){
			other.kill();
			this.health -= 50 * other.health * other.owner.strMulti * other.template.strength/(this.template.strength * this.owner.strMulti * this.health);
			if(this.health <= 0)
				this.health = 1;
		}
		else{
			other.health -= 50 * this.health * this.template.strength * this.owner.strMulti/(other.template.strength * other.owner.strMulti * other.health);
			if(other.health <= 0)
				other.health = 1;
			this.kill();
		}
	}
	
	public void kill(){
		if(this.assignment != null)
			this.assignment.assigned = false;
		this.location.occupiers.remove(this);
		this.owner.units.remove(this);
	}
	
	public static int unitIndex(ArrayList<Selectable> occupiers){
		for(int i = 0; i < occupiers.size(); i++){
			if(occupiers.get(i) instanceof Unit)
				return i;
		}
		return -1;
	}
	
	public void update(){
		this.totMove = template.movement;
		owner.totGold -= this.template.maint;
		if(this.totMove == this.template.movement)
			health += 10;
		if(health > 100)
			health = 100;
	}
}