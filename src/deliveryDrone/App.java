package deliveryDrone;
import javax.swing.SwingUtilities;

public class App {
	// HERE YOU CAN CHANGE THE GUI TO COMPANION A MODULIZED SPECTRA FILE
	// I.E. IF YOU REMOVE PRIORITY FEATURE AT THE SPEC. MAKE IT 'FALSE' HERE
	public static final boolean priorityFeature = false;
	public static final boolean windsFeature = false;
	public static final boolean energyFeature = false;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame("DeliveryDrone",priorityFeature,windsFeature,energyFeature);
			}
		});
	}

}