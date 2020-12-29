package deliveryDrone;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class App {

	public static void main(String[] args) {
		// HERE YOU CAN CHANGE THE GUI TO COMPANION A MODULIZED SPECTRA FILE
		// I.E. IF YOU REMOVE PRIORITY FEATURE AT THE SPEC. MAKE IT 'FALSE' HERE
	
		boolean priority = false ;
		boolean winds = false; 
		boolean energy = false; 
		JFrame frame = new JFrame("Config");
		final String[] options = { "default- priority, energy, wind","priority, wind","priority, energy","wind, energy","prirotiy", "wind", "energy","None of them"};
		String selected = (String) JOptionPane.showInputDialog(frame, 
				"Which features do you use?",
				"Config",
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				options, 
				options[0]);
		System.out.println(selected);
		if(selected!= null) {
			switch (selected) {
			case "default- priority, energy, wind" :
				priority=true;
				winds=true;
				energy=true;
				break;
			case "priority, wind":
				priority=true;
				winds=true;
				break;
			case "priority, energy":
				priority=true;
				energy=true;
				break;
			case "wind, energy":
				winds=true;
				energy=true;
				break;
			case "prirotiy":
				priority=true;
				break;
			case "wind":
				winds=true;
				break;
			case "energy":
				energy=true;
				break;
			case "None of them":
				break;
			}
		}
		else {
			priority=true;
			winds=true;
			energy=true;
		}
		final boolean priorityFeature = priority; 
		final boolean windsFeature = winds; 
		final boolean energyFeature = priority; 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame("DeliveryDrone",priorityFeature,windsFeature,energyFeature);
			}
		});
	}

}