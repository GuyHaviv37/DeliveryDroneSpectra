import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.controller.jit.BasicJitController;

public class DroneController {
	DroneState droneState = new DroneState();
	ControllerExecutor executor;
	
	public DroneController() {
		Map<String,String> inputs = new HashMap<>();
		for(Map.Entry<String,String> e : droneState.getEnvValues().entrySet()) {
			inputs.put(e.getKey(), e.getValue());
		}
		try {
			executor = new ControllerExecutor(new BasicJitController(),"out");
			executor.initState(inputs);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void updateState() {
		Map<String,String> inputs = new HashMap<>();
		try {
			// Update inputs from environment variables
			for(Map.Entry<String,String> e : droneState.getEnvValues().entrySet()) {
				inputs.put(e.getKey(), e.getValue());
			}
			// Update executor state
			executor.updateState(inputs);
			System.out.println("Updated state");
			// Update system variables from new state
			for(Map.Entry<String,String> e : executor.getCurrOutputs().entrySet()) {
				droneState.setSysVar(e.getKey(), e.getValue());
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public String getEnvVar(String key) {
		return this.droneState.getEnvVar(key);
	}
	
	public void setEnvVar(String key, String value) {
		this.droneState.setEnvVar(key, value);
	}

	public String getSysVar(String key) {
		return this.droneState.getSysVar(key);
	}

	public HashMap<String, String> getSysValues() {
		return this.droneState.getSysValues();
	}
}
