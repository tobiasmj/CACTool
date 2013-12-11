package gsd;

import gsd.view.MainView;
import java.awt.EventQueue;

/**
 * main class - starting point
 */
public class MainController {
	// fields
	private MainView _mainView = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@SuppressWarnings("unused")
			public void run() {
				try {
					MainController main = new MainController();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainController() {
		// setup MainView
		_mainView = new MainView();
		_mainView.setVisible(true);
	}
}
