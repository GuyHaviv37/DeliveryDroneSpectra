package deliveryDrone;
import javax.swing.SwingUtilities;

public class App {

	public static void main(String[] args) {
		boolean[] features = ModulizeFeature.getFeature();
		if(features==null) {
			System.out.println("you don't choose any option- exit the program");
			System.exit(0);
		}
		final boolean priorityFeature = features[0]; 
		final boolean windsFeature = features[1]; 
		final boolean energyFeature = features[2]; 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame("DeliveryDrone",priorityFeature,windsFeature,energyFeature);
			}
		});
	}

}