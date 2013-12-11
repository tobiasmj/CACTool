package gsd.view;

import gsd.model.Doctor;
import gsd.service.GazeService;
import gsd.service.obeserver.CollaborationEvent;
import gsd.service.obeserver.CollaborationEventType;
import gsd.service.obeserver.IObservable;
import gsd.service.obeserver.IObserver;
import gsd.service.obeserver.ObserverEvent;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JList;

/**
 * View of the doctors in the interface.
 */
public class DoctorsView extends JList implements MouseListener, IObservable,
		IObserver {

	private static final long serialVersionUID = 1172010157587335350L;
	// fields
	private DoctorsRenderer _docRenderer;
	// List of observers
	private List<IObserver> observers;

	/**
	 * constructor
	 */
	public DoctorsView(GazeService gs) {
		// create & set the renderer for doctor list
		_docRenderer = new DoctorsRenderer(5);
		observers = new ArrayList<IObserver>();
		this.setCellRenderer(_docRenderer);
		this.addMouseListener(this);
	}

	/**
	 * Handling mouse events
	 */
	@Override
	public void mouseClicked(MouseEvent me) {
		if (me.getClickCount() == 2) {
			// set the selected index
			_docRenderer.setSelectedIndex(this.locationToIndex(me.getPoint()));
			// notify observers of collaboration event
			notifyObservers(this.locationToIndex(me.getPoint()));
			this.repaint();

			// sending a test GazeEvent
			// gs.fireTestNotification();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	/**
	 * Observer handling
	 */
	@Override
	public void addObserver(IObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(IObserver observer) {
		observers.remove(observer);
	}

	/**
	 * Notify all observers of collaborationEvents;
	 */
	public void notifyObservers(int docId) {
		Doctor doc = (Doctor) this.getModel().getElementAt(docId);

		CollaborationEvent collabEvent = new CollaborationEvent(
				CollaborationEventType.DOCTOR, doc, -1);
		for (Iterator<IObserver> it = observers.iterator(); it.hasNext();) {
			IObserver observer = (IObserver) it.next();
			observer.onNotify(collabEvent);
		}
	}

	/**
	 * invoked when a observed object sends a message
	 */
	@Override
	public void onNotify(ObserverEvent event) {
		if (event instanceof CollaborationEvent) {
			// cast first
			CollaborationEvent collabEvent = (CollaborationEvent) event;
			// switch on type of event
			switch (collabEvent.type()) {
			case IMAGE:
				_docRenderer.setSingleViewImageId(collabEvent.getImageId());
				break;
			case END:
				// reset the doc renderer
				_docRenderer.setSingleViewImageId(-1);
				break;
			}
			this.repaint();
		}
	}
}