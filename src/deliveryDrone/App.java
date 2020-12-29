package deliveryDrone;
import javax.swing.SwingUtilities;

public class App {

	public static void main(String[] args) {
		Object[] features = ModulizeFeature.getFeature();
		if(features==null) {
			System.out.println("you don't choose any option- exit the program");
			System.exit(0);
		}
		final boolean priorityFeature =(boolean) features[0]; 
		final boolean windsFeature = (boolean)features[1]; 
		final boolean energyFeature = (boolean)features[2]; 
		String mode= (String)features[3];
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame("DeliveryDrone- "+mode,priorityFeature,windsFeature,energyFeature);
			}
		});
	}

}