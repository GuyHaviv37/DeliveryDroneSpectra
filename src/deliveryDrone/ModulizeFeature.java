package deliveryDrone;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ModulizeFeature {
	
	public static boolean[] getFeature(){
		boolean[] result = new boolean[3];
		boolean priority = false ;
		boolean winds = false; 
		boolean energy = false; 
		JFrame frame = new JFrame("Config");
		final String[] options = { "Default - All Features Enabled","Priority and Winds Mode Only",
				"Priority Mode and Energy Only","Winds Mode and Energy Only",
				"Priority Mode Only", "Winds Mode Only",
				"Energy Only","No Extra Features"};
		String selected = (String) JOptionPane.showInputDialog(frame, 
				"Which Features Does Your Specification Use?",
				"Config",
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				options, 
				options[0]);
		if(selected!= null) {
			switch (selected) {
			case "Default - All Features Enabled" :
				priority=true;
				winds=true;
				energy=true;
				break;
			case "Priority and Winds Mode Only":
				priority=true;
				winds=true;
				break;
			case "Priority Mode and Energy Only":
				priority=true;
				energy=true;
				break;
			case "Winds Mode and Energy Only":
				winds=true;
				energy=true;
				break;
			case "Priority Mode Only":
				priority=true;
				break;
			case "Winds Mode Only":
				winds=true;
				break;
			case "Energy Only":
				energy=true;
				break;
			case "No Extra Features":
				break;
			}
		}
		else {
			priority=true;
			winds=true;
			energy=true;
		}
		result[0]=priority;
		result[1]= winds;
		result[2]=energy;
		return result;
	}

}
