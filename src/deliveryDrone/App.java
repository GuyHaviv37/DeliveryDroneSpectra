package deliveryDrone;
import javax.swing.SwingUtilities;

public class App {

	public static void main(String[] args) {
		boolean[] features = ModulizeFeature.getFeatures();
		if(features==null) {
			System.out.println("You must choose a feature set to continue");
			System.exit(0);
		}
		final boolean priorityFeature = features[0]; 
		final boolean windsFeature = features[1]; 
		final boolean energyFeature = features[2]; 
		String mode = ModulizeFeature.getTextChoice();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame("DeliveryDrone - "+mode,priorityFeature,windsFeature,energyFeature);
			}
		});
	}

}