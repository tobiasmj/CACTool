package gsd.view;

import gsd.service.obeserver.CollaborationEvent;
import gsd.service.obeserver.DoctorEvent;
import gsd.service.obeserver.DoctorEventType;
import gsd.service.obeserver.GazeEvent;
import gsd.service.obeserver.IObserver;
import gsd.service.obeserver.ObserverEvent;
import gsd.settings.Settings;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;

/**
 * CustomGlassPane used for drawing crosshairs on top of the UI when
 * collaborating.
 */
public class CustomGlassPane extends JComponent implements IObserver {

	// fields
	private static final long serialVersionUID = -5908311935070790166L;
	// map for storing gaze events to be drawn on top of the single view image
	private HashMap<Integer, GazeEvent> _gazeEventsMap = new HashMap<Integer, GazeEvent>();
	private Vector<Color> _colors = Settings.getInstance().getColors();
	private int _singleImageId;

	/**
	 * overwritten method to control drawing of the content
	 */
	@Override
	public void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g.create();
		int x;
		int y, safeIndex;
		int i = 0;
		Set<Integer> keys = _gazeEventsMap.keySet();
		GazeEvent gevt;
		for (Integer key : keys) {
			// assuming that the x & y coordinates are relative to the
			// application window.
			gevt = _gazeEventsMap.get(key);
				i = gevt.getDoctorid();
				if (gevt.getImageid() == _singleImageId) {
					x = gevt.getX();
					y = gevt.getY();
					if (i > _colors.size()) {
						safeIndex = _colors.size() % i;
						g2.setColor(_colors.get(safeIndex));
						
					} else {
						g2.setColor(_colors.get(i));
					}

					// draw the crosshair
					g2.setStroke(new BasicStroke(2f));
					g2.drawLine(x - 15, y, x - 5, y);
					g2.drawLine(x, y - 15, x, y - 5);
					g2.drawLine(x + 5, y, x + 15, y);
					g2.drawLine(x, y + 5, x, y + 15);
				}
		}
		g2.dispose();
	}

	/**
	 * IObserver method for observing Events
	 */
	@Override
	public void onNotify(ObserverEvent event) {
		if (event instanceof GazeEvent) {
			GazeEvent gevt = (GazeEvent) event;
			// store the latest gazeEvent or just store if no event for current
			// doctor exists...
			
			
			
			
			//Point locOnScreen = Settings.getInstance().getMainFrame()
			//.getLocationOnScreen();
			//int height = Settings.getInstance().getMainFrame().getHeight();
			//int width = Settings.getInstance().getMainFrame().getWidth();
			if (gevt.getX() > 110) {

				if (_gazeEventsMap.get(gevt.getDoctorid()) != null) {
					// a event from doctor exists.. update if newer then stored
					GazeEvent oldGaze = _gazeEventsMap.get(gevt.getDoctorid());
					if (oldGaze.getTime() < gevt.getTime()) {
						// old gaze older then incoming
						_gazeEventsMap.put(gevt.getDoctorid(), gevt);
						this.repaint();
					}
				} else {
					// new doctor, add this event to the map
					_gazeEventsMap.put(gevt.getDoctorid(), gevt);
					this.repaint();
				}
				this.setVisible(true);
				if (_singleImageId == -1) {
					_singleImageId = gevt.getImageid();
				}
			}
		} else if (event instanceof CollaborationEvent) {
			CollaborationEvent collabEvent = (CollaborationEvent) event;

			switch (collabEvent.type()) {
			case DOCTOR:
				_singleImageId = collabEvent.getImageId();
				break;
			case IMAGE:
				_singleImageId = collabEvent.getImageId();
				break;
			case END:
				// clear the map if collaboration ends.
				_gazeEventsMap.clear();
				this.setVisible(false);
				_singleImageId = -1;
				break;
			}
			this.repaint();

		} else if (event instanceof DoctorEvent) {
			DoctorEvent docEvent = (DoctorEvent) event;
			// clear the map if the user logs out
			if (docEvent.type() == DoctorEventType.LOGOUT) {
				_gazeEventsMap.clear();
				this.setVisible(false);
			}
		}
	}

}
