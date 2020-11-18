import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

	//	BufferedImage car;
	//	BufferedImage ambulance;

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

				while (true) {

					sysValues = executor.getCurrOutputs();
					updateSysVaribleValue(sysValues);
					printSysValue();
					//should paint here!
					System.out.println("should paint here");
					///////
					getEnvValueFromConsole();
					updateInputsWithCurrentVal(inputs);
					printEnvValue();
					executor.updateState(inputs);
				}
			}
		});
		animationThread.start();
		repaint();
	}
	public static void main(String[] args) {
		System.out.println("Hello drone");
		JFrame f = new JFrame("Drone Simulator");
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setSize(800, 500);
		App droneSim = new App();
		f.setContentPane(droneSim);
		f.setVisible(true);
	}

}
