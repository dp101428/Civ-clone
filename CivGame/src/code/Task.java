package code;

import java.util.ArrayList;

enum TaskType{SETTLE, MAKEUNIT, MOVE, MAKESETTLER, BUILDING}

public class Task{
	Tile location;
	Class<?> completerType;
	double importance;
	boolean assigned;
	int unitsNeeded;
	TaskType type;
	ArrayList<Selectable> assignedUnits;
	ArrayList<Task> dependentTasks;
	ProductionOption option;
	
	public Task(Tile location, Class<?> completerType, double importance, int unitsNeeded, TaskType type) {
		this.location = location;
		this.completerType = completerType;
		this.importance = importance;
		this.unitsNeeded = unitsNeeded;
		this.type = type;
		assignedUnits = new ArrayList<Selectable>();
	}
	
	public Task(Tile location, Class<?> completerType, double importance, TaskType type){
		this(location, completerType, importance, 0, type);
	}
	
	public Task(Tile location, Class<?> completerType, double importance, int unitsNeeded, TaskType type, ArrayList<Task> dependentTasks){
		this(location, completerType, importance, unitsNeeded, type);
		this.dependentTasks = dependentTasks;
	}
	
	public Task(Tile location, Class<?> completerType, double importance, int unitsNeeded, TaskType type, ProductionOption option){
		this(location, completerType, importance, unitsNeeded, type);
		this.option = option;
	}
	
	@Override
	public String toString(){
		return location.toString();
	}

}
