package deliveryDrone;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
public class ManualMenuPanel extends JPanel implements ActionListener {

	// GLOBAL
	private static final int NUM_OF_HOUSES = 4;

	private JToggleButton houseBtn;
	private JToggleButton warehouseBtn;
	private JButton addBtn;
	private JButton randomAddBtn;
	private JToggleButton priorityOnBtn;
	private JToggleButton priorityOffBtn;
	private JToggleButton windsOnBtn;
	private JToggleButton windsOffBtn;
	private JLabel mainHeader;
	private JLabel priorityHeader;
	private JLabel windsHeader;
	private JComboBox<String> selectionList;
	private DefaultComboBoxModel<String> houseList;
	private DefaultComboBoxModel<String> warehouseList;

	private int selection = 1;
	private boolean isPriorityMode = false;
	private boolean isWindsMode = false;

	// listeners
	private CreationListener creationListener;

	private boolean priorityFeature;
	private boolean windsFeature;

	public ManualMenuPanel(boolean priorityFeature,boolean windsFeature) {
		this.priorityFeature = priorityFeature;
		this.windsFeature = windsFeature;
		String[] houseRequests = generateHouseRequests();
		String[] warehouseRequests = generateWarehouseRequests();
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		setPreferredSize(new Dimension(250, 380));

		ActionListener buttonListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton target = (JButton) e.getSource();
				if (target == addBtn) {
					// fire up request based on selection
					creationListener.addRequest(selection);
					// reset selection list
					selectionList.setSelectedIndex(0);
				} else if (target == randomAddBtn) {
					// randomize a number between 1-12
					int randomInt = ThreadLocalRandom.current().nextInt(1, 13);
					creationListener.addRequest(randomInt);
				}
			}
		};

		// create manual menu elements
		this.mainHeader = new JLabel("Add an outgoing package request");
		gbc.gridwidth = 2;
		add(this.mainHeader, gbc);

		// toggle btns
		this.houseBtn = new JToggleButton("From House");
		this.houseBtn.addActionListener(this);
		this.houseBtn.setSelected(true);
		this.warehouseBtn = new JToggleButton("From Warehouse");
		this.warehouseBtn.addActionListener(this);
		ButtonGroup btnGrp = new ButtonGroup();
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.insets = new Insets(20, 0, 0, 0);
		btnGrp.add(houseBtn);
		btnGrp.add(warehouseBtn);
		add(houseBtn, gbc);
		gbc.gridx++;
		add(warehouseBtn, gbc);
		gbc.gridx = 0;
		gbc.gridy++;

		// selection list & comboBox
		this.selectionList = new JComboBox<String>(houseRequests);
		this.houseList = new DefaultComboBoxModel<String>(houseRequests);
		this.warehouseList = new DefaultComboBoxModel<String>(warehouseRequests);
		ActionListener comboBoxListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = (String) selectionList.getSelectedItem();
				// cases are aligned with pickUpThisState from specification
				// Notice that this structure for the event handler is FIXED with the number of
				// houses
				switch (str) {
				case "Send package from house 1":
					selection = 1;
					break;
				case "Send package from house 2":
					selection = 2;
					break;
				case "Send package from house 3":
					selection = 3;
					break;
				case "Send package from house 4":
					selection = 4;
					break;
				case "Send package to house 1":
					selection = 5;
					break;
				case "Send package to house 2":
					selection = 6;
					break;
				case "Send package to house 3":
					selection = 7;
					break;
				case "Send package to house 4":
					selection = 8;
					break;
				case "Send envelope from house 1":
					selection = 9;
					break;
				case "Send envelope from house 2":
					selection = 10;
					break;
				case "Send envelope from house 3":
					selection = 11;
					break;
				case "Send envelope from house 4":
					selection = 12;
					break;
				}
			}
		};

		selectionList.addActionListener(comboBoxListener);
		gbc.gridwidth = 2;
		add(this.selectionList, gbc);
		gbc.gridy++;

		// add btn
		this.addBtn = new JButton("Add request");
		this.addBtn.addActionListener(buttonListener);
		add(this.addBtn, gbc);
		gbc.gridy++;
		
		//
		this.randomAddBtn = new JButton("Randomize a Request");
		this.randomAddBtn.addActionListener(buttonListener);
		add(this.randomAddBtn,gbc);
		gbc.gridy++;

		// priority & winds btn		
		// toggle winds
		this.windsHeader = new JLabel("Toggle Winds");
		add(this.windsHeader, gbc);

		this.windsOnBtn = new JToggleButton("ON");
		this.windsOnBtn.addActionListener(this);
		this.windsOffBtn = new JToggleButton("OFF");
		this.windsOffBtn.addActionListener(this);
		this.windsOffBtn.setSelected(true);
		ButtonGroup windsGrp = new ButtonGroup();
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.insets = new Insets(20, 0, 0, 0);
		windsGrp.add(windsOnBtn);
		windsGrp.add(windsOffBtn);
		add(windsOnBtn, gbc);
		gbc.gridx++;
		add(windsOffBtn, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		
		// toggle priority
		this.priorityHeader = new JLabel("Toggle Priority Mode");
		add(this.priorityHeader, gbc);

		this.priorityOnBtn = new JToggleButton("ON");
		this.priorityOnBtn.addActionListener(this);
		this.priorityOffBtn = new JToggleButton("OFF");
		this.priorityOffBtn.addActionListener(this);
		this.priorityOffBtn.setSelected(true);
		ButtonGroup priorityGrp = new ButtonGroup();
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.insets = new Insets(20, 0, 0, 0);
		priorityGrp.add(priorityOnBtn);
		priorityGrp.add(priorityOffBtn);
		add(priorityOnBtn, gbc);
		gbc.gridx++;
		add(priorityOffBtn, gbc);
		gbc.gridx = 0;
		gbc.gridy++;

		if(!priorityFeature) {
			this.priorityOnBtn.setEnabled(false);
			this.priorityOffBtn.setEnabled(false);
		}
		if(!windsFeature) {
			this.windsOnBtn.setEnabled(false);
			this.windsOffBtn.setEnabled(false);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JToggleButton target = (JToggleButton) e.getSource();
		if (target == houseBtn) {
			this.selectionList.setModel(this.houseList);
			this.selectionList.setSelectedIndex(0);
		} else if (target == warehouseBtn) {
			this.selectionList.setModel(this.warehouseList);
			this.selectionList.setSelectedIndex(0);
		} else if (target == priorityOnBtn) {
			isPriorityMode = true;
			creationListener.togglePriority(isPriorityMode);
		} else if (target == priorityOffBtn){
			isPriorityMode = false;
			creationListener.togglePriority(isPriorityMode);
		} else if (target == windsOnBtn) {
			isWindsMode = true;
			creationListener.toggleWinds(isWindsMode);
		} else if (target == windsOffBtn) {
			isWindsMode = false;
			creationListener.toggleWinds(isWindsMode);
		}
	}

	public void setCreationListener(CreationListener creationListener) {
		this.creationListener = creationListener;
	}

	private String[] generateWarehouseRequests() {
		String[] res = new String[NUM_OF_HOUSES];
		for (int i = 0; i < NUM_OF_HOUSES; i++) {
			res[i] = "Send package to house " + (i + 1);
		}
		return res;
	}

	private String[] generateHouseRequests() {
		String[] res = new String[NUM_OF_HOUSES*2];
		for (int i = 0; i < NUM_OF_HOUSES; i++) {
			res[i] = "Send package from house " + (i + 1);
		}
		for (int i = 0; i < NUM_OF_HOUSES; i++) {
			res[i+4] = "Send envelope from house " + (i + 1);
		}
		return res;
	}

	public void setButtonsEnabled(boolean enabled) {
		this.houseBtn.setEnabled(enabled);
		this.warehouseBtn.setEnabled(enabled);
		this.selectionList.setEnabled(enabled);
		this.addBtn.setEnabled(enabled);
		this.randomAddBtn.setEnabled(enabled);
		if(priorityFeature) {
			this.priorityOnBtn.setEnabled(enabled);
			this.priorityOffBtn.setEnabled(enabled);			
		}
		if(windsFeature) {
			this.windsOnBtn.setEnabled(enabled);
			this.windsOffBtn.setEnabled(enabled);			
		}
	}

	public void updateModeButtons(boolean priorityMode, boolean windsMode) {
		this.isPriorityMode = priorityMode;
		this.isWindsMode = windsMode;
		if(priorityMode) {
			this.priorityOnBtn.setSelected(true);
		} else {
			this.priorityOffBtn.setSelected(true);
		}
		if(windsMode) {
			this.windsOnBtn.setSelected(true);
		} else {
			this.windsOffBtn.setSelected(true);
		}
	}

}
