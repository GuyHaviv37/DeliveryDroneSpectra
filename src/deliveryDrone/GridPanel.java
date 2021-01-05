package deliveryDrone;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GridPanel extends JPanel implements ActionListener {

	// Locations & Final Defines - These can be modified in accordance with the specification
	int[][] houseLocations = new int[][] { { 0, 0 }, { 0, 2 }, { 2, 0 }, { 2, 2 } };
	int[] warehouseLocation = new int[] { 3, 3 };
	int[] chargingStationLocation = new int[] { 3, 2 };
	public static final int NUM_OF_HOUSES = 4;
	private static final int MAX_ENERGY = 7;
	private static final int MAX_CAPACITY = 2;
	private static final int MAX_PRIORITY_CAP = 1;
	private static final int MAX_ENVELOPES = 5;

	private MainFrame parentFrame;
	private boolean priorityFeature;
	private boolean windsFeature;
	private boolean energyFeature;
	private DroneController controller = new DroneController();
	// ENV in-house variables
	private boolean[] houseRequests = new boolean[NUM_OF_HOUSES];
	private boolean[] warehouseRequests = new boolean[NUM_OF_HOUSES];
	private boolean[] envelopeRequests = new boolean[NUM_OF_HOUSES];
	private boolean priorityMode = false;
	private boolean windsMode = false;

	// SYS in-house variables
	private Drone drone;

	private PickUp pickUpThisState = PickUp.NO_PICKUP;
	private DropOff dropOffThisState = DropOff.NO_DROPOFF;
	private int totalPackages = 0;
	private int[] droneToHouseCap = new int[NUM_OF_HOUSES];
	private int droneToWarehouseCap;
	private int totalEnvelopes = 0;
	private int energy = 0;
	private int priorityCap = 0;

	// AUX
	private Timer timer;
	private int stateNum = 0;
	
	// SCENARIO , DEMO VARIABLES
	private DemoMode isDemo;
	private Queue<ScenarioStep> currentScenario;

	// SIMULATION VARIABLES
	private boolean[] housePackageDisplay = new boolean[NUM_OF_HOUSES];
	private boolean[] warehousePackageDisplay = new boolean[NUM_OF_HOUSES];
	boolean isLoading = false;
	boolean isSkip = false;
	boolean afterScenarioEffect = false;
	boolean turnOnTurbo = false;
	ImageManager im;
	JLabel leavesGIF1;
	JLabel leavesGIF2;
	boolean setWindsOn = false;
	int setWindsOnState = 0;

	int gridSize = 600;
	int squareSize = 150;
	int chargingStationSize = 110;

	int packageSize_Big = 50;
	int packageSize_Small = 20;

	int lightningSize = 25;
	int lightControlSize = 15;
	int arrowSize = 15;

	public GridPanel(MainFrame parentFrame,boolean priorityFeature, boolean windsFeature,boolean energyFeature) {
		this.parentFrame = parentFrame;
		this.priorityFeature = priorityFeature;
		this.windsFeature = windsFeature;
		this.energyFeature = energyFeature;
		this.drone = new Drone(chargingStationLocation);

		setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
		setPreferredSize(new Dimension(594, 700));
		im = new ImageManager();
		loadWindsGIFs();
		this.timer = new Timer(100, this);
		repaint();
		this.timer.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		paintBackground(g);
		paintControlPanel(g);
		paintHouses(g);
		paintChargingStation(g);
		paintWarehouse(g);
		paintWarehouseBoard(g);
		if(isLoading) paintLoadingHeader(g);
		if(isSkip) paintFFHeader(g);

		// paint drone by it's new x,y coordinates
		g.drawImage(drone.getImage(), drone.getX(), drone.getY(), drone.getSize(), drone.getSize(), null);
	}

	private void paintFFHeader(Graphics g) {
		g.drawImage(im.getImage("ffIconImg"), 250, 250, 100, 100, null);
	}

	private void paintLoadingHeader(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(225, 290, 125, 40);
		g.setColor(Color.white);
		g.setFont(new Font("Calibri", Font.BOLD, 20));
		g.drawString("Loading...",235, 317);
	}

	private void paintBackground(Graphics g) {
		g.drawImage(im.getImage("backgroundImg"), 0, 0, gridSize, gridSize, null);
		if(setWindsOn && (setWindsOnState+1 < stateNum) && !(drone.isMoving()) && windsFeature) {
			this.leavesGIF1.setVisible(true);
			this.leavesGIF2.setVisible(true);
			setWindsOn = false;
		}
	}

	private void paintControlPanel(Graphics g) {
		int row, col;
		int paddingWide = 10;
		Color cpColor = new Color(59, 74, 99);
		int stringRow1 = 25, stringRow2 = 45, stringRow3 = 65, stringRow4 = 85;
		// fill "CONTROL PANEL"
		g.setFont(new Font("Calibri", Font.PLAIN,16));
		// Border
		g.setColor(Color.BLACK);
		g.drawLine(0, 600, 594, 600); 
		// Background
		g.setColor(cpColor);
		g.fillRect(0, 600, 594, 100);
		g.setColor(Color.WHITE);
		g.fillRect(10, 610, 594-squareSize, 80);

		// paint environment toggles
		g.setColor(Color.BLACK);
		col = 0;
		row = 4;
		if(priorityFeature) {
			boolean controllerPriorityMode = Boolean.parseBoolean(controller.getEnvVar("priorityMode"));
			g.drawString("Priority Mode:", col * squareSize + paddingWide, row * squareSize + stringRow1);
			g.drawImage(controllerPriorityMode ? im.getImage("greenLightImg") : im.getImage("redLightImg"), (col + 1) * squareSize - 40,
					row * squareSize + stringRow1 - 12, lightControlSize, lightControlSize, null);
			g.drawString("Priority Cap: " + priorityCap + "/" + MAX_PRIORITY_CAP, col * squareSize + paddingWide,
					row * squareSize + stringRow2);			
		}
		g.drawString("Stocking:", col * squareSize + paddingWide, row * squareSize + stringRow4);
		if (drone.isStocking()) {
			if (pickUpThisState != PickUp.NO_PICKUP) {
				g.drawImage(im.getImage("greenArrowImg"), (col + 1) * squareSize - 70, row * squareSize + stringRow4 - 12, arrowSize,
						arrowSize, null);
			}
			if (dropOffThisState != DropOff.NO_DROPOFF) {
				g.drawImage(im.getImage("redArrowImg"), (col + 1) * squareSize - 50, row * squareSize + stringRow4 - 12, arrowSize,
						arrowSize, null);
			}
		}

		int houseNum;
		// paint drone details
		col = 1;
		g.drawString("Package Inventory:",
				col * squareSize + paddingWide,
				row * squareSize + stringRow1);
		g.setColor(totalPackages == MAX_CAPACITY ? Color.red : Color.black);
		g.drawString("Total Packages: " + totalPackages + "/" + MAX_CAPACITY,
				col * squareSize + paddingWide,
				row * squareSize + stringRow2);
		g.setColor(totalEnvelopes == MAX_ENVELOPES ? Color.red : Color.black);
		g.drawString("Total Envelopes: "+totalEnvelopes+"/"+MAX_ENVELOPES,
				(col+1) * squareSize + paddingWide,
				row * squareSize + stringRow2);
		g.setColor(Color.black);
		g.drawString("Packages By Destination:",
				col * squareSize + paddingWide, 
				row * squareSize + stringRow3);
		g.drawString("WH: " + droneToWarehouseCap+",",
				col * squareSize + paddingWide,
				row * squareSize + stringRow4);
		int gapWide = 45;
		for(houseNum = 0; houseNum < 4; houseNum++) {
			g.drawString("H"+(houseNum+1)+": "+droneToHouseCap[houseNum]+ 
					(houseNum < 3 ? "," : ""),
					col * squareSize + paddingWide+10 + gapWide,
					row * squareSize + stringRow4);
			gapWide += 45;
		}

		// paint charging bar
		col = 3;
		g.setFont(new Font("Calibri",Font.BOLD,20));
		g.setColor(Color.white);
		if(energyFeature) {
			g.drawString("Battery: ", col * squareSize + 40, row * squareSize + 35);
			g.setColor(Color.black);
			g.drawImage(im.getImage("lightningImg"), col * squareSize + 15, row * squareSize + 20, lightningSize, lightningSize, null);
			g.drawRect(col * squareSize + paddingWide, row * squareSize + 60, squareSize - 35, 20);
			float batteryFilled = 1 - (this.energy / (float) MAX_ENERGY);
			Color currentBatteryColor = batteryFilled > 0.6 ? Color.green : batteryFilled < 0.2 ? Color.red : Color.orange;
			g.setColor(currentBatteryColor);
			g.fillRect(col * squareSize + paddingWide, row * squareSize + 60,
					Math.round((batteryFilled * (squareSize - 35))) + 5, 20);			
		}

	}

	private void paintHouses(Graphics g) {
		int row, col;
		g.setFont(new Font("Calibri", Font.BOLD, 16));
		
		
		// "paint houses"
		int houseNum = 0;
		for (int[] location : houseLocations) {
			row = location[0];
			col = location[1];
			g.drawImage(im.getImage("houseImg"), col * squareSize, row * squareSize, squareSize, squareSize, null);
			g.setColor(Color.black);
			g.drawString("" + (houseNum + 1), (col + 1) * squareSize - 15, row * squareSize + 45);
			if(housePackageDisplay[houseNum]) {
				BufferedImage waitingImg = envelopeRequests[houseNum] ? im.getImage("envelopeImg") : im.getImage("packageImg_TWH");
				g.drawImage(waitingImg, col * squareSize + 10, (row + 1) * squareSize - 60, packageSize_Big,
						packageSize_Big, null);
			}
			if (drone.isStocking()) {
				if (dropOffThisState.getIndex() == (houseNum + 1)) {
					g.drawImage(im.getImage("packageImg_TH"), (col + 1) * squareSize - (packageSize_Big + 10),
							(row + 1) * squareSize - 60, packageSize_Big, packageSize_Big, null);
				}
			}
			houseNum++;
		}
	}

	private void paintChargingStation(Graphics g) {
		int row, col;
		// "paint charging station"
		row = chargingStationLocation[0];
		col = chargingStationLocation[1];
		g.drawImage(im.getImage("chargingStationImg"), col * squareSize + (squareSize - chargingStationSize),
				row * squareSize + (squareSize - chargingStationSize), chargingStationSize, chargingStationSize, null);
	}

	private void paintWarehouse(Graphics g) {
		int row, col;
		// "paint warehouse"
		row = warehouseLocation[0];
		col = warehouseLocation[1];
		g.drawImage(im.getImage("warehouseImg"), col * squareSize, row * squareSize, squareSize, squareSize, null);
		if (drone.isStocking()) {
			if (dropOffThisState == DropOff.DROPOFF_AT_WH_PACKAGE) {
				g.drawImage(im.getImage("packageImg_TWH"), (col + 1) * squareSize - (packageSize_Big + 10),
						(row + 1) * squareSize - 60, packageSize_Big, packageSize_Big, null);
			} else if (dropOffThisState == DropOff.DROPOFF_AT_WH_ENVELOPE) {
				g.drawImage(im.getImage("envelopeImg"), (col + 1) * squareSize - (packageSize_Big + 10),
						(row + 1) * squareSize - 60, packageSize_Big, packageSize_Big, null);
			}
		}
	}

	private void paintWarehouseBoard(Graphics g) {
		int houseNum, row, col, paddingHigh = 20;
		// paint warehouse details
		row = warehouseLocation[0];
		col = warehouseLocation[1];
		g.setColor(Color.white);
		for (houseNum = 0; houseNum < 4; houseNum++) {
			g.drawString("H" + (houseNum + 1) + ":", col * squareSize + 10,
					row * squareSize + 20 * (houseNum + 2) + paddingHigh);
			if(warehouseRequests[houseNum]) {
				g.drawImage(im.getImage("packageImg_TH"), col * squareSize + 37,
				row * squareSize + 20 * (houseNum + 2) + (paddingHigh - 15), packageSize_Small,
				packageSize_Small, null);	
			}
		}
	}

	/* HANDLE USER EVENTS */

	public void addPickupRequest(int requestNumber) {
		if (requestNumber >= 1 && requestNumber <= 4) {
			if(pickUpThisState.getIndex() != requestNumber) {
				this.houseRequests[requestNumber - 1] = true;
				this.envelopeRequests[requestNumber - 1] = false;				
			}
		} else if (requestNumber >= 5 && requestNumber <= 8) {
			this.warehouseRequests[requestNumber - 5] = true;
		} else if (requestNumber >= 9 && requestNumber <= 12) {
			if(pickUpThisState.getIndex() != requestNumber - 8) {
				this.houseRequests[requestNumber - 9] = true;
				this.envelopeRequests[requestNumber - 9] = true;	
			}
		}
		updateEnvironment();
		repaint();
	}

	public void togglePriority(boolean newPriority) {
		this.priorityMode = newPriority;
		updateEnvironment();
		repaint();
	}

	public void toggleWinds(boolean newWinds) {
		this.windsMode = newWinds;
		if(newWinds) {
			setWindsOn = true;
			setWindsOnState = stateNum;
		} else {			
			leavesGIF1.setVisible(false);
			leavesGIF2.setVisible(false);
			setWindsOn = false;
		}
		updateEnvironment();
		repaint();
	}
	
	public void toggleDemo(boolean isDemo) {
		if(isDemo) {
			this.isDemo = DemoMode.WAITING;
			this.parentFrame.enableDemoBtn(false);
			this.parentFrame.enableRunScenarioBtn(false, "Run Scenario");
		} else {
			this.isDemo = DemoMode.IDLE;
			this.parentFrame.updateButtonsEnabled(true);
			this.parentFrame.updateModeButtons(priorityMode,windsMode);
			this.parentFrame.enableRunScenarioBtn(true, "Run Scenario");
		}
	}
	
	public void createScenario(int scenarioNumber) {
		if(scenarioNumber > 0) {
			this.currentScenario = ScenarioManager.getScenario(scenarioNumber);
		} else if (scenarioNumber < 0 && !afterScenarioEffect){
			this.turnOnTurbo = true;
			this.isSkip = true;
		}
	}

	// this is what the timer will generate at each delay
	// General approach:
	// * update all environment variables
	// * Update Drone
	// * paint component
	// * get new state from the controller, if needed
	@Override
	public void actionPerformed(ActionEvent e) {
		updateDemo(); 
		updateScenario();
		updateEnvironment();
		updateDrone();
		repaint();
		// When we have animation we will check that no animation is running before
		// getting new state
		if(drone.isStocking() && drone.isInLastStockFrame()) {
			clearPickedRequests();
		}
		if (!(drone.isMoving() || drone.isStocking())) {
			getNewState();
		}
	}
	
	private void updateScenario() {
		if(this.currentScenario == null) {
			if(afterScenarioEffect) {
				if(drone.isStocking()) {
					resetModes();
					this.parentFrame.updateButtonsEnabled(true); // buttons work again
					this.parentFrame.enableRunScenarioBtn(true, "Run Scenario");
					afterScenarioEffect = false;
				}
			}
			return;
		}
		ScenarioStep currentStep = this.currentScenario.peek();
		if (!currentStep.HasStarted()) { // update all the env variable by the current step
			if(currentStep.getStepNumber() == 0) {
				if(!(drone.isMoving() || drone.isStocking())) {
					this.drone.setTurboMode(true);	
					
				}
				resetModes();
				
				this.isLoading = true;
				this.parentFrame.updateButtonsEnabled(false);
				this.parentFrame.enableRunScenarioBtn(false,"Fast Forward Step");
				if(isGridClear()) {
					this.drone.setTurboMode(false);
					this.parentFrame.enableRunScenarioBtn(true,"Fast Forward Step");
					this.isLoading = false;
				} else {
					return ;
				}
			}
			boolean[] stepRequests = currentStep.getHouseRequestValue();
			for(int i=0;i<stepRequests.length;i++) {
				if(stepRequests[i]) {
					addPickupRequest(i+1);					
				}
			}
			this.warehouseRequests = currentStep.getWarehouseRequestValue();
			this.envelopeRequests = currentStep.getEnvelopeModeValue();
			this.windsMode= currentStep.getIsWinds();
			this.priorityMode = currentStep.getIsPriority();
			currentStep.setHasStarted(true);
		} 
		else if (currentStep.isFinished(this.droneToHouseCap, this.droneToWarehouseCap,this.totalEnvelopes,
				this.houseRequests, this.pickUpThisState, this.dropOffThisState)) { // step finished, get the next step
			this.drone.setTurboMode(false);
			if(this.isSkip) {
				this.isSkip = false;
			}
			this.currentScenario.poll();
			if (this.currentScenario.isEmpty()) { // no more steps
				this.currentScenario = null;
				afterScenarioEffect = true;
			}
		} else { //during a step
			// if we toggled turnOnTurbo (on Skip Step) then wait for right time then turn on
			if(this.turnOnTurbo && !(drone.isMoving() || drone.isStocking())) {
				this.drone.setTurboMode(true);
				this.turnOnTurbo = false;
			}

		}
	}

	private void updateDemo() {
		if(this.isDemo == DemoMode.RUNNING) {
			Random rand = new Random();
			double precentNewRequest = rand.nextDouble();
			if(precentNewRequest < 0.025) {
				int randomInt = ThreadLocalRandom.current().nextInt(1, 13);
				addPickupRequest(randomInt);
			}
			double precentWind = rand.nextDouble();
			double precentPriority = rand.nextDouble();
			boolean prevWinds = this.windsMode;
			boolean newWinds = (precentWind < 0.002) ? !prevWinds : prevWinds;
			if(prevWinds != newWinds) {
				toggleWinds(newWinds);
			}
			this.priorityMode= (precentPriority < 0.002) ? !this.priorityMode : this.priorityMode;
		} else if (this.isDemo == DemoMode.WAITING) {
			this.isDemo = DemoMode.RUNNING;
			this.parentFrame.enableDemoBtn(true);
		}
	}
	

	private void updateDrone() {
		if (drone.isMoving()) {
			if (!drone.shouldMakeStop()) {
				drone.move();
			} else {
				// Drone should halt
				if (pickUpThisState != PickUp.NO_PICKUP || dropOffThisState != DropOff.NO_DROPOFF || drone.isCharging()) {
					// MOVING SHOULD BE FALSE , STOCKING SHOULD BE TRUE
					drone.toggleMoving(false, true);
				} else {
					// MOVING SHOULD BE FALSE , STOCKING SHOULD BE FALSE (I.E. IDLE)
					drone.toggleMoving(false, false);
				}
			}
		} else if (drone.isStocking()) {
			// hold here for some time, than change state to IDLE
			if (drone.stock()) { // returns true when done stocking
				drone.toggleMoving(false, false);
			}
		} else {
			// drone is IDLE (standing and done stocking), need to calculate new animation
			int newRow = Integer.parseInt(controller.getSysVar("drone[0]"));
			int newCol = Integer.parseInt(controller.getSysVar("drone[1]"));
			drone.setNewDestination(newRow, newCol);
			// THERES NO STOCKING WHILE MOVING
			drone.toggleMoving(true, false);
		}
	}

	private void updateRequests() {
		for (int i = 0; i < houseRequests.length; i++) {
			controller.setEnvVar("outHousePackages[" + i + "]", Boolean.toString(houseRequests[i]));
		}
		for (int j = 0; j < warehouseRequests.length; j++) {
			controller.setEnvVar("outWarehousePackages[" + j + "]", Boolean.toString(warehouseRequests[j]));
		}
		for (int k = 0; k < envelopeRequests.length; k++) {
			controller.setEnvVar("envelopeRequests["+k+"]", Boolean.toString(envelopeRequests[k]));
		}
	}

	private void updatePriority() {
		controller.setEnvVar("priorityMode", Boolean.toString(this.priorityMode));
	}

	private void updateWinds() {
		controller.setEnvVar("windsMode", Boolean.toString(this.windsMode));
	}

	private void updateEnvironment() {
		updateRequests();
		updatePriority();
		updateWinds();
	}

	private void getNewState() {
		stateNum++;
		int i;
		controller.updateState();
		// make updates to all GUI components field variables that needs changing.
		// Drone is updated on the updateDrone method

		// PUTS , DOTS
		this.pickUpThisState = PickUp.parsePickUpEnum((controller.getSysVar("pickUpThisState")));
		this.dropOffThisState = DropOff.parseDropOffEnum((controller.getSysVar("dropOffThisState")));
		// Inventory, Envelopes counters
		this.totalPackages = Integer.parseInt(controller.getSysVar("totalPackages"));
		for (i = 0; i < NUM_OF_HOUSES; i++) {
			this.droneToHouseCap[i] = Integer.parseInt(controller.getSysVar("droneToHouseCap" + (i + 1)));
		}
		this.droneToWarehouseCap = Integer.parseInt(controller.getSysVar("droneToWarehouseCap"));
		this.totalEnvelopes = Integer.parseInt(controller.getSysVar("totalEnvelopesToWH"));
		// Energy
		this.energy = Integer.parseInt(controller.getSysVar("energy"));
		// Priority
		this.priorityCap = Integer.parseInt(controller.getSysVar("priorityCap"));
		// House Displays
		for (i = 0; i < NUM_OF_HOUSES; i++) {
			this.housePackageDisplay[i] = Boolean.parseBoolean(controller.getEnvVar("outHousePackages["+i+"]"));
			this.warehousePackageDisplay[i] = Boolean.parseBoolean(controller.getEnvVar("outWarehousePackages["+i+"]"));
		}
	}
	
	private void loadWindsGIFs() {
		ImageIcon icon = new ImageIcon("img/leaves_colors.gif");
		leavesGIF1 = new JLabel();
		leavesGIF2 = new JLabel();
		leavesGIF1.setSize(600, 300);
		leavesGIF2.setSize(600, 300);
		leavesGIF1.setIcon(icon);
		leavesGIF2.setIcon(icon);
		this.add(leavesGIF1);
		this.add(leavesGIF2);
		leavesGIF1.setVisible(false);
		leavesGIF2.setVisible(false);
	}
	
	private void clearPickedRequests() {
		int pickupIndex = pickUpThisState.getIndex();
		switch(pickUpThisState) {
		case NO_PICKUP:
			break;
		case PICKUP_FROM_HOUSE1:
		case PICKUP_FROM_HOUSE2:
		case PICKUP_FROM_HOUSE3:
		case PICKUP_FROM_HOUSE4:
			this.houseRequests[pickupIndex - 1] = false;
			this.envelopeRequests[pickupIndex - 1] = false;
			this.housePackageDisplay[pickupIndex - 1] = false;
			break;
		case PICKUP_TO_HOUSE1:
		case PICKUP_TO_HOUSE2:
		case PICKUP_TO_HOUSE3:
		case PICKUP_TO_HOUSE4:
			this.warehouseRequests[pickupIndex - 5] = false;
		default:
			break;
		}
		updateEnvironment();
		repaint();
	}
	
	
	private boolean isGridClear() {
		if(totalPackages > 0 || totalEnvelopes > 0) return false;
		for(int i=0;i<houseRequests.length;i++) {
			if(houseRequests[i] || warehouseRequests[i]) return false;
		}
		return true;
	}
	
	private void resetModes() {
		this.priorityMode = false;
		toggleWinds(false);
		this.parentFrame.updateModeButtons(priorityMode, windsMode);
	}

}

enum DemoMode{
	IDLE,WAITING,RUNNING;
}

