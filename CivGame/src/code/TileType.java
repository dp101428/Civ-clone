package code;

import org.newdawn.slick.Color;
public class TileType {
	int moveCost, prodYield, comYield, foodYield;
	Color colour;
	public TileType(int moveCost, int prodYield, int comYield, int foodYield, Color colour){
		this.moveCost = moveCost;
		this.prodYield = prodYield;
		this.comYield = comYield;
		this.foodYield = foodYield;
		this.colour = colour;
	}
}
