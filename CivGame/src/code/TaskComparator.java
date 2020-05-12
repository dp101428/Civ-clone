package code;

import java.util.Comparator;

public class TaskComparator implements Comparator<PotentialAssignment> {

	public TaskComparator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(PotentialAssignment first, PotentialAssignment other) {
		// TODO Auto-generated method stub
		return first.compareTo(other);
	}

}
