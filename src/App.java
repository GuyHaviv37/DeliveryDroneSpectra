import java.awt.Color;
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
import javax.swing.WindowConstants;

import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.controller.jit.BasicJitController;



public class App extends JComponent {

	final int NUM_OF_HOUSES = 4;

	//private static final long serialVersionUID = 1L; //no idea what it is

	ControllerExecutor executor;
	//env
	boolean[] outHousePackages = new boolean[NUM_OF_HOUSES];
	boolean[] outWarehousePackages= new boolean[NUM_OF_HOUSES];
	//sys
	int[] drone= new int[2];
	int[] droneToHouseCap= new int[NUM_OF_HOUSES];
	int droneToWarehouseCap;
	boolean[] pickUpThisStateFromWareHouse= new boolean[NUM_OF_HOUSES];
	boolean[] pickUpThisStateFromHouse= new boolean[NUM_OF_HOUSES];
	int totalPackages=0;
	// simulation variables
	BufferedImage droneImg;
	int squareSize = 145;
	int droneSize = 125;

	Thread thread;
	void initAllArraysToFalse() {
		for(int i=0;i<NUM_OF_HOUSES;i++) {
			outHousePackages[i]=false;
			outWarehousePackages[i]=false;
		}
	}
	void getEnvValueFromConsole()  {
		try {
			BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in)); 
			System.out.println("enter house # which get new out package (# between 0-3 include): example- 2 3");
			String outHousePackages_console = reader.readLine(); 
			System.out.println("enter house # which get new package in warehouse (# between 0-3 include): example- 1 0");
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
			droneToHouseCap[i] = Integer.parseInt(sysValues.get("droneToHouseCap["+Integer.toString(i)+"]"));
			pickUpThisStateFromWareHouse[i] = Boolean.parseBoolean(sysValues.get("pickUpThisStateFromWareHouse["+Integer.toString(i)+"]"));
			pickUpThisStateFromHouse[i] = Boolean.parseBoolean(sysValues.get("pickUpThisStateFromHouse["+Integer.toString(i)+"]"));
		}	
		totalPackages =Integer.parseInt(sysValues.get("totalPackages"));
	}
	void printEnvValue() {
		System.out.println("Envirenment varibales");
		for(int i=0; i<NUM_OF_HOUSES;i++) {
			System.out.print("  |  outHousePackages["+Integer.toString(i)+"]= ");
			System.out.print(outHousePackages[i]);
		}
		System.out.println();
		for(int i=0; i<NUM_OF_HOUSES;i++) {
			System.out.print("  |  outWarehousePackages["+Integer.toString(i)+"]= ");
			System.out.print(outWarehousePackages[i]);
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
		for(int i=0; i<NUM_OF_HOUSES;i++) {
			System.out.print("  |  pickUpThisStateFromWareHouse["+Integer.toString(i)+"]= ");
			System.out.print(pickUpThisStateFromWareHouse[i]);
		}
		System.out.println();
		for(int i=0; i<NUM_OF_HOUSES;i++) {
			System.out.print("  |  pickUpThisStateFromHouse["+Integer.toString(i)+"]= ");
			System.out.print(pickUpThisStateFromHouse[i]);
		}	
		System.out.println();
	}
	
	public App() {
		Thread animationThread = new Thread(new Runnable() {
			public void run() {
				try {
					executor = new ControllerExecutor(new BasicJitController(), "out");
				}
				catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
				Map<String,String> inputs = new HashMap<String, String>();
				Map<String, String> sysValues;
				initAllArraysToFalse(); // keep asm on the env
				updateInputsWithCurrentVal(inputs);
				printEnvValue();
				executor.initState(inputs);
				repaint();

				while (true) {

					sysValues = executor.getCurrOutputs();
					updateSysVaribleValue(sysValues);
					printSysValue();
					
					repaint();

					getEnvValueFromConsole();
					updateInputsWithCurrentVal(inputs);
					printEnvValue();
					executor.updateState(inputs);
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
		
		// "paint drone"
		g.drawImage(droneImg, this.drone[1]*squareSize, this.drone[0]*squareSize, droneSize, droneSize, null);
		
	}
	
	public static void main(String[] args) {
		// Frame init
		JFrame frame = new JFrame("DeliveryDrone");
		frame.setSize(594,617);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		App app = new App();
		
		frame.setContentPane(app);
		frame.setVisible(true);
	}

}
