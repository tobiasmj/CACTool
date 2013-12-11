package gsd.service;

import pku.edu.dataAccess.Doctor;
import gsd.model.Doctors;
import gsd.persistance.InformationStorage;
import gsd.persistance.InformationStorageFactory;
import gsd.service.obeserver.DoctorEvent;
import gsd.service.obeserver.DoctorEventType;
import gsd.service.obeserver.IObservable;
import gsd.service.obeserver.IObserver;
import gsd.settings.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dk.itu.infobus.ws.EventBus;
import dk.itu.infobus.ws.Listener;
import dk.itu.infobus.ws.PatternBuilder;
import dk.itu.infobus.ws.PatternOperator;

/**
 * Use this class to listen to for location events from the BLIP system to know
 * when a doctor logs in
 */
public class LocationService implements Runnable, IObservable {
	// fields
	// The location of the instance of the client. Needed to know when a doctor
	// arrives at this specific location
	private String location;
	// List of online doctors
	private Doctors doctors;
	// Currently logged in doctor
	private Doctor doctor;
	// Id there a logged in doctor
	private boolean loggedIn;
	// Access to database
	private InformationStorage dbAdapter;
	// GenieHub address
	private String ebAddress;
	// GenieHub port
	private int ebPort;
	// GenieHub instance
	private EventBus eb;
	// List of observers
	private List<IObserver> observers;

	/**
	 * Constructor. Set location and start listening for logins
	 * 
	 * @param location
	 */
	public LocationService() {
		Settings settings = Settings.getInstance();
		this.location = settings.getLocation();
		this.ebAddress = settings.getEbAddress();
		this.ebPort = settings.getEbPort();
		loggedIn = false;
		observers = new ArrayList<IObserver>();
		dbAdapter = InformationStorageFactory.getStorageAdapter();
	}

	/**
	 * Run the service in its own thread using java.lang.Thread
	 */
	@Override
	public void run() {
		try {
			eb = new EventBus(ebAddress, ebPort);
			eb.start();
			// Add listener to listen for logins
			LoginListener loginListener = new LoginListener(location);
			eb.addListener(loginListener);
		} catch (Exception e) {
			// TODO: handle
		}

	}

	/**
	 * Get online Doctors
	 * 
	 * @return Doctors
	 */
	public Doctors getDoctors() {
		if (doctors == null) {
			doctors = new Doctors();
			return doctors;
		} else {
			return doctors;
		}
	}

	/**
	 * Get the currently logged in doctor
	 * 
	 * @return
	 */
	public Doctor getDoctor() {
		return doctor;
	}

	/**
	 * set the currently logged in doctor
	 * 
	 * @param doctor
	 */
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	/**
	 * Called when a device is detected in the zone of this instance
	 * 
	 * @param btMac
	 *            bluetooth mac-address detected in this zone
	 */
	private void deviceDetected(String btMac) {
		// We only allow one doctor to be logged in at the same place
		if (doctor == null) {
			// Check db to see if it is a doctor if so log in
			Doctor d = dbAdapter.getDoctor(btMac);
			if (d != null) {
				// Successful login
				this.doctor = d;
				this.doctor.setOnline(1);
				doLogin();
			}
		}
	}

	/**
	 * Called when a doctor has logged in (entered the zone)
	 */
	private void doLogin() {
		loggedIn = true;
		doctor.setOnline(1);
		// Set doctor as logged in
		dbAdapter.updateDoctor(doctor);
		// Get a list of currently logged in doctors
		doctors = dbAdapter.getDoctors(this.doctor);
		// Add listeners to keep the list updated
		try {
			eb.addListener(new LogoutListener(this.doctor.getMac(),
					this.location));
			eb.addListener(new DeviceDetectedListener());
			eb.addListener(new DevicedMovedListener());
		} catch (IOException e) {
			// TODO: Handle
		}
		notifyObservers();
	}

	/**
	 * Called when a doctor has logged out (left the zone)
	 */
	private void doLogout() {
		doctor.setOnline(0);
		// Set doctor as logged out
		dbAdapter.updateDoctor(doctor);
		// Release objects and notify observers
		loggedIn = false;
		notifyObservers();
		doctors = null;
		doctor = null;
	}

	/**
	 * Called when a device is detected somewhere Use this method to keep the
	 * list of online doctors updated
	 * 
	 * @param btMac
	 * @param location
	 */
	private void deviceDetected(String btMac, String location) {
		// If it is not this location
		if (location != this.location) {
			Doctor doctor = dbAdapter.getDoctor(btMac);
			if (doctor != null) {
				doctors.addDoctor(doctor);
			}
		}
	}

	/**
	 * 
	 * @param btMac
	 * @param location
	 */
	private void devicedLeft(String btMac, String location) {
		// If it is not this location
		if (location != this.location) {
			// Check if it is indeed a registered doctor
			Doctor doctor = dbAdapter.getDoctor(btMac);
			if (doctor != null) {
				doctors.removeDoctor(doctor);
			}
		}
	}

	/**
	 * Add an observer
	 */
	@Override
	public void addObserver(IObserver observer) {
		observers.add(observer);
	}

	/**
	 * Remove an observer
	 */
	@Override
	public void removeObserver(IObserver observer) {
		observers.remove(observer);
	}

	/**
	 * Notify all observers of doctor login / logout;
	 */
	public void notifyObservers() {
		DoctorEvent evt;
		if (loggedIn) {
			evt = new DoctorEvent(DoctorEventType.LOGIN, doctor);
		} else {
			evt = new DoctorEvent(DoctorEventType.LOGOUT, doctor);
		}
		for (Iterator<IObserver> it = observers.iterator(); it.hasNext();) {
			IObserver observer = (IObserver) it.next();
			observer.onNotify(evt);
		}
	}

	/************************************************************
	 * GenieHub listener implementations
	 ************************************************************/
	/**
	 * Listen for logins at this specific location
	 */
	class LoginListener extends Listener {

		public LoginListener(String location) {
			super(new PatternBuilder().addMatchAll("terminal.btmac")
					.add("type", PatternOperator.EQ, "device.detected")
					.add("zone.current", PatternOperator.EQ, location)
					.getPattern());
		}

		@Override
		public void onStarted() {
		}

		@Override
		public void onMessage(Map<String, Object> msg) {
			deviceDetected((String) msg.get("terminal.btmac"));
		}

		@Override
		public void cleanUp() throws Exception {
		}
	}

	/**
	 * Listens for logout of a specific mac address at a specific location
	 */
	class LogoutListener extends Listener {

		public LogoutListener(String btMac, String location) {
			super(new PatternBuilder()
					.add("terminal.btmac", PatternOperator.EQ, btMac)
					.add("type", PatternOperator.EQ, "device.moved")
					.add("zone.previous", PatternOperator.EQ, location)
					.getPattern());
		}

		@Override
		public void onStarted() {
		}

		@Override
		public void onMessage(Map<String, Object> msg) {
			doLogout();
		}

		@Override
		public void cleanUp() throws Exception {
		}

	}

	/**
	 * Listen for newly detected bluetooth devices
	 */
	class DeviceDetectedListener extends Listener {

		public DeviceDetectedListener() {
			super(new PatternBuilder().addMatchAll("terminal.btmac")
					.add("type", PatternOperator.EQ, "device.detected")
					.addMatchAll("zone.current").getPattern());
		}

		@Override
		public void onStarted() {
		}

		@Override
		public void onMessage(Map<String, Object> msg) {
			deviceDetected((String) msg.get("terminal.btmac"),
					(String) msg.get("zone.current"));

		}

		@Override
		public void cleanUp() throws Exception {
		}
	}

	/**
	 * Listens for moved events
	 */
	class DevicedMovedListener extends Listener {

		public DevicedMovedListener() {
			super(new PatternBuilder().addMatchAll("terminal.btmac")
					.add("type", PatternOperator.EQ, "device.moved")
					.addMatchAll("zone.previous").getPattern());
		}

		@Override
		public void onStarted() {
		}

		@Override
		public void onMessage(Map<String, Object> msg) {
			devicedLeft((String) msg.get("terminal.btmac"),
					(String) msg.get("zone.previous"));
		}

		@Override
		public void cleanUp() throws Exception {
		}

	}
}
