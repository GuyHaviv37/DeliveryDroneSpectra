package deliveryDrone;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class ScenarioMenuPanel extends JPanel implements ActionListener {
	
	public JComboBox<String> selectionList;
	private JButton runScenarioBtn;
	private JButton demoBtn;
	private boolean isDemo;
	private JLabel scenarioHeader;
	private JLabel demoHeader;
	private int selection = 0;
	private MenuListener menuListener;
	private CreationListener creationListener;
	
	public ScenarioMenuPanel() {
		String[] scenarioTitles = {"Choose Scenario","Scenario 1", "Scenario 2","Scenario 3"};
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		
		// create Btns
		this.runScenarioBtn = new JButton("Run Scenario");
		this.runScenarioBtn.addActionListener(this);
		this.runScenarioBtn.setEnabled(false);
		
		this.demoBtn = new JButton("Run Demo");
		this.demoBtn.addActionListener(this);
		//this.demoBtn.setToolTipText("Run automated environment");
		
		// Selection List
		this.selectionList = new JComboBox<String>(scenarioTitles);
		ActionListener comboBoxListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = (String) selectionList.getSelectedItem();
				runScenarioBtn.setEnabled(true);
				switch(str) {
					case "Choose Scenario":
						selection = 0;
						runScenarioBtn.setEnabled(false);
						break;
					case "Scenario 1":
						selection = 1;
						break;
					case "Scenario 2":
						selection = 2;
						break;
					case "Scenario 3":
						selection = 3;
						break;
				}
			}
		};
		selectionList.addActionListener(comboBoxListener);
		
		// Add Scenario section
		gbc.insets = new Insets(20,0,0,0);
		this.scenarioHeader = new JLabel("Run a Scenario");
		add(this.scenarioHeader,gbc);
		gbc.gridy++;
		add(this.selectionList,gbc);
		gbc.gridy++;
		add(this.runScenarioBtn,gbc);
		gbc.gridy++;
		
		// Add Demo section
		this.demoHeader = new JLabel("Run Randomized Demo");
		add(this.demoHeader,gbc);
		gbc.gridy++;
		add(this.demoBtn,gbc);

	}

	public void setMenuListener(MenuListener menuListener) {
		this.menuListener = menuListener;
	}

	public void setCreationListener(CreationListener creationListener) {
		this.creationListener = creationListener;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton target = (JButton) e.getSource();
		if (target == runScenarioBtn) {
			creationListener.createScenario(selection);
			menuListener.updateButtonsEnabled(false);
		} else if (target == demoBtn) {
			// toggle demoMode
			this.isDemo = !this.isDemo;
			this.demoBtn.setText((this.isDemo ? "Stop" : "Run")+" Demo");
			creationListener.toggleDemo(this.isDemo);
			menuListener.updateButtonsEnabled(!this.isDemo); // if demo runs make disabled.
		}
	}

	public void setDemoBtnEnabled(boolean isEnabled) {
		this.demoBtn.setEnabled(isEnabled);
	}

	public void setButtonsEnabled(boolean enabled) {
		this.selectionList.setEnabled(enabled);
		if(this.selectionList.getSelectedIndex() != 0) {
			this.runScenarioBtn.setEnabled(enabled);
		}
		if(!this.isDemo) {
			this.demoBtn.setEnabled(enabled);
		}
	}

}
