import java.util.HashMap;

public class DroneState {

	private static final int NUM_OF_HOUSES = 4;
	private HashMap<String,String> sysValues = new HashMap<>();
	private HashMap<String,String> envValues = new HashMap<>();
	
	public DroneState() {
		// Init both maps to have clear definitions of keys it can accept, with default values.
		initEnvValues();
		initSysValues();
	}
	
	private void initEnvValues() {
		for(int i=0;i<NUM_OF_HOUSES;i++) {
			// TODO
			envValues.put("outHousePackages["+i+"]", "EMPTY");
			envValues.put("outWarehousePackages["+i+"]", "EMPTY");
//			envValues.put("outHousePackages["+i+"]", "false");
//			envValues.put("outWarehousePackages["+i+"]", "false");
		}
		// TODO
		//envValues.put("priorityMode", "false");
		//envValues.put("windsMode", "false");
	}
	
	private void initSysValues() {
		// drone location
		sysValues.put("drone[0]", "2");
		sysValues.put("drone[1]", "3");
		// PUTS , DOTS
		sysValues.put("pickUpThisState","0");
		sysValues.put("dropOffThisState" , "0");
		// Inventory counters
		sysValues.put("totalPackages", "0");
		sysValues.put("droneToWarehouseCap", "0");
		sysValues.put("droneToHouseCap1", "0");
		sysValues.put("droneToHouseCap2", "0");
		sysValues.put("droneToHouseCap3", "0");
		sysValues.put("droneToHouseCap4", "0");
		// Monitors
		sysValues.put("waitingPackageOutHouse1", "false");
		sysValues.put("waitingPackageOutHouse2", "false");
		sysValues.put("waitingPackageOutHouse3", "false");
		sysValues.put("waitingPackageOutHouse4", "false");
		sysValues.put("waitingPackageInWarehouseToHouse1", "false");
		sysValues.put("waitingPackageInWarehouseToHouse2", "false");
		sysValues.put("waitingPackageInWarehouseToHouse3", "false");
		sysValues.put("waitingPackageInWarehouseToHouse4", "false");
		// Energy - TODO
		sysValues.put("energy", "0");
		// Priority - TODO
		sysValues.put("priorityCap", "0");
	}
	
	public HashMap<String,String> getSysValues(){
		return sysValues;
	}
	
	public HashMap<String,String> getEnvValues(){
		return envValues;
	}
	
	public String getSysVar(String key) {
		if(this.sysValues.containsKey(key)) {
			return sysValues.get(key);
		}
		return "N/A";
	}
	
	public String getEnvVar(String key) {
		if(this.envValues.containsKey(key)) {
			return envValues.get(key);
		}
		return "N/A";
	}
	
	public void setSysVar(String key,String value) {
		if(this.sysValues.containsKey(key)) {
			sysValues.put(key, value);
		}
	}
	
	public void setEnvVar(String key,String value) {
		if(this.envValues.containsKey(key)) {
			envValues.put(key, value);
		}
	}
}
