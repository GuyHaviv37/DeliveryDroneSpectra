package deliveryDrone;
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

public class AppNoaConsole extends JComponent {

	final int NUM_OF_HOUSES = 4;

	// private static final long serialVersionUID = 1L; //no idea what it is

	ControllerExecutor executor;
	// env
	boolean[] outHousePackages = new boolean[NUM_OF_HOUSES];
	boolean[] outWarehousePackages = new boolean[NUM_OF_HOUSES];
	boolean priorityMode = false;
	boolean envelopeMode1 = false;
	boolean envelopeMode2 = false;
	boolean envelopeMode3 = false;
	boolean envelopeMode4 = false;
	// sys
	int[] drone = new int[2];
	int pickUpThisState;
	int dropoffThisState;

	// BufferedImage car;
	// BufferedImage ambulance;

	Thread thread;

	void initAllArraysToEmpty() {
		for (int i = 0; i < NUM_OF_HOUSES; i++) {
			outHousePackages[i] = false;
			outWarehousePackages[i] = false;
		}
	}

	void getEnvValueFromConsole() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("enter house # which get new out package (# between 0-3 include): ");
			String outHousePackages_console = reader.readLine();
			System.out.println("enter house # which get new package in warehouse (# between 0-3 include):");
			String warehousePackages_console = reader.readLine();
			System.out.println("enter 1 if priority mode");
			String getPriority = reader.readLine();
			System.out.println("enter house # which move to envelope mode (# between 0-3 include)");
			String getEnvelope = reader.readLine();
			System.out.println("enter house # which move to package mode (# between 0-3 include)");
			String resetEnvelope = reader.readLine();

			String[] split_houses = outHousePackages_console.split(" ");
			String[] split_warehouse = warehousePackages_console.split(" ");
			String[] split_priority = getPriority.split(" ");
			String[] split_envelope = getEnvelope.split(" ");
			String[] split_reset_envelope = resetEnvelope.split(" ");

			initAllArraysToEmpty();
			for (int i = 0; i < split_houses.length; i++) {
				outHousePackages[Integer.parseInt(split_houses[i])] = true;
			}
			for (int i = 0; i < split_warehouse.length; i++) {
				outWarehousePackages[Integer.parseInt(split_warehouse[i])] = true;
			}

			if (split_priority.length > 0) {
				if (Integer.parseInt(split_priority[0]) == 1) {
					priorityMode = true;
				}
				if (Integer.parseInt(split_priority[0]) == 0) {
					priorityMode = false;
				}
				System.out.println("parse priority mode " + split_priority[0] + " " + priorityMode);
			}
			for (int i = 0; i < split_envelope.length; i++) {
				if (Integer.parseInt(split_envelope[i]) == 0) {
					envelopeMode1 = true;
				}
				if (Integer.parseInt(split_envelope[i]) == 1) {
					envelopeMode2 = true;
				}
				if (Integer.parseInt(split_envelope[i]) == 2) {
					envelopeMode3 = true;
				}
				if (Integer.parseInt(split_envelope[i]) == 3) {
					envelopeMode4 = true;
				}
			}
			for (int i = 0; i < split_reset_envelope.length; i++) {
				if (Integer.parseInt(split_reset_envelope[i]) == 0) {
					envelopeMode1 = false;
				}
				if (Integer.parseInt(split_reset_envelope[i]) == 1) {
					envelopeMode2 = false;
				}
				if (Integer.parseInt(split_reset_envelope[i]) == 2) {
					envelopeMode3 = false;
				}
				if (Integer.parseInt(split_reset_envelope[i]) == 3) {
					envelopeMode4 = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	void updateInputsWithCurrentVal(Map<String, String> inputs) {
		for (int i = 0; i < NUM_OF_HOUSES; i++) {
			String outHouse = "outHousePackages[" + Integer.toString(i) + "]";
			String outWarehouse = "outWarehousePackages[" + Integer.toString(i) + "]";
			inputs.put(outHouse, String.valueOf(outHousePackages[i]));
			inputs.put(outWarehouse, String.valueOf(outWarehousePackages[i]));
			inputs.put("priorityMode", String.valueOf(priorityMode));
			inputs.put("envelopeMode1", String.valueOf(envelopeMode1));
			inputs.put("envelopeMode2", String.valueOf(envelopeMode2));
			inputs.put("envelopeMode3", String.valueOf(envelopeMode3));
			inputs.put("envelopeMode4", String.valueOf(envelopeMode4));
		}
	}

	void updateSysVaribleValue(Map<String, String> sysValues) {

		drone[0] = Integer.parseInt(sysValues.get("drone[0]"));
		drone[1] = Integer.parseInt(sysValues.get("drone[1]"));

		for (int i = 0; i < NUM_OF_HOUSES; i++) {
			pickUpThisState = Integer.parseInt(sysValues.get("pickUpThisState"));
			dropoffThisState = Integer.parseInt(sysValues.get("dropOffThisState"));
		}
	}

	void printEnvValue() {
		System.out.println("********************Envirenment varibales*******************");
		for (int i = 0; i < NUM_OF_HOUSES; i++) {
			if (outHousePackages[i] != false) {
				System.out.print(" | outHousePackages[" + Integer.toString(i) + "]= ");
				System.out.print(outHousePackages[i]);
			}
		}
		System.out.println();
		for (int i = 0; i < NUM_OF_HOUSES; i++) {
			if (outWarehousePackages[i] != false) {
				System.out.print(" | outWarehousePackages[" + Integer.toString(i) + "]= ");
				System.out.print(outWarehousePackages[i]);
			}
		}
		System.out.println();
		System.out.println("priority mode = " + priorityMode);
		System.out.println();
		System.out.println("envelopemode1 = " + envelopeMode1);
		System.out.println("envelopemode2 = " + envelopeMode2);
		System.out.println("envelopemode3 = " + envelopeMode3);
		System.out.println("envelopemode4 = " + envelopeMode4);
		System.out.println();
	}

	void printSysValue(Map<String, String> sysValues) {
		System.out.println("*************************system varibales*************************");
		System.out.println("drone[0] = " + drone[0] + "  |  drone[1] = " + drone[1]);
		if (drone[0] == 2 && drone[1] == 3) {
			System.out.println("charging!!!!");
		}
		if (dropoffThisState > 0) {
			System.out.println("dropOffThisState= " + dropoffThisState);
		}
		if (pickUpThisState > 0) {
			System.out.println("pickUpThisState= " + pickUpThisState);
		}
		System.out.println("priority counter= " + sysValues.get("priorityCap"));
		System.out.println("energy counter= " + sysValues.get("energy"));
		System.out.println("*************************dron capacity*************************");
		System.out.println("droneTWHCap= " + sysValues.get("droneToWarehouseCap"));
		System.out.println("droneTHCap1= " + sysValues.get("droneToHouseCap1"));
		System.out.println("droneTHCap2= " + sysValues.get("droneToHouseCap2"));
		System.out.println("droneTHCap3= " + sysValues.get("droneToHouseCap3"));
		System.out.println("droneTHCap4= " + sysValues.get("droneToHouseCap4"));
		System.out.println("*************************waiting in warehouse*************************");
		System.out.println("waitingInWarehouseToHouse1= " + sysValues.get("waitingPackageInWarehouseToHouse1"));
		System.out.println("waitingInWarehouseToHouse2= " + sysValues.get("waitingPackageInWarehouseToHouse2"));
		System.out.println("waitingInWarehouseToHouse3= " + sysValues.get("waitingPackageInWarehouseToHouse3"));
		System.out.println("waitingInWarehouseToHouse4= " + sysValues.get("waitingPackageInWarehouseToHouse4"));
		System.out.println("*************************waiting in house#*************************");
		System.out.println("waitingHouse1= " + sysValues.get("waitingPackageOutHouse1"));
		System.out.println("waitingHouse2= " + sysValues.get("waitingPackageOutHouse2"));
		System.out.println("waitingHouse3= " + sysValues.get("waitingPackageOutHouse3"));
		System.out.println("waitingHouse4= " + sysValues.get("waitingPackageOutHouse4"));
		System.out.println("*************************drone inventory #*************************");
		System.out.println("total packages= " + sysValues.get("totalPackages"));
		System.out.println("total envelope= " + sysValues.get("totalEnvelopesToWH"));
		System.out.println("to WH package= " + sysValues.get("droneToWarehouseCap"));
		System.out.println("to HW1= " + sysValues.get("droneToHouseCap1"));
		System.out.println("to HW2= " + sysValues.get("droneToHouseCap2"));
		System.out.println("to HW3= " + sysValues.get("droneToHouseCap3"));
		System.out.println("to HW4= " + sysValues.get("droneToHouseCap4"));
		System.out.println("*******************END STATE**********************");
	}

	public AppNoaConsole() {
		Thread animationThread = new Thread(new Runnable() {
			public void run() {
				try {
					executor = new ControllerExecutor(new BasicJitController(), "out");
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
				Map<String, String> inputs = new HashMap<String, String>();
				Map<String, String> sysValues;
				initAllArraysToEmpty(); // keep asm on the env
				updateInputsWithCurrentVal(inputs);
				printEnvValue();
				executor.initState(inputs);

				while (true) {

					sysValues = executor.getCurrOutputs();
					// System.out.println(sysValues);
					updateSysVaribleValue(sysValues);
					printSysValue(sysValues);

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
		AppNoaConsole droneSim = new AppNoaConsole();
		f.setVisible(true);
	}

}