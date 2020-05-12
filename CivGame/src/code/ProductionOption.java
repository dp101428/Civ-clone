package code;

public class ProductionOption {
	boolean once;
	int cost;
	String name;
	ProdResolver complete;
	double importance;
	public ProductionOption( int cost, String name, ProdResolver complete, boolean once, double importance){
		this.cost = cost;
		this.name = name;
		this.complete = complete;
		this.once = once;
		this.importance = importance;
	}
	
	public String toString(){
		return name;
	}
	
}
