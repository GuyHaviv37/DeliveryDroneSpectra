package deliveryDrone;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MenuPanel extends JPanel {

	private ManualMenuPanel manualMenu;
	private ScenarioMenuPanel scenarioMenu;
	// component styling
	private JLabel h1;

	public MenuPanel() {
		// Init GridBagLayout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		setPreferredSize(new Dimension(300, 700));
		setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));

		// build menus
		this.manualMenu = new ManualMenuPanel();
		this.scenarioMenu = new ScenarioMenuPanel();

		// manage main menu stylings
		this.h1 = new JLabel("Delievery Drone Simulator");
		this.h1.setFont(new Font("Arial", Font.BOLD, 20));
		gbc.insets = new Insets(0, 20, 0, 20);
		add(this.h1, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy++;
		add(this.manualMenu, gbc);
		gbc.gridy++;
		add(this.scenarioMenu, gbc);
		// more labels and such if needed later.

		// add event listener to all of the child menus - manual and scenario
		MenuListener menuListener = new MenuListener() {

			@Override
			public void updateButtonsEnabled(boolean enabled) {
				manualMenu.setButtonsEnabled(enabled);
				scenarioMenu.setButtonsEnabled(enabled);
			}
			
			
		};

		this.manualMenu.setMenuListener(menuListener);
		this.scenarioMenu.setMenuListener(menuListener);
	}

	public void setCreationListener(CreationListener creationListener) {
		this.manualMenu.setCreationListener(creationListener);
		this.scenarioMenu.setCreationListener(creationListener);
	}

	public void updateButtonsEnabled(boolean isEnabled) {
		if(isEnabled) {
			manualMenu.setButtonsEnabled(true);
			scenarioMenu.setButtonsEnabled(true);
		}
	}
	
	public void enableDemoBtn(boolean isEnabled) {
		scenarioMenu.setDemoBtnEnabled(isEnabled);
	}

}
