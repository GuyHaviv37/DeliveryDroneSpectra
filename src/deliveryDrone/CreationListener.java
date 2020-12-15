package deliveryDrone;

public interface CreationListener {

	public void addRequest(int requestNumber);

	public void togglePriority(boolean newPriority);

	public void toggleWinds(boolean newWinds);
	
	public void toggleDemo(boolean isDemo);
	
	public void createScenario(int scenarioNumber);
}
