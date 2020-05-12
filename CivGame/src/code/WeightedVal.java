package code;

public class WeightedVal implements Comparable<WeightedVal>{
	double tScore, weight;
	Object obj;
	public WeightedVal(double tScore, double weight, Object obj) {
		this.tScore = tScore;
		this.weight = weight;
		this.obj = obj;
	}
	
	@Override
	public String toString(){
		return tScore + ", " + weight +", " +  obj;
	}

	@Override
	public int compareTo(WeightedVal other) {
		if(tScore != 0.0 && other.tScore != 0.0)
			return (int) ((other.tScore - tScore)  * 10000);
		else
			return (int) ((other.weight - this.weight) * 10000);
	}

}
