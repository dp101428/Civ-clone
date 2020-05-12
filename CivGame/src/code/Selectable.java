package code;

public class Selectable {
	boolean isSelected;
	Player owner;
	Tile location;
	public Selectable(Player owner, Tile location){
		this.owner = owner;
		this.location = location;
	}
}
