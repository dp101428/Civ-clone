package code;

public class PotentialAssignment implements Comparable<PotentialAssignment> {
	Task task;
	Selectable unit;
	public PotentialAssignment(Task task, Selectable unit) {
		this.task = task;
		this.unit = unit;
	}

	@Override
	public int compareTo(PotentialAssignment other) {
		return (int) ((other.task.importance * 4/other.task.location.getDistance(unit.location) - task.importance * 4/task.location.getDistance(unit.location)) * 1000);
	}
	
	@Override
	public boolean equals(Object other){
		return (((PotentialAssignment)other).task.importance * 4/((PotentialAssignment)other).task.location.getDistance(unit.location) == task.importance * 4/task.location.getDistance(unit.location));
	}

}
