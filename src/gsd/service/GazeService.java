package gsd.service;

import gsd.service.obeserver.GazeEvent;
import gsd.service.obeserver.IObservable;
import gsd.service.obeserver.IObserver;
import gsd.settings.Settings;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dk.itu.infobus.ws.EventBuilder;
import dk.itu.infobus.ws.EventBus;
import dk.itu.infobus.ws.Generator;
import dk.itu.infobus.ws.GeneratorAdapter;
import dk.itu.infobus.ws.Listener;
import dk.itu.infobus.ws.PatternBuilder;
import dk.itu.infobus.ws.PatternOperator;

/**
 * Use this class to listen for GenieHub events from a Gaze Tracker and
 * transform them before re-emitting them. The events should contain the
 * following fields: {type="gazeevent", x, y}
 * 
 * NOTE: EventBus and GenieHub are the same thing. The framework we work with
 * use EventBus while we use GenieHub
 */
public class GazeService implements Runnable, IObservable {
	// fields
	// GenieHub address
	private String ebAddress;
	// GenieHub port
	private int ebPort;
	// GenieHub instance
	private EventBus eb;
	// Listener for Gaze Events
	private Listener localGazeEventListener;
	// Generator for transformed Gaze Events
	private Generator gazeEventGenerator;
	// The ID of the doctor currently logged in
	private int doctorId = -1;
	// The ID of the image being looked at
	private int imageId = -1;
	// List of observers
	private List<IObserver> observers;

	/**
	 * Constructor
	 * 
	 * @param address
	 *            of GenieHub instance
	 * @param port
	 *            of GenieHub instance
	 */
	public GazeService() {
		this.ebAddress = Settings.getInstance().getEbAddress();
		this.ebPort = Settings.getInstance().getEbPort();
		observers = new ArrayList<IObserver>();
	}

	/**
	 * Run the service in its own thread using java.lang.Thread
	 */
	public void run() {
		try {
			// Start
			eb = new EventBus(ebAddress, ebPort);
			eb.start();
			// Add listener for local gaze events
			localGazeEventListener = new LocalGazeListener();
			eb.addListener(localGazeEventListener);
			// Add Generator
			gazeEventGenerator = new GeneratorAdapter("GazeGenerator", "id",
					"timestamp", "type", "doctorid", "imageid", "x", "y");
			eb.addGenerator(gazeEventGenerator);
		} catch (Exception e) {
			System.out.println("Eventbus error in Listener: "
					+ e.getStackTrace());
		}
	}

	/**
	 * Stop the service.
	 */
	public void stop() {
		if (eb != null) {
			try {
				eb.stop();
			} catch (Exception e) {

			}
		}
	}

	/**
	 * Start listening for gazeevents from either a doctor or an image
	 * 
	 * @param doctorId
	 * @param imageId
	 */
	public void listenForGazeEvents(int doctorId, int imageId) {
		Listener gl;
		try {
			if (imageId != -1) {
				gl = new ImageGazeListener(imageId);
				eb.addListener(gl);
			} else if (doctorId != -1) {
				gl = new DoctorGazeListener(doctorId);
				eb.addListener(gl);
			}
		} catch (Exception e) {
			// TODO: handle
		}
	}

	/**
	 * Call on logout to stop listening for gazeevents from all doctors
	 */
	public void stopListening() {

	}

	/**
	 * Called when an event has been received to re-emit it in to GenieHub
	 * Before transmitting the events, the x- and y-coordinates are transformed
	 * from absolute to relative coordinates
	 * 
	 * @param type
	 *            , event type
	 * @param x
	 *            , x-coordinate of gaze event
	 * @param y
	 *            , y-coordinate of gaze event
	 */
	//private void publish(String type, double x, double y) {
	private void publish(String type, long x, long y) {
				// Transform coordinates
		Point tp = transformPoint(new Point((int) x, (int) y));

		// only publish if the transformed point is inside the application
		// window ... == not null
		if (tp != null) {
			// Helper to build events
			EventBuilder evb = new EventBuilder();
			evb.put("id", 1).put("timestamp", System.currentTimeMillis())
					.put("type", type).put("doctorid", doctorId)
					.put("imageid", imageId).put("x", tp.x).put("y", tp.y);
			// Generate and publish event
			gazeEventGenerator.publish(evb.getEvent());
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
	 * Notify all observers of gazeevents;
	 */
	public void notifyObservers(long id, long time, long doctorid,
			long imageid, long x, long y) {
		GazeEvent evt = new GazeEvent((int) id, (int) time, (int) doctorid,
				(int) imageid, (int) x, (int) y);
		for (Iterator<IObserver> it = observers.iterator(); it.hasNext();) {
			IObserver observer = (IObserver) it.next();
			observer.onNotify(evt);
		}
	}

	/**
	 * Set which image is being looked at
	 * 
	 * @param imageId
	 */
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	/**
	 * Set local doctor
	 * 
	 * @param doctor
	 */
	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}

	/**
	 * Called when a gazeevent from a doctor/image has been received
	 * 
	 * @param msg
	 */
	private void onGazeEvent(Map<String, Object> msg) {
		if (this.doctorId != -1) {
			// If no imageid has been set, we must add a listener for the local
			// doctor aswell
			if (imageId == -1) {
				long iid = (Long) msg.get("imageid");
				imageId = (int) iid;
				listenForGazeEvents(doctorId, -1);
			}
			notifyObservers((Long) msg.get("id"), (Long) msg.get("timestamp"),
					(Long) msg.get("doctorid"), (Long) msg.get("imageid"),
					(Long) msg.get("x"), (Long) msg.get("y"));
		}
	}

	/**
	 * Transform an x- or y-coordinate
	 * 
	 * @param n
	 *            - x- or y-coordinate
	 * @return transformed coordinate
	 */
	private Point transformPoint(Point p) {
		// get screenSize and location
		Point locOnScreen = Settings.getInstance().getMainFrame()
				.getLocationOnScreen();
		Point transPoint;
		int height = Settings.getInstance().getMainFrame().getHeight();
		int width = Settings.getInstance().getMainFrame().getWidth();
		// transform screen point to application point
		// check if inside application window.
		if (p.x <= locOnScreen.x + width && p.x >= locOnScreen.x
				&& p.y <= locOnScreen.y + height && p.y >= locOnScreen.y) {
			transPoint = new Point(p.x - locOnScreen.x, p.y - locOnScreen.y);
			return transPoint;
		}
		return null;
	}

	/**
	 * Listener implementation for local gazeevents
	 */
	class LocalGazeListener extends Listener {

		public LocalGazeListener() {
			super(new PatternBuilder()
					.add("type", PatternOperator.EQ, "gazeevent")
					.add("zone", PatternOperator.EQ,
							Settings.getInstance().getLocation())
					.addMatchAll("x").addMatchAll("y").getPattern());
		}

		@Override
		public void onStarted() {
		}

		@Override
		public void onMessage(Map<String, Object> msg) {
			// On event from Gaze Tracker we re-emit the event with extra
			// information
//			publish((String) msg.get("type"), ((Double) msg.get("x")),
//					((Double) msg.get("y")));
			publish((String) msg.get("type"), ((Long) msg.get("x")),
					((Long) msg.get("y")));

		}

		@Override
		public void cleanUp() throws Exception {
		}
	}

	/**
	 * Listeners for gazeevents from doctor or image
	 */
	class DoctorGazeListener extends Listener {
		public DoctorGazeListener(int doctorId) {
			super(new PatternBuilder().addMatchAll("timestamp")
					.addMatchAll("id")
					.add("doctorid", PatternOperator.EQ, doctorId)
					.add("type", PatternOperator.EQ, "gazeevent")
					.addMatchAll("imageid").addMatchAll("x").addMatchAll("y")
					.getPattern());
		}

		@Override
		public void onStarted() {
		}

		@Override
		public void onMessage(Map<String, Object> msg) {
			onGazeEvent(msg);
		}

		@Override
		public void cleanUp() throws Exception {
		}
	}

	class ImageGazeListener extends Listener {
		public ImageGazeListener(int imageId) {
			super(new PatternBuilder().addMatchAll("timestamp")
					.addMatchAll("id").addMatchAll("doctorid")
					.add("type", PatternOperator.EQ, "gazeevent")
					.add("imageid", PatternOperator.EQ, imageId)
					.addMatchAll("x").addMatchAll("y").getPattern());
		}

		@Override
		public void onStarted() {
		}

		@Override
		public void onMessage(Map<String, Object> msg) {
			onGazeEvent(msg);
		}

		@Override
		public void cleanUp() throws Exception {
		}
	}
}
