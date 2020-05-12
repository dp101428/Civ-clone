//Originally this class has code in it, but all the functionality of a settler is now in game or unit, but this still needs to exist
//Because the code determining the functionality relies of the object being of type settler
package code;

public class Settler extends Unit {

	public Settler(Player owner, UnitTemplate template) {
		super(owner, template);
	}

	public Settler(Player owner, UnitTemplate template, int x, int y) {
		super(owner, template, x, y);
	}
	
	@Override
	public void kill(){
		if(this.owner instanceof AiPlayer){
			((AiPlayer)owner).taskList.remove(this.assignment);
		}
		super.kill();
	}
	
	public void settle(){
		owner.addCity(location.x, location.y);
		kill();
	}

}
