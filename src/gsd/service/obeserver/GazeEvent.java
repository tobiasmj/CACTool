package gsd.service.obeserver;

/**
 * Events from the gazetracker
 */
public class GazeEvent extends ObserverEvent {
	// fields
	private int id, time, doctorid, imageid, x, y;

	/***********************************
	 * Constructor
	 * 
	 * @param id
	 * @param time
	 *            time in milisecs
	 * @param doctorid
	 *            id of the doctor
	 * @param imageid
	 *            id of the image
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 */
	public GazeEvent(int id, int time, int doctorid, int imageid, int x, int y) {
		this.id = id;
		this.time = time;
		this.doctorid = doctorid;
		this.imageid = imageid;
		this.x = x;
		this.y = y;
	}

	/***********************************
	 * Getters
	 **********************************/

	public int getId() {
		return id;
	}

	public int getTime() {
		return time;
	}

	public int getDoctorid() {
		return doctorid;
	}

	public int getImageid() {
		return imageid;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
