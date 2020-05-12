//exists as a bundle of parameters to give to a unit, no other purpose

package code;

public class UnitTemplate {
	int movement, strength, maint;
	String typeName;
	UnitDisp renderModel;
	
	public UnitTemplate(int movement, int strength, String typeName, int maint, UnitDisp renderModel){
		this.movement = movement;
		this.strength = strength;
		this.typeName = typeName;
		this.maint = maint;
		this.renderModel = renderModel;
	}
}
