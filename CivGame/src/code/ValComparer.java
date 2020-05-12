package code;

import java.util.Comparator;

public class ValComparer implements Comparator<WeightedVal> {

	public ValComparer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(WeightedVal first, WeightedVal other) {
		return first.compareTo(other);
	}

}
