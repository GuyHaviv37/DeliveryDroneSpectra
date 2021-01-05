package deliveryDrone;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;

enum ScenarioNumber{ 
	ONE(1,"A package is sent from the warehouse to a random house"),
	TWO(2,"All houses request a package or an envelope delivery"),
	THREE(3,"Priority mode, with no warehouse packages"),
	FOUR(4,"Pickup: 3 WH packages, 2 house envelopes - with Priority Mode"),
	FIVE(5,"Pickup: all houses request package delivery, with a 4 package refill after a pickup. Partial use of Winds Mode"),
	SIX(6,"Full Grid, Full Delivery - All houses request delivery, Warehouse full as well. with Winds Mode"),
	SEVEN(7,"Carousel of house packages (Requests in cycle) - Full Delivery"),
	EIGHT(8,"On-the-way Pickup: Random WH request to a house, upon pickup - add delivery request from that house. Full Delivery of house request");

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
		case 8:
			return EIGHT;
	}
		return null; 
	}
}
public class ScenarioManager {

	/*
	 * Scenario #1 - 'A package is sent from the warehouse to a random house'
	 * This scenario has two steps. 
	 * The first step is finished whenever the drone pickup the package from warehouse.
	 * The second step is finished whenever the drone drop off the package at house the house.
	 */
	private static Queue<ScenarioStep> createScenarioOne() {
		Queue<ScenarioStep> scenarioSteps = new LinkedList<>();
		int stepNumber = 0;
		boolean[] house = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] warehouse0 = new boolean[GridPanel.NUM_OF_HOUSES]; 
		int randomNum = ThreadLocalRandom.current().nextInt(0, 4);

		warehouse0[randomNum]=true; 
		ScenarioStep scenarioStep0 = new ScenarioStep(ScenarioNumber.ONE,stepNumber++, house, warehouse0) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests,  PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState.getIndex() == randomNum+5) {
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		boolean[] warehouse1 = new boolean[GridPanel.NUM_OF_HOUSES]; 
		ScenarioStep scenarioStep1 = new ScenarioStep(ScenarioNumber.ONE,stepNumber++, house, warehouse1) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(dropOffThisState.getIndex() == randomNum+1) {
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep1); 
		return scenarioSteps;
	}

	/*
	 * Scenario #2 - 'All houses request a package or an envelope delivery'
	 * This scenario has one step. 
	 * The step is finished whenever the drone pickup the requests from all the houses.
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
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				switch (pickUpThisState) {
				case PICKUP_FROM_HOUSE1:
					this.getPrivateData()[0]=true;
					break;
				case PICKUP_FROM_HOUSE2:
					this.getPrivateData()[1]=true;
					break;
				case PICKUP_FROM_HOUSE3:
					this.getPrivateData()[2]=true;
					break;
				case PICKUP_FROM_HOUSE4:
					this.getPrivateData()[3]=true;
					break;
				default:
					break;
				}
				for(int i=0; i< GridPanel.NUM_OF_HOUSES ; i++) {
					if(!this.getPrivateData()[i]) {
						return false;
					}
				}
				return true;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		return scenarioSteps;
	}
	/*
	 * Scenario #3 - 'Priority mode, with no warehouse packages'
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
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				switch (pickUpThisState) {
				case PICKUP_FROM_HOUSE1:
					this.getPrivateData()[0]=true;
					break;
				case PICKUP_FROM_HOUSE3:
					this.getPrivateData()[2]=true;
					break;
				default:
					break;
				}
				if(this.getPrivateData()[2] && this.getPrivateData()[0]) {
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		return scenarioSteps;
	}
	/*
	 * Scenario #4 - 'Pickup: 3 WH packages, 2 house envelopes - with Priority Mode'
	 * This scenario has one step. 
	 * The step is finished whenever the drone pickup all the requests
	 * */
	private static Queue<ScenarioStep> createScenarioFour() {
		Queue<ScenarioStep> scenarioSteps = new LinkedList<>();
		int stepNumber = 0;
		boolean[] house = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] envelope = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] warehouse0 = new boolean[GridPanel.NUM_OF_HOUSES]; 
		Arrays.fill(envelope, Boolean.TRUE);
		house[2]=true;
		house[0]=true;
		warehouse0[1]=true;
		warehouse0[2]=true;
		warehouse0[3]=true;

		ScenarioStep scenarioStep0 = new ScenarioStep(ScenarioNumber.FOUR,stepNumber++, house, warehouse0,false, true, envelope) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				switch (pickUpThisState) {
				case PICKUP_FROM_HOUSE1:
					this.getPrivateData()[0]=true;
					break;
				case PICKUP_FROM_HOUSE3:
					this.getPrivateData()[2]=true;
					break;
				case PICKUP_TO_HOUSE2:
					this.getPrivateData()[5]=true;
					break;
				case PICKUP_TO_HOUSE3:
					this.getPrivateData()[6]=true;
					break;
				case PICKUP_TO_HOUSE4:
					this.getPrivateData()[7]=true;
					break;
				default:
					break;
				}

				if(this.getPrivateData()[0] && this.getPrivateData()[2] && this.getPrivateData()[5]&& this.getPrivateData()[6]&&this.getPrivateData()[7]) {
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		return scenarioSteps;
	}
	/*
	 * Scenario #5 - 'Pickup: all houses request package delivery, with a 4 package refill after a pickup. Partial use of Winds Mode'
	 * This scenario has 5 steps. 
	 * step#0 is finished whenever the drone pickup the first package from house.
	 * step#1 is finished whenever the drone pickup the second package from house.
	 * step#2 is finished whenever the drone pickup the third package from house.
	 * step#3 is finished whenever the drone pickup the fourth package from house.
	 * step#4 is finished whenever the drone delivered all the packages to the warehouse.
	 */
	private static Queue<ScenarioStep> createScenarioFive() {
		Queue<ScenarioStep> scenarioSteps = new LinkedList<>();
		int stepNumber = 0;
		boolean[] house = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] warehouse0 = new boolean[GridPanel.NUM_OF_HOUSES]; 
		boolean[] envelope = new boolean[GridPanel.NUM_OF_HOUSES];
		Arrays.fill(house, Boolean.TRUE);
		ScenarioStep scenarioStep0 = new ScenarioStep(ScenarioNumber.FIVE,stepNumber++, house, warehouse0) {
			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState != PickUp.NO_PICKUP) {
					this.getPrivateData()[0]=true;
					return false;
				}
				else {
					if(this.getPrivateData()[0]) {
						return true;
					}
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		boolean[] house1 = new boolean[GridPanel.NUM_OF_HOUSES];
		Arrays.fill(house1, Boolean.TRUE);
		ScenarioStep scenarioStep1 = new ScenarioStep(ScenarioNumber.FIVE,stepNumber++, house1, warehouse0) {
			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState != PickUp.NO_PICKUP) {
					this.getPrivateData()[0]=true;
					return false;
				}
				else {
					if(this.getPrivateData()[0]) {
						return true;
					}
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep1); 
		boolean[] house2 = new boolean[GridPanel.NUM_OF_HOUSES];
		Arrays.fill(house2, Boolean.TRUE);
		ScenarioStep scenarioStep2 = new ScenarioStep(ScenarioNumber.FIVE,stepNumber++, house2, warehouse0,true,false,envelope) {
			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState != PickUp.NO_PICKUP) {
					this.getPrivateData()[0]=true;
					return false;
				}
				else {
					if(this.getPrivateData()[0]) {
						return true;
					}
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep2); 
		boolean[] house3 = new boolean[GridPanel.NUM_OF_HOUSES];
		Arrays.fill(house3, Boolean.TRUE);
		ScenarioStep scenarioStep3 = new ScenarioStep(ScenarioNumber.FIVE,stepNumber++, house3, warehouse0,true,false,envelope) {
			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState != PickUp.NO_PICKUP) {
					this.getPrivateData()[0]=true;
					return false;
				}
				else {
					if(this.getPrivateData()[0]) {
						return true;
					}
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep3); 
		boolean[] house4 = new boolean[GridPanel.NUM_OF_HOUSES];
		Arrays.fill(house4, Boolean.TRUE);
		ScenarioStep scenarioStep4 = new ScenarioStep(ScenarioNumber.FIVE,stepNumber++, house4, warehouse0,true,false,envelope) {
			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState != PickUp.NO_PICKUP) {
					this.getPrivateData()[0]=true;
					return false;
				}
				else {
					if(this.getPrivateData()[0]) {
						return true;
					}
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep4); 
		ScenarioStep scenarioStep5 = new ScenarioStep(ScenarioNumber.FIVE,stepNumber++, house, warehouse0) {
			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				for(int i=0; i< GridPanel.NUM_OF_HOUSES;i++) {
					if(houseRequests[i]) {
						return false;
					}

				}
				if((droneToWarehouseCap + totalEnvelopes)==1 && dropOffThisState != DropOff.NO_DROPOFF) {
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep5); 
		return scenarioSteps;
	}
	/*
	 * Scenario #6 - 'Full Grid, Full Delivery - All houses request delivery, Warehouse full as well. with Winds Mode'
	 * This scenario has one step. 
	 * The step is finished whenever the drone drop off all the requests
	 * */
	private static Queue<ScenarioStep> createScenarioSix() {
		Queue<ScenarioStep> scenarioSteps = new LinkedList<>();
		int stepNumber = 0;
		boolean[] house = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] envelope = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] warehouse = new boolean[GridPanel.NUM_OF_HOUSES]; 
		Random rand = new Random();
		for(int i=0; i< GridPanel.NUM_OF_HOUSES;i++) {
			envelope[i]=rand.nextBoolean();
		}
		Arrays.fill(house, Boolean.TRUE);
		Arrays.fill(warehouse, Boolean.TRUE);


		ScenarioStep scenarioStep0 = new ScenarioStep(ScenarioNumber.SIX,stepNumber++, house, warehouse,true, false, envelope) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {

				switch (pickUpThisState) {
				case PICKUP_FROM_HOUSE1:
					this.getPrivateData()[0]=true;
					break;
				case PICKUP_FROM_HOUSE2:
					this.getPrivateData()[1]=true;
					break;
				case PICKUP_FROM_HOUSE3:
					this.getPrivateData()[2]=true;
					break;
				case PICKUP_FROM_HOUSE4:
					this.getPrivateData()[3]=true;
					break;
				case PICKUP_TO_HOUSE1:
					this.getPrivateData()[4]=true;
					break;
				case PICKUP_TO_HOUSE2:
					this.getPrivateData()[5]=true;
					break;
				case PICKUP_TO_HOUSE3:
					this.getPrivateData()[6]=true;
					break;
				case PICKUP_TO_HOUSE4:
					this.getPrivateData()[7]=true;
					break;
				default:
					break;
				}
				for(int i=0; i<GridPanel.NUM_OF_HOUSES*2;i++) {
					if(!this.getPrivateData()[i]) {
						return false;
					}
				}
				int totalCap= droneToWarehouseCap + totalEnvelopes;
				for(int i=0; i<GridPanel.NUM_OF_HOUSES;i++) {
					totalCap+= droneToHouseCap[i];
				}
				if(totalCap==1 && dropOffThisState != DropOff.NO_DROPOFF) {
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		return scenarioSteps;
	}
	/*
	 * Scenario #7 - 'Carousel of house packages (Requests in cycle) - Full Delivery'
	 * This scenario has 5 steps. 
	 * step#0 is finished whenever the drone pickup the package from house #1.
	 * step#1 is finished whenever the drone pickup the package from house #2.
	 * step#2 is finished whenever the drone pickup the package from house #4.
	 * step#3 is finished whenever the drone pickup the package from house #3.
	 * step#4 is finished whenever the drone delivered all the packages to the warehouse.
	 */
	private static Queue<ScenarioStep> createScenarioSeven() {
		Queue<ScenarioStep> scenarioSteps = new LinkedList<>();
		int stepNumber = 0;
		boolean[] house = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] warehouse0 = new boolean[GridPanel.NUM_OF_HOUSES]; 
		house[0]=true;
		boolean[] envelope = new boolean[GridPanel.NUM_OF_HOUSES];
		envelope[0]=true;
		ScenarioStep scenarioStep0 = new ScenarioStep(ScenarioNumber.SEVEN,stepNumber++, house, warehouse0,false,false, envelope) {
			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState != PickUp.NO_PICKUP) {
					this.getPrivateData()[0]=true;
					return false;
				}
				else {
					if(this.getPrivateData()[0]) {
						return true;
					}
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		boolean[] house1 = new boolean[GridPanel.NUM_OF_HOUSES];
		house1[1]=true;
		boolean[] envelope1 = new boolean[GridPanel.NUM_OF_HOUSES];
		envelope1[1]=true;
		ScenarioStep scenarioStep1 = new ScenarioStep(ScenarioNumber.FIVE,stepNumber++, house1, warehouse0, false,false, envelope1) {
			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState != PickUp.NO_PICKUP) {
					this.getPrivateData()[0]=true;
					return false;
				}
				else {
					if(this.getPrivateData()[0]) {
						return true;
					}
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep1); 
		boolean[] house2 = new boolean[GridPanel.NUM_OF_HOUSES];
		house2[3]=true;
		boolean[] envelope2= new boolean[GridPanel.NUM_OF_HOUSES];
		envelope2[3]=true;
		ScenarioStep scenarioStep2 = new ScenarioStep(ScenarioNumber.FIVE,stepNumber++, house2, warehouse0, false,false, envelope2) {
			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState != PickUp.NO_PICKUP) {
					this.getPrivateData()[0]=true;
					return false;
				}
				else {
					if(this.getPrivateData()[0]) {
						return true;
					}
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep2); 
		boolean[] house3 = new boolean[GridPanel.NUM_OF_HOUSES];
		house3[2]=true;
		boolean[] envelope3= new boolean[GridPanel.NUM_OF_HOUSES];
		envelope3[2]=true;
		ScenarioStep scenarioStep3 = new ScenarioStep(ScenarioNumber.FIVE,stepNumber++, house3, warehouse0, false,false,envelope3) {
			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState != PickUp.NO_PICKUP) {
					this.getPrivateData()[0]=true;
					return false;
				}
				else {
					if(this.getPrivateData()[0]) {
						return true;
					}
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep3); 
		ScenarioStep scenarioStep4 = new ScenarioStep(ScenarioNumber.FIVE,stepNumber++, house, warehouse0) {
			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				for(int i=0; i< GridPanel.NUM_OF_HOUSES;i++) {
					if(houseRequests[i]) {
						return false;
					}
				}
				if((droneToWarehouseCap + totalEnvelopes)==1 && dropOffThisState != DropOff.NO_DROPOFF) {
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep4); 
		return scenarioSteps;
	}
	/*
	 * Scenario #8 - 'On-the-way Pickup: Random WH request to a house, upon pickup - add delivery request from that house. Full Delivery of house request'
	 * This scenario has two steps. 
	 * The first step is finished whenever the drone pickup the package from warehouse.
	 * The second step is finished whenever the drone drop off the package at house .
	 */
	private static Queue<ScenarioStep> createScenarioEight() {
		Queue<ScenarioStep> scenarioSteps = new LinkedList<>();
		int stepNumber = 0;
		boolean[] house = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] warehouse0 = new boolean[GridPanel.NUM_OF_HOUSES]; 
		int randomNum = ThreadLocalRandom.current().nextInt(0, 4);

		warehouse0[randomNum]=true; 
		ScenarioStep scenarioStep0 = new ScenarioStep(ScenarioNumber.EIGHT,stepNumber++, house, warehouse0) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState.getIndex() == randomNum+5) {
					return true;
				}
				return false;
			}
		};
		scenarioSteps.offer(scenarioStep0); 
		boolean[] warehouse1 = new boolean[GridPanel.NUM_OF_HOUSES];
		boolean[] house1 = new boolean[GridPanel.NUM_OF_HOUSES];
		house1[randomNum]=true; 
		ScenarioStep scenarioStep1 = new ScenarioStep(ScenarioNumber.EIGHT,stepNumber++, house1, warehouse1) {

			@Override
			public boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, int totalEnvelopes, boolean[] houseRequests, PickUp pickUpThisState, DropOff dropOffThisState) {
				if(pickUpThisState.getIndex() == randomNum+1) {
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
		case 2:
			return createScenarioTwo();
		case 3:
			return createScenarioThree();
		case 4:
			return createScenarioFour();
		case 5:
			return createScenarioFive();
		case 6:
			return createScenarioSix();
		case 7:
			return createScenarioSeven();
		case 8:
			return createScenarioEight();
	}
		return null;
	}
}
