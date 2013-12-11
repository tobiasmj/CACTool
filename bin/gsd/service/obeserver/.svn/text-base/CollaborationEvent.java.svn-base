package gsd.service.obeserver;

import gsd.model.Doctor;

/**
 * Class used for sending collaboration events internally in the CACTool
 */
public class CollaborationEvent extends ObserverEvent {
	// fields
	private CollaborationEventType _type;
	private int _imageId;
	private Doctor _doctor;

	// constructor
	public CollaborationEvent(CollaborationEventType type, Doctor doctor,
			int imageId) {
		super();
		this._type = type;
		this._doctor = doctor;
		this._imageId = imageId;
	}

	/**
	 * gets the type of collaboration event.
	 * 
	 * @return CollaborationTypeEvent
	 */
	public CollaborationEventType type() {
		return _type;
	}

	/**
	 * get the doctor in the collaboration event.
	 * 
	 * @return Doctor
	 */
	public Doctor getDoctor() {
		return _doctor;
	}

	/**
	 * get the imageId in the collaboration event.
	 * 
	 * @return int
	 */
	public int getImageId() {
		return _imageId;
	}
}
