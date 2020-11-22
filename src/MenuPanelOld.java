import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanelOld extends JPanel implements ActionListener{
	
	JPanel buttonsContainer;
	boolean[] outHousePackages;
	boolean[] outWarehousePackages;
	JButton[] buttons = new JButton[8];
	MenuPanelOld(boolean[] outHousePackages, boolean[] outWareousePackages){
		this.outHousePackages = outHousePackages;
		this.outWarehousePackages = outWareousePackages;
		
		this.setPreferredSize(new Dimension(300,617));
		this.setBackground(Color.gray);
		this.add(new JLabel("Control Buttons:"));
		
		buttonsContainer = new JPanel();
		buttonsContainer.setLayout(new GridLayout(4,2,5,5));
		
		for(int i=0;i<8;i++) {
			String buttonLabel = "";
			if(i>=0 && i<=3) {
				buttonLabel += "outHouse "+i;
			} else buttonLabel += "outWareHouse "+(i-4);
			
			buttons[i] = new JButton(buttonLabel);
			buttons[i].addActionListener(this);
			buttonsContainer.add(buttons[i]);
		}
		
		this.add(buttonsContainer);
		this.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// based on clicked button update outHousePackages / outWarehousePackages
		System.out.println("Button clicked");
		for(int i=0;i<8;i++) {
			if(i>=0 && i<=3) { // outHouse
				outHousePackages[i] = e.getSource() == buttons[i];
			} else { // outWarehouse
				outWarehousePackages[i-4] = e.getSource() == buttons[i];
			}
		}
	}
}
