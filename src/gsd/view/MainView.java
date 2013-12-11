package gsd.view;

import gsd.model.Doctors;
import gsd.model.Images;
import gsd.persistance.DBAdapter;
import gsd.service.GazeService;
import gsd.service.LocationService;
import gsd.service.obeserver.CollaborationEvent;
import gsd.service.obeserver.CollaborationEventType;
import gsd.service.obeserver.DoctorEvent;
import gsd.service.obeserver.DoctorEventType;
import gsd.service.obeserver.IObserver;
import gsd.service.obeserver.ObserverEvent;
import gsd.settings.Settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 * The main view of the application.s
 */
public class MainView extends JFrame implements IObserver {
	private static final long serialVersionUID = 4639564387761311692L;
	private CustomContentPane _contentPane;
	private DoctorsView _doctorsView;
	private ImagesView _imageView;
	private Doctors _doctors = null;
	private Color _background = new Color(103, 103, 103);
	private Images _images = null;
	private LocationService _locationService;
	private GazeService _gazeService;
	private CustomGlassPane _customGlassPane;

	/**
	 * Create the frame.
	 */
	public MainView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 768);
		// this.setResizable(false);
		Settings.getInstance().setMainFrameInstance(this);
		// set up and start the location service
		_locationService = new LocationService();
		Thread locationServiceThread = new Thread(_locationService);
		locationServiceThread.start();
		// add observer for Login/Logout events
		_locationService.addObserver(this);
		// create content pane
		_contentPane = new CustomContentPane();
		setContentPane(_contentPane);
		repaint();
	}

	// testing purposes
	public GazeService getGazeService() {
		return _gazeService;
	}

	/**
	 * invoked when an observed class emits an event
	 */
	@Override
	public void onNotify(ObserverEvent event) {
		// new Login/logout event happened.
		// checking if class type is correct
		if (event instanceof DoctorEvent) {
			DoctorEvent docEvent = (DoctorEvent) event;
			if (docEvent.type() == DoctorEventType.LOGIN) {
				onLogin(docEvent);
			} else if (docEvent.type() == DoctorEventType.LOGOUT) {
				onLogout(docEvent);
			}
		} else if (event instanceof CollaborationEvent) {
			CollaborationEvent colEvent = (CollaborationEvent) event;
			if (colEvent.type() == CollaborationEventType.IMAGE)
				onImageSelect(colEvent);
			else if (colEvent.type() == CollaborationEventType.DOCTOR)
				onDoctorSelect(colEvent);
		}
	}

	/*
	 * called when logging in
	 */
	private void onLogin(DoctorEvent docEvent) {
		_contentPane.setLogin(true);
		// create and set the glasspane
		_customGlassPane = new CustomGlassPane();
		this.setGlassPane(_customGlassPane);
		this.getGlassPane().setVisible(true);

		// create the list of doctors
		_doctorsView = new DoctorsView(_gazeService);
		_doctorsView.setFixedCellHeight(30);
		_doctorsView.setFixedCellWidth(100);
		_doctorsView.setBackground(_background);
		_doctorsView.setPreferredSize(new Dimension(100, this.getHeight()));
		// get the doctors
		_doctors = _locationService.getDoctors();

		// setting the model of the doctorsView
		_doctorsView.setModel(_doctors);

		// adding doctors list to ContentPane
		_contentPane.setLayout(new BorderLayout(10, 10));
		_contentPane.setBackground(_background);
		_contentPane.add(_doctorsView, BorderLayout.WEST);

		// creating imageview & adding it to the ContentPane
		// get the images
		_images = DBAdapter.getInstance().getImages();

		// setup the image view
		_imageView = new ImagesView(_images, 250, 250, 10);
		_imageView.setSize(300, 200);
		_imageView.setVisible(true);
		_imageView.setPreferredSize(new Dimension(this.getWidth() - 100, this
				.getHeight()));
		_contentPane.add(_imageView, BorderLayout.CENTER);

		// let doctorsView observe imageview for collaboration events and
		// visa versa
		_imageView.addObserver(_doctorsView);
		_imageView.addObserver(_customGlassPane);

		// Listen for image select
		_imageView.addObserver(this);
		_doctorsView.addObserver(this);

		_doctorsView.addObserver(_imageView);
		_doctorsView.addObserver(_customGlassPane);

		this.pack();
	}

	/**
	 * called when logging out.
	 */
	private void onLogout(DoctorEvent docEvent) {
		_contentPane.setLogin(false);
		_doctors = new Doctors();
		// Remove UI elements
		remove(_imageView);
		remove(_doctorsView);
		remove(_customGlassPane);
		// Release objects
		_imageView = null;
		_doctorsView = null;
		_customGlassPane = null;
		this.repaint();
		if (_gazeService != null) {
			_gazeService.stop();
			_gazeService = null;
		}
	}

	/**
	 * Start listening and sending gaze events
	 * 
	 * @param colEvent
	 */
	private void onImageSelect(CollaborationEvent colEvent) {
		_gazeService = new GazeService();

		// Add observers
		_gazeService.addObserver(_doctors);
		_gazeService.addObserver(_customGlassPane);
		_gazeService.addObserver(_imageView);

		Thread gazeServiceThread = new Thread(_gazeService);
		gazeServiceThread.start();

		try {
			Thread.sleep(1000);
		} catch (Exception e) {

		}

		_gazeService.setDoctorId(_locationService.getDoctor().getId());
		_gazeService.setImageId(colEvent.getImageId());
		_gazeService.listenForGazeEvents(-1, colEvent.getImageId());
	}

	/**
	 * called when a doctor is selected.
	 */
	private void onDoctorSelect(CollaborationEvent colEvent) {
		_gazeService = new GazeService();

		// Add observers
		_gazeService.addObserver(_doctors);
		_gazeService.addObserver(_customGlassPane);
		_gazeService.addObserver(_imageView);

		Thread gazeServiceThread = new Thread(_gazeService);
		gazeServiceThread.start();

		try {
			Thread.sleep(1000);
		} catch (Exception e) {

		}

		_gazeService.setDoctorId(_locationService.getDoctor().getId());
		_gazeService.listenForGazeEvents(colEvent.getDoctor().getId(), -1);
	}
}
