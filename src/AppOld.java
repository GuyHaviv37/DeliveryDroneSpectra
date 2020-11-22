import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.controller.jit.BasicJitController;



public class AppOld extends JComponent {

	final int NUM_OF_HOUSES = 4;

	//private static final long serialVersionUID = 1L; //no idea what it is

	ControllerExecutor executor;
	//env
	Map<String,String> inputs;
	boolean[] outHousePackages = new boolean[NUM_OF_HOUSES];
	boolean[] outWarehousePackages= new boolean[NUM_OF_HOUSES];
	//sys
	Map<String, String> sysValues;
	int[] drone= new int[2];
	int[] droneToHouseCap= new int[NUM_OF_HOUSES];
	int droneToWarehouseCap;
//	boolean[] pickUpThisStateFromWareHouse= new boolean[NUM_OF_HOUSES];
//	boolean[] pickUpThisStateFromHouse= new boolean[NUM_OF_HOUSES];
	int pickUpThisState;
	int dropOffThisState;
	int totalPackages=0;
	// simulation variables
	BufferedImage droneImg;
	int squareSize = 145;
	int droneSize = 125;
	int stateNum = 0;

	Thread thread;
	
	public AppOld() {
		Thread animationThread = new Thread(new Runnable() {
			public void run() {
				try {
					executor = new ControllerExecutor(new BasicJitController(), "out");
				}
				catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
				inputs = new HashMap<String, String>();
				
				initAllArraysToFalse(); // keep asm on the env
				updateInputsWithCurrentVal(inputs);
				//printEnvValue();
				executor.initState(inputs);
				repaint();

				while (true) {

					sysValues = executor.getCurrOutputs();
					updateSysVaribleValue(sysValues);
					//printSysValue();
					
					repaint();

					getEnvValueFromConsole();
					updateInputsWithCurrentVal(inputs);
					printEnvValue();
					System.out.println(stateNum);
					executor.updateState(inputs);
					
					stateNum++;
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
				}
			}
		});
		try {
			droneImg = ImageIO.read(new File("img/drone.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		animationThread.start();
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		int row;
		int col;
		g.setFont(new Font("MV Boli",Font.BOLD,14));
		// fill "grass" background
		g.setColor(new Color(50,160,70));
		g.fillRect(0, 0, 600, 600);
		
		// "paint houses"
		int houseNum = 0;
		for(row=0;row<=2;row+=2) {
			for(col=0;col<=2;col+=2) {
				g.setColor(new Color(50,60,210));
				g.fillRect(col*squareSize, row*squareSize, squareSize, squareSize);
				g.setColor(Color.black);
				g.drawString("House"+(houseNum), col*squareSize + 20, row*squareSize + 20);
				if(sysValues != null) {
					if(Boolean.parseBoolean(sysValues.get("waitingPackageOutHouse"+houseNum))){
						g.setColor(Color.red);
						g.drawString("Waiting!", col*squareSize + 20, (row+1)*squareSize - 40);
					}
					if(pickUpThisState == (houseNum+1)) {
						g.setColor(Color.green);
						g.drawString("Picked Up!", col*squareSize + 20, (row+1)*squareSize - 20);
					}					
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
		
		// paint warehouse details
		row = 3;
		col = 2;
		if(sysValues!=null) {
			g.drawString("WH Details",col*squareSize + 20, row*squareSize + 20);
			for(houseNum=0;houseNum<4;houseNum++) { 
				String waiting = Boolean.parseBoolean(sysValues.get("waitingPackageInWarehouseToHouse"+houseNum)) ? "W" : "";
				String pickedUp = pickUpThisState == (houseNum+5) ? "PU" : "";
				g.drawString("H"+houseNum+": "+waiting+" "+pickedUp, col*squareSize + 20, row*squareSize + 20*(houseNum+2));
			}			
		}
		
		//paint drone details
		row = 3;
		col = 0;
		g.drawString("Drone Details", col*squareSize + 20, row*squareSize + 20);
		g.drawString("Total: "+totalPackages, col*squareSize + 20, row*squareSize + 40);
		g.drawString("To-WH: "+droneToWarehouseCap, col*squareSize + 20, row*squareSize + 60);
		for(houseNum=0;houseNum<4;houseNum++) { 
			g.drawString("To-H"+houseNum+": "+droneToHouseCap[houseNum], col*squareSize + 20, row*squareSize + 20*(houseNum+4));
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
	
	void initAllArraysToFalse() {
		for(int i=0;i<NUM_OF_HOUSES;i++) {
			outHousePackages[i]=false;
			outWarehousePackages[i]=false;
		}
	}
	void getEnvValueFromConsole()  {
		try {
			BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in)); 
			System.out.println("Enter House# for outgoing package request (# between 0-3 include): example- 2 3");
			String outHousePackages_console = reader.readLine(); 
			System.out.println("Enter House# for warehouse package request (# between 0-3 include): example- 1 0");
			String warehousePackages_console = reader.readLine();  

			String[] split_houses = outHousePackages_console.split(" ");
			String[] split_warehouse = warehousePackages_console.split(" ");
			initAllArraysToFalse();
			for(int i=0; i<split_houses.length;i++) {
				outHousePackages[Integer.parseInt(split_houses[i])]=true;
			}
			for(int i=0; i<split_warehouse.length;i++) {
				outWarehousePackages[Integer.parseInt(split_warehouse[i])]=true;
			}
			System.out.println("-----------------------------------------------------");
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	void updateInputsWithCurrentVal(Map<String,String> inputs) {
		for(int i=0; i<NUM_OF_HOUSES;i++) {
			String outHouse = "outHousePackages[" + Integer.toString(i)+"]";
			String outWarehouse = "outWarehousePackages[" + Integer.toString(i)+"]";
			inputs.put(outHouse, Boolean.toString(outHousePackages[i]) );
			inputs.put(outWarehouse, Boolean.toString(outWarehousePackages[i]));
		}
	}
	void updateSysVaribleValue(Map<String, String> sysValues) {

		drone[0] = Integer.parseInt(sysValues.get("drone[0]"));
		drone[1] = Integer.parseInt(sysValues.get("drone[1]"));
		droneToWarehouseCap = Integer.parseInt(sysValues.get("droneToWarehouseCap"));
		for(int i=0; i<NUM_OF_HOUSES;i++) {
			droneToHouseCap[i] = Integer.parseInt(sysValues.get("droneToHouseCap"+Integer.toString(i)));
//			pickUpThisStateFromWareHouse[i] = Boolean.parseBoolean(sysValues.get("pickUpThisStateFromWareHouse["+Integer.toString(i)+"]"));
//			pickUpThisStateFromHouse[i] = Boolean.parseBoolean(sysValues.get("pickUpThisStateFromHouse["+Integer.toString(i)+"]"));
		}	
		totalPackages =Integer.parseInt(sysValues.get("totalPackages"));
		pickUpThisState = Integer.parseInt(sysValues.get("pickUpThisState"));
		dropOffThisState = Integer.parseInt(sysValues.get("dropOffThisState"));
		
	}
	void printEnvValue() {
		System.out.println("Envirenment varibales");
		for(int i=0; i<NUM_OF_HOUSES;i++) {
			System.out.print("  |  outHousePackages["+Integer.toString(i)+"]= ");
			System.out.print(inputs.get("outHousePackages["+i+"]"));
		}
		System.out.println();
		for(int i=0; i<NUM_OF_HOUSES;i++) {
			System.out.print("  |  outWarehousePackages["+Integer.toString(i)+"]= ");
			System.out.print(inputs.get("outWarehousePackages["+i+"]"));
		}
		System.out.println();
	}
	void printSysValue() {
		System.out.println("system varibales");
		System.out.println("totalPackages= " + totalPackages);
		System.out.println("drone[0] = " + drone[0] +"  |  drone[1] = " +drone[1]);
		System.out.println("droneToWarehouseCap = " + droneToWarehouseCap);
		for(int i=0; i<NUM_OF_HOUSES;i++) {
			System.out.print("  |  droneToHouseCap["+Integer.toString(i)+"]= ");
			System.out.print(droneToHouseCap[i]);
		}
		System.out.println();
//		for(int i=0; i<NUM_OF_HOUSES;i++) {
//			System.out.print("  |  pickUpThisStateFromWareHouse["+Integer.toString(i)+"]= ");
//			System.out.print(pickUpThisStateFromWareHouse[i]);
//		}
//		System.out.println();
//		for(int i=0; i<NUM_OF_HOUSES;i++) {
//			System.out.print("  |  pickUpThisStateFromHouse["+Integer.toString(i)+"]= ");
//			System.out.print(pickUpThisStateFromHouse[i]);
//		}	
		System.out.println("pickUpThisState = "+pickUpThisState);
		System.out.println();
	}
	
//	public static void main(String[] args) {
//		// Frame init
//		JFrame frame = new JFrame("DeliveryDrone");
//		frame.setSize(894,617);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setResizable(false);
//		frame.setLocationRelativeTo(null);
//		frame.setLayout(new BorderLayout());
//		
//		AppOld app = new AppOld();
//		app.setPreferredSize(new Dimension(594,617));
//		frame.add(app,BorderLayout.WEST);
//		
//		JPanel panel = new MenuPanelOld(app.outHousePackages,app.outWarehousePackages);
//		frame.add(panel,BorderLayout.EAST);
//		
//		frame.setVisible(true);
//	}

}
