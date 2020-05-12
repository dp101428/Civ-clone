package code;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import java.util.ArrayList;

public class Tile implements Comparable<Tile>{
	TileType type;
	int x,y, genVal, height, rainfall, id;
	ArrayList<Selectable> occupiers;
	
	public Tile(TileType type, int x, int y){
		this.type = type;
		this.x = x;
		this.y = y;
		occupiers = new ArrayList<Selectable>();
	}
	
	public Tile(int x, int y, int id){
		this.x = x;
		this.y = y;
		occupiers = new ArrayList<Selectable>();
		this.id = id;
	}
	
	public void render(Graphics g){
		if(type != null){
			Camera camera = Camera.getInstance();
			g.setColor(type.colour);
			g.fillRect(x * 40 - camera.getX(), y * 40 - camera.getY(), 40, 40);
			g.setColor(Color.black);
			//g.drawString("" + rainfall, x * 40 - camera.getX(),  y * 40 - camera.getY());
			//g.drawString("" + height, x * 40 - camera.getX(),  y * 40 - camera.getY() + 20);
		}
	}
	
	
	
	public boolean isAdjacentTo(Tile other){
		return(Math.abs(x - other.x) <= 1) && (Math.abs(y - other.y) <= 1 && other != this);
	}
	
	public double getDistance(Tile other){
		return(Math.sqrt((this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y)));
	}
	
	public String toString(){
		return "(" + x + ", " + y + ")";
	}

	@Override
	public int compareTo(Tile arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
		
	@Override
	public boolean equals(Object other){
		return ((Tile)other).id == this.id;
	}
}
