package code;

import org.newdawn.slick.Graphics;

public interface UnitDisp {
	public void draw(Graphics g, int x, int y, Player owner, boolean isSelected, int totMove, UnitTemplate template, int health);
}
