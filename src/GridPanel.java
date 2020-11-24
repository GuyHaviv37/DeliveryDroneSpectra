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
	private MainFrame parentFrame;
	private DroneController controller = new DroneController();
	// ENV in-house variables
	private boolean[] houseRequests = new boolean[NUM_OF_HOUSES];
	private boolean[] warehouseRequests = new boolean[NUM_OF_HOUSES];
	
	// SYS in-house variables
	private int[] drone = new int[]{2,3};
	private int pickUpThisState = 0;
	private int dropOffThisState = 0;
	private int totalPackages = 0;
	private int[] droneToHouseCap= new int[NUM_OF_HOUSES];
	private int droneToWarehouseCap;
	private boolean[] houseMonitors = new boolean[NUM_OF_HOUSES];
	private boolean[] warehouseMonitors = new boolean[NUM_OF_HOUSES];
	
	// AUX
	private Timer timer;
	private int stateNum = 0;
	// pauseTimers for animations;
	// SCENARIO QUEUE
	
	// BASIC SIMULATION VARIABLES
	BufferedImage droneImg;
	int squareSize = 150;
	int droneSize = 125;

	public GridPanel(MainFrame parentFrame) {
		this.parentFrame = parentFrame;
		
		setBorder(BorderFactory.createLineBorder(new Color(0,0,0),1));
		setPreferredSize(new Dimension(594,600));
		try {
			droneImg = ImageIO.read(new File("img/drone.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.timer = new Timer(3000,this);
		repaint();
		this.timer.start();
	}
	
	@Override 
	public void paintComponent(Graphics g){
		int row;
		int col;
		g.setFont(new Font("MV Boli",Font.BOLD,14));
		// fill "grass" background
		g.setColor(new Color(50,160,70));
		g.fillRect(0, 0, 594, 617);
		
		// "paint houses"
		int houseNum = 0;
		for(row=0;row<=2;row+=2) {
			for(col=0;col<=2;col+=2) {
				g.setColor(new Color(50,60,210));
				g.fillRect(col*squareSize, row*squareSize, squareSize, squareSize);
				g.setColor(Color.black);
				g.drawString("House"+(houseNum+1), col*squareSize + 20, row*squareSize + 20);
				if(houseMonitors[houseNum]){
					g.setColor(Color.red);
					g.drawString("Waiting!", col*squareSize + 20, (row+1)*squareSize - 40);
				}
				if(pickUpThisState == (houseNum+1)) {
					g.setColor(Color.green);
					g.drawString("Picked Up!", col*squareSize + 20, (row+1)*squareSize - 20);
				}			
				if(dropOffThisState == (houseNum+1)) {
					g.setColor(Color.orange);
					g.drawString("Drop-off!", col*squareSize + 20, (row+1)*squareSize - 5);
				}
				houseNum++;
			}
		}
		
		//"paint charging station"
		g.setColor(Color.yellow);
		row = 2;
		col = 3;
		g.fillRect(col*squareSize, row*squareSize, squareSize, squareSize);
		g.setColor(Color.black);
		g.drawString("Charging Station", col*squareSize + 20, row*squareSize + 20);
		
		// "paint warehouse"
		g.setColor(Color.red);
		row = 3;
		col = 3;
		g.fillRect(col*squareSize, row*squareSize, squareSize, squareSize);
		g.setColor(Color.black);
		g.drawString("Warehouse", col*squareSize + 20, row*squareSize + 20);
		if(dropOffThisState == 5) {
			g.setColor(Color.orange);
			g.drawString("Drop-off!", col*squareSize + 20, (row+1)*squareSize - 5);
		}
		
		// paint warehouse details
		row = 3;
		col = 2;
		g.setColor(Color.black);
		g.drawString("WH Details",col*squareSize + 20, row*squareSize + 20);
		for(houseNum=0;houseNum<4;houseNum++) { 
			String waiting = warehouseMonitors[houseNum] ? "W" : "";
			String pickedUp = pickUpThisState == (houseNum+5) ? "PU" : "";
			g.drawString("H"+(houseNum+1)+": "+waiting+" "+pickedUp, col*squareSize + 20, row*squareSize + 20*(houseNum+2));
		}	
		
		//paint drone details
		row = 3;
		col = 0;
		g.drawString("Drone Details", col*squareSize + 20, row*squareSize + 20);
		g.drawString("Total: "+totalPackages, col*squareSize + 20, row*squareSize + 40);
		g.drawString("To-WH: "+droneToWarehouseCap, col*squareSize + 20, row*squareSize + 60);
		for(houseNum=0;houseNum<4;houseNum++) { 
			g.drawString("To-H"+(houseNum+1)+": "+droneToHouseCap[houseNum], col*squareSize + 20, row*squareSize + 20*(houseNum+4));
		}
		
		// paint state details
		row=0;
		col=3;
		g.drawString("State Details", col*squareSize + 20, row*squareSize + 20);
		g.drawString("State #: "+stateNum, col*squareSize + 20, row*squareSize + 40);
		g.drawString("PUTS: "+pickUpThisState, col*squareSize + 20, row*squareSize + 60);
		g.drawString("DOTS: "+dropOffThisState, col*squareSize + 20, row*squareSize + 80);
		
		// "paint drone"
		g.drawImage(droneImg, this.drone[1]*squareSize, this.drone[0]*squareSize, droneSize, droneSize, null);
	}

	public void addPickupRequest(int requestNumber) {
		if(requestNumber >= 1 && requestNumber <= 4) {
			this.houseRequests[requestNumber-1] = true;
		} else if (requestNumber >= 5 && requestNumber <= 8) {
			this.warehouseRequests[requestNumber-5] = true;
		}
		updateRequests();
		getNewState();
		repaint();
	}

	// this is what the timer will generate at each delay
	// General approach:
	// * update all environment variables
	// * paint component
	// * get new state from the controller
	@Override
	public void actionPerformed(ActionEvent e) {
		updateRequests();
		repaint();
		// When we have animation we will check that no animation is running before getting new state
		getNewState();
		stateNum++;
	}
	
	private void updateRequests() {
		for(int i=0;i<houseRequests.length;i++) {
			controller.setEnvVar("outHousePackages["+i+"]",Boolean.toString(houseRequests[i]));
		}
		for(int j=0;j<warehouseRequests.length;j++) {
			controller.setEnvVar("outWarehousePackages["+j+"]",Boolean.toString(warehouseRequests[j]));
		}
		for(int k=0;k<NUM_OF_HOUSES;k++) {
			houseRequests[k] = false;
			warehouseRequests[k] = false;
		}
	}

	private void getNewState() {
		int i;
		controller.updateState();
		// make updates to all GUI components field variables that needs changing.
		//drone
		this.drone[0] = Integer.parseInt(controller.getSysVar("drone[0]"));
		this.drone[1] = Integer.parseInt(controller.getSysVar("drone[1]"));
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
		
	}


	
	

}