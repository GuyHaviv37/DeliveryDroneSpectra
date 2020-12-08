import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GridPanel extends JPanel implements ActionListener{
	
	private static final int NUM_OF_HOUSES = 4;
	private static final int MAX_ENERGY = 7;
	private static final int MAX_CAPACITY = 4;
	private static final int MAX_PRIORITY_CAP = 1;
	
	private MainFrame parentFrame;
	private DroneController controller = new DroneController();
	// ENV in-house variables
	private boolean[] houseRequests = new boolean[NUM_OF_HOUSES];
	private boolean[] warehouseRequests = new boolean[NUM_OF_HOUSES];
	private boolean priorityMode = false;
	private boolean windsMode = false;
	
	// SYS in-house variables
	private Drone drone;
	
	private int pickUpThisState = 0;
	private int dropOffThisState = 0;
	private int totalPackages = 0;
	private int[] droneToHouseCap= new int[NUM_OF_HOUSES];
	private int droneToWarehouseCap;
	private boolean[] houseMonitors = new boolean[NUM_OF_HOUSES];
	private boolean[] warehouseMonitors = new boolean[NUM_OF_HOUSES];
	private int energy = 0;
	private int priorityCap = 0;
	
	// AUX
	private Timer timer;
	private int stateNum = 0;
	// SCENARIO QUEUE
	
	// BASIC SIMULATION VARIABLES
	int[][] houseLocations = new int[][]{{0,0},{0,2},{2,0},{2,2}};
	BufferedImage backgroundImg;
	BufferedImage houseImg;
	BufferedImage warehouseImg;
	BufferedImage chargingStationImg;
	
	BufferedImage packageImg;

	BufferedImage lightningImg;
	BufferedImage greenLightImg;
	BufferedImage redLightImg;
	
	int gridSize = 600;
	int squareSize = 150;
	int chargingStationSize = 125;
	int packageSize_Big = 50;
	int packageSize_Small = 20;
//	int houseSize = 125;
	int lightningSize = 25;
	int lightControlSize = 15;

	public GridPanel(MainFrame parentFrame) {
		this.parentFrame = parentFrame;
		this.drone = new Drone();
		initRequests();
				
		setBorder(BorderFactory.createLineBorder(new Color(0,0,0),1));
		setPreferredSize(new Dimension(594,700));
		try {
			backgroundImg = ImageIO.read(new File("img/background_grass.jpeg"));
			houseImg = ImageIO.read(new File("img/orange_house_gray.png"));
			warehouseImg = ImageIO.read(new File("img/warehouse.png"));
			chargingStationImg = ImageIO.read(new File("img/charging_station.png"));
			
			packageImg = ImageIO.read(new File("img/package_bright_color.png"));
			
			lightningImg = ImageIO.read(new File("img/lightning.png"));
			redLightImg = ImageIO.read(new File("img/RED.png"));
			greenLightImg = ImageIO.read(new File("img/GREEN.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//this.timer = new Timer(3000,this);
		this.timer = new Timer(100,this);
		repaint();
		this.timer.start();
	}
	

	@Override 
	public void paintComponent(Graphics g){
		int row,col;
		paintBackground(g);
		paintControlPanel(g);
		paintHouses(g);
		paintChargingStation(g);
		paintWarehouse(g);
		paintWarehouseBoard(g);

		// paint state details
		g.setColor(Color.black);
		row=0;
		col=3;
		g.drawString("State Details", col*squareSize + 20, row*squareSize + 20);
		g.drawString("State #: "+stateNum, col*squareSize + 20, row*squareSize + 40);
		
		// paint drone by it's new x,y coordinates
		g.drawImage(drone.getImage(),drone.getX(),drone.getY(),drone.getSize(),drone.getSize(),null);
	}
	
	private void paintBackground(Graphics g) {
		// fill "grass" background
//		g.setColor(new Color(50,160,70));
//		g.fillRect(0, 0, 600, 600);
		g.drawImage(this.backgroundImg, 0,0,gridSize,gridSize,null);
	}
	
	private void paintControlPanel(Graphics g) {
		int row,col;
		int paddingWide = 10;
		Color lightPrimary = new Color(167, 184, 212);
		Color primary = new Color(99, 118, 150);
		Color darkPrimary = new Color(59, 74, 99);
		// 700 is height of the grid
		int stringRow1 = 25,stringRow2 = 45,stringRow3 = 65,stringRow4;
		// fill "CONTROL PANEL"
		g.setFont(new Font("MV Boli",Font.BOLD,14));
		// Border
		g.setColor(Color.BLACK);
		g.drawLine(0, 600, 594, 600);
		// Background
		g.setColor(darkPrimary);
		g.fillRect(0, 600, 594, 100);
		g.setColor(primary);
		g.fillRect(10, 610, 574, 80);

		// paint environment toggles
		g.setColor(Color.BLACK);
		col = 0;
		row = 4;
		g.drawString("Priority Mode:", col*squareSize + paddingWide, row*squareSize + stringRow2);
		g.drawImage(priorityMode ? greenLightImg : redLightImg,(col+1)*squareSize - 30,row*squareSize + stringRow2 -12,lightControlSize,lightControlSize,null);
		g.drawString("Winds-Control: ", col*squareSize + paddingWide, row*squareSize + stringRow3);
		g.drawImage(windsMode ? greenLightImg : redLightImg,(col+1)*squareSize - 30,row*squareSize + stringRow3 -12,lightControlSize,lightControlSize,null);
		
		int houseNum;
		//paint drone details
		col = 1;
		g.drawString("Inventory:", col*squareSize + paddingWide, row*squareSize + stringRow1);
		g.drawString("Total:"+totalPackages+"/"+MAX_CAPACITY, col*squareSize + paddingWide, row*squareSize + stringRow2);
		g.drawString("To-WH:"+droneToWarehouseCap, col*squareSize + paddingWide, row*squareSize + stringRow3);
		for(houseNum=0;houseNum<4;houseNum++) { 
			int gapWide, gapHigh;
			gapWide = houseNum < 2 ? 80 : 150;
			gapHigh = houseNum % 2 == 0 ? stringRow2 : stringRow3;
			g.drawString("To-H"+(houseNum+1)+":"+droneToHouseCap[houseNum],col*squareSize+paddingWide+gapWide,row*squareSize+gapHigh);
		}
		
		//g.drawString("PUTS: "+pickUpThisState, col*squareSize + 20, row*squareSize + 60);
		//g.drawString("DOTS: "+dropOffThisState, col*squareSize + 20, row*squareSize + 80);
		
		// paint charging bar
		col = 3;
		g.drawString("Battery: ", col*squareSize + 40, row*squareSize + 35);
		g.drawImage(lightningImg,col*squareSize + 15,row*squareSize + 20,lightningSize,lightningSize,null);
		g.drawRect(col*squareSize + paddingWide, row*squareSize + 60, squareSize - 35 , 20);
		float batteryFilled = 1 - (this.energy / (float)MAX_ENERGY);
		Color currentBatteryColor = batteryFilled > 0.6 ? Color.green : batteryFilled < 0.2 ? Color.red : Color.orange;
		g.setColor(currentBatteryColor);
		g.fillRect(col*squareSize + paddingWide, row*squareSize + 60, Math.round((batteryFilled*(squareSize - 35)))+5, 20);
		
	}
	
	private void paintHouses(Graphics g) {
		int row,col;
		g.setFont(new Font("MV Boli",Font.BOLD,14));

		// "paint houses"
		int houseNum = 0;
		for(int[] location : houseLocations) {
			row = location[0];
			col = location[1];
			g.drawImage(houseImg, col*squareSize, row*squareSize, squareSize, squareSize, null);
			
			//g.setColor(Color.black);
			//g.drawString("House"+(houseNum+1), col*squareSize + 20, row*squareSize + 20);
			// TODO - add house numbers (to images?)
			if(houseMonitors[houseNum]){
//				g.setColor(Color.red);
//				g.drawString("Waiting!", col*squareSize + 20, (row+1)*squareSize - 40);
				g.drawImage(packageImg,col*squareSize + 10,(row+1)*squareSize - 60, packageSize_Big, packageSize_Big, null);
			}
			if(drone.isStocking()) {
				if(pickUpThisState == (houseNum+1)) {
//					g.setColor(Color.green);
//					g.drawString("Picked Up!", col*squareSize + 20, (row+1)*squareSize - 20);
				}			
				if(dropOffThisState == (houseNum+1)) {
//					g.setColor(Color.orange);
//					g.drawString("Drop-off!", col*squareSize + 20, (row+1)*squareSize - 5);
					g.drawImage(packageImg, (col+1)*squareSize - (packageSize_Big+10),(row+1)*squareSize - 60, packageSize_Big, packageSize_Big, null);
				}								
			}
			houseNum++;
		}		
	}

	private void paintChargingStation(Graphics g) {
		int row,col;
		//"paint charging station"
		row = 2;
		col = 3;
//		g.setColor(Color.yellow);
//		g.fillRect(col*squareSize, row*squareSize, squareSize, squareSize);
//		g.setColor(Color.black);
//		g.drawString("Charging Station", col*squareSize + 20, row*squareSize + 20);	
		g.drawImage(chargingStationImg, col*squareSize + 25, row*squareSize + 25, chargingStationSize, chargingStationSize, null);
	}

	private void paintWarehouse(Graphics g) {
		int row,col;
		// "paint warehouse"
		row = 3;
		col = 3;
//		g.setColor(Color.red);
//		g.fillRect(col*squareSize, row*squareSize, squareSize, squareSize);
//		g.setColor(Color.black);
//		g.drawString("Warehouse", col*squareSize + 20, row*squareSize + 20);
		g.drawImage(warehouseImg, col*squareSize, row*squareSize, squareSize, squareSize, null);
		if(drone.isStocking()) {
			if(dropOffThisState == 5) {
//				g.setColor(Color.orange);
//				g.drawString("Drop-off!", col*squareSize + 20, (row+1)*squareSize - 5);
				g.drawImage(packageImg, (col+1)*squareSize - (packageSize_Big+10),(row+1)*squareSize - 60, packageSize_Big, packageSize_Big, null);
			}			
		}		
	}
	
	private void paintWarehouseBoard(Graphics g) {
		int houseNum,row,col;
		// paint warehouse details
		row = 3;
		col = 3;
		g.setColor(Color.white);
		for(houseNum=0;houseNum<4;houseNum++) { 
			g.drawString("H"+(houseNum+1)+":", col*squareSize + 15,row*squareSize + 20*(houseNum+2));
			if(warehouseMonitors[houseNum]) {
				g.drawImage(packageImg, col*squareSize + 45, row*squareSize + 20*(houseNum+2)-15, packageSize_Small, packageSize_Small, null);
			}
		}	
	}





	/* HANDLE USER EVENTS*/

	public void addPickupRequest(int requestNumber) {
		if(requestNumber >= 1 && requestNumber <= 4) {
			this.houseRequests[requestNumber-1] = true;
		} else if (requestNumber >= 5 && requestNumber <= 8) {
			this.warehouseRequests[requestNumber-5] = true;
		}
		updateEnvironment();
		//getNewState();
		repaint();
	}
	
	public void togglePriority(boolean newPriority) {
		this.priorityMode = newPriority;
		updateEnvironment();
		//getNewState();
		repaint();
	}
	
	public void toggleWinds(boolean newWinds) {
		this.windsMode = newWinds;
		updateEnvironment();
		//getNewState();
		repaint();
	}

	// this is what the timer will generate at each delay
	// General approach:
	// * update all environment variables
	// * Update Drone
	// * paint component
	// * get new state from the controller, if needed
	@Override
	public void actionPerformed(ActionEvent e) {
		updateEnvironment();
		updateDrone();
		repaint();
		// When we have animation we will check that no animation is running before getting new state
		if(!(drone.isMoving() || drone.isStocking())) {
			getNewState();			
			stateNum++;
		}
	}
	
	private void updateDrone() {
		if(drone.isMoving()) {
			if(!drone.shouldMakeStop()) {
				drone.move();
			} else {
				// Drone should halt
				if(pickUpThisState > 0 || dropOffThisState > 0 || drone.isCharging()) {
					// MOVING SHOULD BE FALSE , STOCKING SHOULD BE TRUE
					drone.toggleMoving(false, true);					
				} else {
					// MOVING SHOULD BE FALSE , STOCKING SHOULD BE FALSE (I.E. IDLE)
					drone.toggleMoving(false, false);
				}
			}
		} else if (drone.isStocking()) {
			// hold here for some time, than change state to IDLE
			if(drone.stock()) { //returns true when done stocking
				drone.toggleMoving(false, false);				
			}
		} else {
			// drone is IDLE (standing and done stocking), need to calculate new animation
			int newRow = Integer.parseInt(controller.getSysVar("drone[0]"));
			int newCol = Integer.parseInt(controller.getSysVar("drone[1]"));
			drone.setNewDestination(newRow, newCol);
			// THERES NO STOCKING WHILE MOVING
			drone.toggleMoving(true,false);
		}
	}


	private void updateRequests() {
		// I assume that resetting a true package as true is ok, since we aggregate them anyways
		for(int i=0;i<houseRequests.length;i++) {
			controller.setEnvVar("outHousePackages["+i+"]",Boolean.toString(houseRequests[i]));
		}
		for(int j=0;j<warehouseRequests.length;j++) {
			controller.setEnvVar("outWarehousePackages["+j+"]",Boolean.toString(warehouseRequests[j]));
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
		int i;
		controller.updateState();
		// make updates to all GUI components field variables that needs changing.
		// Drone is updated on the updateDrone method
		
		// PUTS , DOTS
		this.pickUpThisState = Integer.parseInt(controller.getSysVar("pickUpThisState"));
		this.dropOffThisState = Integer.parseInt(controller.getSysVar("dropOffThisState"));
		// Inventory counters
		this.totalPackages = Integer.parseInt(controller.getSysVar("totalPackages"));
		for(i=0;i<NUM_OF_HOUSES;i++) {
			this.droneToHouseCap[i] = Integer.parseInt(controller.getSysVar("droneToHouseCap"+(i+1)));
		}
		this.droneToWarehouseCap = Integer.parseInt(controller.getSysVar("droneToWarehouseCap"));
		// Monitors
		for(i=0;i<NUM_OF_HOUSES;i++) {
			this.houseMonitors[i] = Boolean.parseBoolean(controller.getSysVar("waitingPackageOutHouse"+(i+1)));
		}
		for(i=0;i<NUM_OF_HOUSES;i++) {
			this.warehouseMonitors[i] = Boolean.parseBoolean(controller.getSysVar("waitingPackageInWarehouseToHouse"+(i+1)));
		}
		// Energy
		this.energy = Integer.parseInt(controller.getSysVar("energy"));
		// Priority
		this.priorityCap = Integer.parseInt(controller.getSysVar("priorityCap"));
		
		// After every new state reset the requests env. variables
		initRequests();
		
	}
	
	private void initRequests() {
		for(int i=0;i<houseRequests.length;i++) {
			houseRequests[i] = false;
			warehouseRequests[i] = false;
		}
	}
	
//	enum PackageType {
//		EMPTY,SMALL
//	}




	
	

}
