package code;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Tech {
	int cost, era;
	Tech[] prereq;
	String name;
	Resolver completer;
	double weight;
	
	public Tech(int cost, String name, Resolver completer, int era, double weight){
		this(cost,name,new Tech[0], completer, era, weight);//if no prereqs given, none assumed
	}
	
	public Tech(int cost, String name, Tech[] prereq, Resolver completer, int era, double weight){
		this.cost = cost;
		this.name = name;
		this.prereq = prereq;
		this.completer = completer;
		this.era = era;
		this.weight = weight;
	}
	
	public void render(int x, int y, Graphics g){
		int status = researchStatus();
		if(status == 1)
			g.setColor(Color.green);
			
		
		if(status == 0)
			g.setColor(Color.yellow);
		
		if(status == -1)
			g.setColor(Color.gray);
		
		g.fillRect(x, y, 200, 20);
		if(status != 0)
			g.setColor(Color.white);
		else
			g.setColor(Color.black);
		g.drawString(name, x + 100 - name.length()* 9 / 2, y);
		g.setColor(Color.white);
		for(int i = 0; i < ResearchView.options.length; i++){
			for(int j = 0; j < ResearchView.options[i].length; j++){
				if(ResearchView.options[i][j] != null){
					for(Tech tech : prereq){
						if(tech == ResearchView.options[i][j])
							g.drawLine(i * 220 +240, j * 30 + 50, x, y + 10);
					}
				}
			}
		}
		if(Game.currPlayer.currentResearch == this){
			g.setColor(Color.gray);
			g.drawRect(x, y, 200, 20);
		}
	}
	
	
	public int researchStatus(){
		if(Game.currPlayer.researched.contains(this))
			return 1;
		for(Tech option : prereq){
			if(!Game.currPlayer.researched.contains(option))
				return -1;
		}
		for(Tech[] column : ResearchView.options){
			for(Tech tech : column){
				if(tech != null && tech.era < this.era && !Game.currPlayer.researched.contains(tech))
					return -1;
			}
		}
		return 0;
	}
	@Override
	public String toString(){
		return(name + ", " + weight);
	}
	
}
