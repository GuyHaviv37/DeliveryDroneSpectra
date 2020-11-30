import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class ManualMenuPanel extends JPanel implements ActionListener{

	//GLOBAL
	private static final int NUM_OF_HOUSES = 4;
	
	private JToggleButton houseBtn;
	private JToggleButton warehouseBtn;
	private JButton addBtn;
	private JToggleButton priorityOnBtn;
	private JToggleButton priorityOffBtn;
	private JToggleButton windsOnBtn;
	private JToggleButton windsOffBtn;
//	private JButton priorityBtn;
//	private JButton windsBtn;
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
	private MenuListener menuListener;
	private CreationListener creationListener;
	
	public ManualMenuPanel() {
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
		
		setPreferredSize(new Dimension(250,320));
		
		ActionListener buttonListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton target = (JButton)e.getSource();
				if(target == addBtn) {
					// fire up request based on selection
					creationListener.addRequest(selection);
					//reset selection list
					selectionList.setSelectedIndex(0);
				} 
			}			
		};
		
		// create manual menu elements
		this.mainHeader = new JLabel("Add an outgoing package request");
		gbc.gridwidth = 2;
		add(this.mainHeader,gbc);
		
		// toggle btns
		this.houseBtn = new JToggleButton("From House");
		this.houseBtn.addActionListener(this);
		this.houseBtn.setSelected(true);
		this.warehouseBtn = new JToggleButton("From Warehouse");
		this.warehouseBtn.addActionListener(this);
		ButtonGroup btnGrp = new ButtonGroup();
		gbc.gridwidth=1;
		gbc.gridy++;
		gbc.insets = new Insets(20,0,0,0);
		btnGrp.add(houseBtn);
		btnGrp.add(warehouseBtn);
		add(houseBtn,gbc);
		gbc.gridx++;
		add(warehouseBtn,gbc);
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
				// Notice that this structure for the event handler is FIXED with the number of houses
				switch(str) {
					case "Send from house 1":
						selection = 1;
						break;
					case "Send from house 2":
						selection = 2;
						break;
					case "Send from house 3":
						selection = 3;
						break;
					case "Send from house 4":
						selection = 4;
						break;
					case "Send to house 1":
						selection = 5;
						break;
					case "Send to house 2":
						selection = 6;
						break;
					case "Send to house 3":
						selection = 7;
						break;
					case "Send to house 4":
						selection = 8;
						break;
				}
			}
		};
		
		selectionList.addActionListener(comboBoxListener);
		gbc.gridwidth = 2;
		add(this.selectionList,gbc);
		gbc.gridy++;
		
		//add btn
		this.addBtn = new JButton("Add request");
		this.addBtn.addActionListener(buttonListener);
		add(this.addBtn,gbc);
		gbc.gridy++;
		
		//priority & winds btn
		this.priorityHeader = new JLabel("Toggle Priority Mode");
		add(this.priorityHeader,gbc);
		
		// toggle priority
		this.priorityOnBtn = new JToggleButton("ON");
		this.priorityOnBtn.addActionListener(this);
		this.priorityOffBtn = new JToggleButton("OFF");
		this.priorityOffBtn.addActionListener(this);
		this.priorityOffBtn.setSelected(true);
		ButtonGroup priorityGrp = new ButtonGroup();
		gbc.gridwidth=1;
		gbc.gridy++;
		gbc.insets = new Insets(20,0,0,0);
		priorityGrp.add(priorityOnBtn);
		priorityGrp.add(priorityOffBtn);
		add(priorityOnBtn,gbc);
		gbc.gridx++;
		add(priorityOffBtn,gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		
		this.windsHeader = new JLabel("Toggle Winds");
		add(this.windsHeader,gbc);
		
		// toggle winds
		this.windsOnBtn = new JToggleButton("ON");
		this.windsOnBtn.addActionListener(this);
		this.windsOffBtn = new JToggleButton("OFF");
		this.windsOffBtn.addActionListener(this);
		this.windsOffBtn.setSelected(true);
		ButtonGroup windsGrp = new ButtonGroup();
		gbc.gridwidth=1;
		gbc.gridy++;
		gbc.insets = new Insets(20,0,0,0);
		windsGrp.add(windsOnBtn);
		windsGrp.add(windsOffBtn);
		add(windsOnBtn,gbc);
		gbc.gridx++;
		add(windsOffBtn,gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		JToggleButton target = (JToggleButton) e.getSource();
		if(target == houseBtn) {
			this.selectionList.setModel(this.houseList);
			this.selectionList.setSelectedIndex(0);
		} else if (target == warehouseBtn){
			this.selectionList.setModel(this.warehouseList);
			this.selectionList.setSelectedIndex(0);			
		} else if (target == priorityOnBtn || target == priorityOffBtn) {
			isPriorityMode = !isPriorityMode;
			creationListener.togglePriority(isPriorityMode);
		}
		else if (target == windsOnBtn || target == windsOffBtn) {
			isWindsMode = !isWindsMode;
			creationListener.toggleWinds(isWindsMode);
		}
		// add listener for winds toggle.
	}
	
	public void setMenuListener(MenuListener menuListener) {
		this.menuListener = menuListener;
	}

	public void setCreationListener(CreationListener creationListener) {
		this.creationListener = creationListener;
	}
	
	private String[] generateWarehouseRequests() {
		String[] res = new String[NUM_OF_HOUSES];
		for(int i=0;i<NUM_OF_HOUSES;i++) {
			res[i] = "Send to house "+(i+1);
		}
		return res;
	}

	private String[] generateHouseRequests() {
		String[] res = new String[NUM_OF_HOUSES];
		for(int i=0;i<NUM_OF_HOUSES;i++) {
			res[i] = "Send from house "+(i+1);
		}
		return res;
	}




}
