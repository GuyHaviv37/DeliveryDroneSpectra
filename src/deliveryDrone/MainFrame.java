package deliveryDrone;
import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private GridPanel gridPanel;
	private MenuPanel menuPanel;

	public MainFrame(String appName,boolean priorityFeature,boolean windsFeature,boolean energyFeature) {
		super(appName);
		this.gridPanel = new GridPanel(this,priorityFeature,windsFeature,energyFeature);
		this.menuPanel = new MenuPanel(priorityFeature,windsFeature);
		setLayout(new BorderLayout());
		
		// Implement a CreationListener: connection between the menus and GridPanel
		this.menuPanel.setCreationListener(new CreationListener() {
			@Override
			public void addRequest(int requestNumber) {
				gridPanel.addPickupRequest(requestNumber);
			}

			@Override
			public void togglePriority(boolean newPriority) {
				gridPanel.togglePriority(newPriority);
			}

			@Override
			public void toggleWinds(boolean newWinds) {
				gridPanel.toggleWinds(newWinds);
			}
			
			@Override
			public void toggleDemo(boolean isDemo) {
				gridPanel.toggleDemo(isDemo);
			}

			@Override
			public void createScenario(int scenarioNumber) {
				if(scenarioNumber != 0) gridPanel.createScenario(scenarioNumber);
			}

		});

		add(this.gridPanel, BorderLayout.WEST);
		add(this.menuPanel, BorderLayout.EAST);
		
		ImageIcon image = new ImageIcon("img/drone_icon.png");
		this.setIconImage(image.getImage());
		
		setSize(900, 700);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void updateButtonsEnabled(boolean b) {
		this.menuPanel.updateButtonsEnabled(b);
	}

	public void enableDemoBtn(boolean b) {
		this.menuPanel.enableDemoBtn(b);
	}
	
	public void enableRunScenarioBtn(boolean b,String text) {
		this.menuPanel.enableRunScenarioBtn(b,text);
	}

	public void updateModeButtons(boolean priorityMode, boolean windsMode) {
		this.menuPanel.updateModeButtons(priorityMode,windsMode);
	}
}
