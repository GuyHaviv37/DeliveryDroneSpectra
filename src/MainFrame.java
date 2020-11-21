import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MainFrame extends JFrame{
	private GridPanel gridPanel;
	private MenuPanel menuPanel;
	
	public MainFrame(String appName) {
		super(appName);
		// add image icon
		setLayout(new BorderLayout());
		//passing 'this' as parent element of the grid panel
		this.gridPanel = new GridPanel(this);
		this.menuPanel = new MenuPanel();
		
		this.menuPanel.setCreationListener(new CreationListener() {

			@Override
			public void addRequest(int requestNumber) {
				gridPanel.addPickupRequest(requestNumber);
			}
			
			// add scenario
			// add other environment variables such as winds or rain.
			
		});
		
		add(this.gridPanel,BorderLayout.WEST);
		add(this.menuPanel,BorderLayout.EAST);
		
		// size could get parameterized
		// grid cells are 145x145 of 4x4 grid
		// drone size is 125x125
		setSize(894,617);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		// junction project added up cleanup of gridPanel.
	}
}
