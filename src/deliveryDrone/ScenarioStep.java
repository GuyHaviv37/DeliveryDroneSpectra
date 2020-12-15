package deliveryDrone;

abstract class ScenarioStep {
	private ScenarioNumber scenarioID; 
	private boolean[] houseRequestsValue; // house request this step
	private boolean[] warehouseRequestsValue; // warehouse request this step
	private boolean isWinds ; // is there winds this step?
	private boolean isPriority; // is there priority mode this step?
	
	private boolean hasStarted = false; // did the step start?
	private int stepNumber;

	public ScenarioStep(ScenarioNumber scenario, int stepNumber, boolean[] houseRequestsValue, boolean[] warehouseRequestsValue) {
		this.scenarioID = scenario;
		this.stepNumber = stepNumber;
		this.houseRequestsValue = houseRequestsValue;
		this.warehouseRequestsValue = warehouseRequestsValue;
		this.isWinds = false;
		this.isPriority = false;
	}

	public ScenarioStep(ScenarioNumber scenario, int stepNumber, boolean[] houseRequestsValue, boolean[] warehouseRequestsValue,boolean isWinds, boolean isPriority ) {
		this.scenarioID = scenario;
		this.stepNumber = stepNumber;
		this.houseRequestsValue = houseRequestsValue;
		this.warehouseRequestsValue = warehouseRequestsValue;
		this.isWinds = isWinds;
		this.isPriority = isWinds;
	}
	
	public void setHasStarted(boolean hasStarted) {
		this.hasStarted = hasStarted;
	}

	public ScenarioNumber getScenarioNumber() {
		return this.scenarioID;
	}

	public boolean[] getHouseRequestValue() {
		return this.houseRequestsValue;
	}

	public boolean[] getWarehouseRequestValue() {
		return this.warehouseRequestsValue;
	}

	public boolean getIsWinds() {
		return this.isWinds;
	}

	public boolean getIsPriority() {
		return this.isPriority;
	}

	public String getDescription() {
		return this.scenarioID.toString();
	}
	
	public int getStepNumber() {
		return stepNumber;
	}

	public boolean HasStarted() {
		return hasStarted;
	}
	
	/*
	 * Because each step has different target it wants to achieve, 
	 * each step defines it's own finishing state in order to let the scheduler know 
	 * when this step is finished and it can run the next step of the played scenario.
	 */
	public abstract boolean isFinished(int[] droneToHouseCap, int droneToWarehouseCap, boolean[] houseMonitors, boolean[] warehouseMonitors);
}
