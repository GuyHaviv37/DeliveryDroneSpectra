package deliveryDrone;

import java.util.LinkedList;
import java.util.Queue;

enum ScenarioNumber{ 
	ONE(1,"One package from warehouse to house #4"),
	TWO(2,"  "),
	THREE(3,"  "),
	FOUR(4,"  "),
	FIVE(5,"  "),
	SIX(6,"  "),
	SEVEN(7,"  ");

	private String description;
	private int number;

	ScenarioNumber(int number,String description){
		this.number = number;
		this.description = description;
	}

	public String toString() {
		return this.description;
	}

	public int getNumber() {
		return this.number;
	}

	public static ScenarioNumber getScenarioNumber(int scenario) {
		switch(scenario) {
		case 1:
			return ONE;
		case 2:
			return TWO;
		case 3:
			return THREE;
		case 4:
			return FOUR;
		case 5:
			return FIVE;
		case 6:
			return SIX;
		case 7:
			return SEVEN;
		}
		return null; 
	}
}
public class ScenarioManager {
	
	/*
	 * Scenario #1 - 'One package from warehouse to house #4'
	 * This scenario has two steps. 
	 * The first step is finished whenever the drone pickup the package from warehouse.
	 * The second step is finished whenever the drone drop off the package at house #4.
	 */
	private static Queue<ScenarioStep> createScenarioOne() {
		Queue<ScenarioStep> scenarioSteps = new LinkedList<>();
		int stepNumber = 0;
		boolean[] house = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] warehouse = new boolean[GridPanel.NUM_OF_HOUSES];
		warehouse[3]=true; // package in the warehouse to house #4
		ScenarioStep scenarioStep0 = new ScenarioStep(ScenarioNumber.ONE,stepNumber++, house, warehouse) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, boolean[] houseMonitors, boolean[] warehouseMonitors) {
				if(warehouseMonitors[3] == false) {
					System.out.println("scenario #1 - step #0 is finished");
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		warehouse[3]=false; 
		ScenarioStep scenarioStep1 = new ScenarioStep(ScenarioNumber.ONE,stepNumber++, house, warehouse) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, boolean[] houseMonitors, boolean[] warehouseMonitors) {
				if(droneToHouseCap[3] == 0) {
					System.out.println("scenario #1 - final step (step #1) is finished");
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep1); 
		return scenarioSteps;
	}
	
	/*
	 * returns a queue of steps needed to be taken in order to play the specified scenario
	 */
	public static Queue<ScenarioStep> getScenario(int scenarioID) {
		switch (scenarioID) {
		case 1:
			return createScenarioOne();
//		case 2:
//			return createScenarioTwo();
//		case 3:
//			return createScenarioThree();
//		case 4:
//			return createScenarioFour();
//		case 5:
//			return createScenarioFive();
//		case 6:
//			return createScenarioSix();
//		default:
//			return createScenarioSeven();
		}
		return null;
	}
}
