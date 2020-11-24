import javax.swing.JPanel;

public class ScenarioMenuPanel extends JPanel {

	private MenuListener menuListener;
	private CreationListener creationListener;

	public void setMenuListener(MenuListener menuListener) {
		this.menuListener = menuListener;
	}
	
	public void setCreationListener(CreationListener creationListener) {
		this.creationListener = creationListener;
	}

}
