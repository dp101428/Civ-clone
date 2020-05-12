package code;

import java.util.ArrayList;

import org.newdawn.slick.Color;



public class AiPlayer extends Player {
	int currentCities;
	AICore ai;
	ArrayList<WeightedVal> futureCityList;
	ArrayList<Task> taskList;
	public AiPlayer(Color playerColour, String name, Game actGame) {
		super(playerColour, name, actGame);
		ai = new AICore(this);
		taskList = new ArrayList<Task>();
		futureCityList = new ArrayList<WeightedVal>();
	}
	
	public void updateAI(){
		int cities = 0;
		for(Player player : Game.allPlayers){
			cities += player.cities.size();
		}
		if(cities != currentCities || this.cities.size() == 0){
			currentCities = cities;
			futureCityList = ai.getCityLocations(); //only calculates valid city locations if there's a different number
		}
		SettlerTask:
		for(WeightedVal location: futureCityList){
			for(Task task: taskList){
				if(task.location == (Tile)location.obj)//don't make a task for a spot if there's already one for it
					continue SettlerTask;
			}
			taskList.add(new Task((Tile)location.obj, Settler.class, location.tScore, 1, TaskType.SETTLE));
		}
		//checks to see if city locations are still wanted
		elimLocations();
		//gives tasks to units
		assignTasksToUnits();
		//pick the best task for units
		for(Unit unit: units){
			unit.hasAssignment = false;
			unit.tasks.sort(new TaskComparator());
			if(unit.tasks.size() != 0){
				//System.out.println("could have had an assignment");
				for(int i = 0; i < unit.tasks.size() && !unit.hasAssignment; i++){
					boolean filled = false;
					if(unit.tasks.get(i).task.assignedUnits.size() == unit.tasks.get(i).task.unitsNeeded && unit.tasks.get(i).task.unitsNeeded != 0)
						filled = true;
					if(!filled){
						unit.tasks.get(i).task.assigned = true;
						unit.assignment = unit.tasks.get(i).task;
						unit.assignment.assignedUnits.add(unit);
						unit.hasAssignment = true;
					}
				}
				if(unit.assignment != null)
					unit.hasAssignment = true;
			}
		}
		
		//if there's no settlers and city locations that don't have tasks: make more settlers
		for(int i = 0; i < taskList.size(); i++){
			if(!taskList.get(i).assigned && this.cities.size() != 0){
				//needs to find the most appropriate city to make the new unit
				double shortestDist = Double.POSITIVE_INFINITY;
				City bestCity = null;
				for(City city : this.cities){
					double dist = taskList.get(i).location.getDistance(city.location);
					if(dist < shortestDist){
						shortestDist = dist;
						bestCity = city;
					}
				}
				//if the task not assigned was to make a city
				if(taskList.get(i).type == TaskType.SETTLE){
					taskList.get(i).assigned = true;
					ArrayList<Task> cityLoc = new ArrayList<Task>();
					cityLoc.add(taskList.get(i));
					System.out.println(taskList.get(i).importance);
					taskList.add(i, new Task(bestCity.location, City.class, taskList.get(i).importance, 1, TaskType.MAKESETTLER, cityLoc));
					i++;
				}
			}
		}
		assignTasksToCities();
		for(City city: this.cities){
			city.tasks.sort(new TaskComparator());
			for(PotentialAssignment task: city.tasks){
				System.out.println(task.task.importance);
				if(task.task.type == TaskType.BUILDING)
					System.out.println("^ was a building");
			}
			if(city.tasks.size() != 0){
				//System.out.println("could have had an assignment");
				for(int i = 0; i < city.tasks.size() && city.currItem == city.owner.noSelect && !city.tasks.get(i).task.assigned; i++){
					if(!city.tasks.get(i).task.assigned){
						city.tasks.get(i).task.assigned = true;
						city.tasks.get(i).task.assignedUnits.add(city);
						city.currentTask = city.tasks.get(i).task;
						if(city.tasks.get(i).task.type == TaskType.MAKESETTLER)
							city.currItem = city.owner.options.get(2);
						if(city.tasks.get(i).task.type == TaskType.BUILDING){
							city.currItem = city.tasks.get(i).task.option;
							System.out.println("had a building option: " + city.tasks.get(i).task.option.name);
						}
					}
				}
			}
		}
		moveUnits();
		
		//selecting tech
		ArrayList<Tech> researchableTechs = new ArrayList<Tech>();
		for(Tech[] row : ResearchView.options){
			for(Tech tech : row){
				if(tech != null && tech.researchStatus() == 0)
					researchableTechs.add(tech);
			}
		}
		researchableTechs.sort(new TechSorter(this));
		this.currentResearch = researchableTechs.get(researchableTechs.size()-1);
		System.out.println(this.currentResearch);
	}
	
	public void moveUnits(){
		Camera camera = Camera.getInstance();
		for(int i = 0; i < units.size(); i++){
			if(units.get(i).hasAssignment){
				//System.out.println("had an assignment");
				ArrayList<Tile> path = ai.aStar(units.get(i));
				int moves = path.size() -2;
				System.out.println(path);
				System.out.println(units.get(i).assignment.location);
				System.out.println(units.get(i).location);
				//System.out.println(path.get(moves));
				while(units.get(i).totMove > 0 && units.get(i).location != units.get(i).assignment.location){
					System.out.println("Tile targeted by move: " + (path.get(moves).x * 40 - camera.getX() + camera.getX())/40 + ", " + (path.get(moves).y * 40 - camera.getY() + camera.getY())/40);
					units.get(i).move(path.get(moves).x * 40 - camera.getX(), path.get(moves).y * 40 - camera.getY());
					moves--;
					System.out.println(units.get(i).location);
				}
				if(units.get(i).location == units.get(i).assignment.location){
					units.get(i).assignment.assignedUnits.remove(units.get(i));
					taskList.remove(units.get(i).assignment);
					if(units.get(i).assignment.type == TaskType.SETTLE){
						((Settler)units.get(i)).settle();
						i--;
						elimLocations();
					}
					else{
						units.get(i).assignment = null;
						units.get(i).hasAssignment = false;
					}
				}
			}
		}
	}
	
	public void assignTasksToUnits(){
		for(Task task : taskList){
			for(Unit unit : units){
				if(task.completerType.equals(unit.getClass())){
					unit.tasks.add(new PotentialAssignment(task, unit));
				}
			}
		}
	}
	
	public void assignTasksToCities(){
		for(Task task : taskList){
			for (City city : cities){
				if(task.completerType.equals(City.class)){
					city.tasks.add(new PotentialAssignment(task, city));
				}
			}
		}
	}
	//checks to see if all city locations are still valid
	public void elimLocations(){
		for(int i = 0; i < taskList.size(); i++){
			boolean isIn = false;
			for(WeightedVal location : futureCityList){
				if (taskList.get(i).location.equals((Tile)location.obj))
					isIn = true;
			}
			if(!isIn){
				for(Selectable unit : taskList.get(i).assignedUnits){
					if(unit instanceof Unit && ((Unit)unit).assignment == taskList.get(i)){
						((Unit)unit).assignment = null;
						((Unit)unit).hasAssignment = false;
					}
				}
				taskList.remove(i);
				i--;
			}
		}
	}

	public void addBuildingTasks(){
		for(City city : cities){
			for(ProductionOption building : city.owner.options){
				if(building.once && !city.compBuilds.contains(building)){
					System.out.println("could have had a building");
					city.tasks.add(new PotentialAssignment(new Task(city.location, City.class, 2 * building.importance/(building.cost/(city.pop * city.assignmentPercs[1] * city.yields[1]/132000 * city.locProd * city.owner.globProd * city.owner.eff)), 1, TaskType.BUILDING, building), city));
				}
			}
		}
	}

}
