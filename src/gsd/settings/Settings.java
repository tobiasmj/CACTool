package gsd.settings;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JFrame;

/**
 * Settings class for holding important settings Implemented as singleton
 */
public class Settings {
	// Geniehub address
	private String ebAddress;
	// Geniehub port
	private int ebPort;
	// Vector of colors
	Vector<Color> _colors;
	// Location of this deployed instance
	private String location;
	// Singleton instance
	private static Settings _instance;

	private JFrame _mfInstance;

	/**
	 * Constructor
	 */
	private Settings() {
		ebAddress = "10.1.1.129";
		ebPort = 8000;
		location = "zone";

		_colors = new Vector<Color>();
		_colors.add(new Color(34, 139, 34));
		_colors.add(new Color(210, 105, 30));
		_colors.add(new Color(199, 21, 133));
		_colors.add(new Color(148, 0, 211));
		_colors.add(new Color(0, 0, 205));
		_colors.add(new Color(0, 139, 139));
		_colors.add(new Color(220, 20, 60));
	}

	/***************************
	 * Getters
	 ***************************/
	public static Settings getInstance() {
		if (_instance == null)
			_instance = new Settings();
		return _instance;
	}

	public String getEbAddress() {
		return ebAddress;
	}

	public int getEbPort() {
		return ebPort;
	}

	public String getLocation() {
		return location;
	}

	public Vector<Color> getColors() {
		return _colors;
	}

	public void setMainFrameInstance(JFrame jf) {
		_mfInstance = jf;
	}

	public JFrame getMainFrame() {
		return _mfInstance;
	}
}
