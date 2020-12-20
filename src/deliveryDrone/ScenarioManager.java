package deliveryDrone;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

enum ScenarioNumber{ 
	ONE(1,"One Random package from warehouse"),
	TWO(2,"each house send package to warehouse"),
	THREE(3,"priority without warehouse packages");
//	FOUR(4,"  "),
//	FIVE(5,"  "),
//	SIX(6,"  "),
//	SEVEN(7,"  ");

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
//		case 4:
//			return FOUR;
//		case 5:
//			return FIVE;
//		case 6:
//			return SIX;
//		case 7:
//			return SEVEN;
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
		boolean[] warehouse0 = new boolean[GridPanel.NUM_OF_HOUSES]; 
	    int randomNum = ThreadLocalRandom.current().nextInt(0, 4);

		warehouse0[randomNum]=true; // package in the warehouse to house #4
		ScenarioStep scenarioStep0 = new ScenarioStep(ScenarioNumber.ONE,stepNumber++, house, warehouse0) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, boolean[] houseMonitors, boolean[] warehouseMonitors,  int pickUpThisState, int dropOffThisState) {
				if(pickUpThisState == randomNum+5) {
					System.out.println("scenario #1 - step #0 is finished");
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		boolean[] warehouse1 = new boolean[GridPanel.NUM_OF_HOUSES]; 
		ScenarioStep scenarioStep1 = new ScenarioStep(ScenarioNumber.ONE,stepNumber++, house, warehouse1) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, boolean[] houseMonitors, boolean[] warehouseMonitors,  int pickUpThisState, int dropOffThisState) {
				if(dropOffThisState == randomNum+1) {
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
	 * Scenario #2 - 'each house send one package to warehouse'
	 * This scenario has one step. 
	 * The step is finished whenever the drone pickup the packages from all the houses.
	 */
	private static Queue<ScenarioStep> createScenarioTwo() {
		Queue<ScenarioStep> scenarioSteps = new LinkedList<>();
		int stepNumber = 0;
		boolean[] house = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] warehouse0 = new boolean[GridPanel.NUM_OF_HOUSES]; 
		boolean[] envelope = new boolean[GridPanel.NUM_OF_HOUSES];
		Random rand = new Random(); 
		for(int i=0; i<envelope.length;i++) {
			envelope[i]= rand.nextBoolean();
		}
		Arrays.fill(house, Boolean.TRUE);
		ScenarioStep scenarioStep0 = new ScenarioStep(ScenarioNumber.TWO,stepNumber++, house, warehouse0, false, false, envelope) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, boolean[] houseMonitors, boolean[] warehouseMonitors,  int pickUpThisState, int dropOffThisState) {
				switch (pickUpThisState) {
				case 1:
					this.getPrivateData()[0]=true;
					break;
				case 2:
					this.getPrivateData()[1]=true;
					break;
				case 3:
					this.getPrivateData()[2]=true;
					break;
				case 4:
					this.getPrivateData()[3]=true;
					break;
				}
				for(int i=0; i< this.getPrivateData().length ; i++) {
					if(!this.getPrivateData()[i]) {
						return false;
					}
				}
				System.out.println("scenario #2 - final step (step #0) is finished");
				return true;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		return scenarioSteps;
	}
	/*
	 * Scenario #3 - 'priority without warehouse packages'
	 * This scenario has one step. 
	 * The step is finished whenever the drone pickup the package from house#1 and the envelope from house #3.
	 */
	private static Queue<ScenarioStep> createScenarioThree() {
		Queue<ScenarioStep> scenarioSteps = new LinkedList<>();
		int stepNumber = 0;
		boolean[] house = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] envelope = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] warehouse0 = new boolean[GridPanel.NUM_OF_HOUSES]; 
		house[0]=true;
		house[2]= true;
		envelope[2]=true;
		ScenarioStep scenarioStep0 = new ScenarioStep(ScenarioNumber.THREE,stepNumber++, house, warehouse0,false, true, envelope) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, boolean[] houseMonitors, boolean[] warehouseMonitors,  int pickUpThisState, int dropOffThisState) {
				switch (pickUpThisState) {
				case 1:
					this.getPrivateData()[0]=true;
					break;
				case 3:
					this.getPrivateData()[2]=true;
					break;
				}
				if(this.getPrivateData()[2] && this.getPrivateData()[0]) {
					System.out.println("scenario #3 - final step (step #0) is finished");
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		return scenarioSteps;
	}
	/*
	 * returns a queue of steps needed to be taken in order to play the specified scenario
	 */
	public static Queue<ScenarioStep> getScenario(int scenarioID) {
		switch (scenarioID) {
		case 1:
			return createScenarioOne();
		case 2:
			return createScenarioTwo();
		case 3:
			return createScenarioThree();
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
