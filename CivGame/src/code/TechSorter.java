package code;

import java.util.Comparator;

public class TechSorter implements Comparator<Tech> {
	Player player;
	public TechSorter(Player player) {
		this.player = player;
	}

	@Override
	public int compare(Tech o1, Tech o2) {
		double timeToResearch = 999.0;
		try{//research per turn will be zero when all they have is a settler, so a try/catch is needed
			timeToResearch = player.currentResearch.cost/player.sciPerTurn + 1;
		}
		catch(ArithmeticException a){
			timeToResearch = 999;
		}
		return (int) ((o1.weight/timeToResearch - o2.weight/timeToResearch) * 100000);
	}

}
