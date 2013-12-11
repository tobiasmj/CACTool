package gsd.service.obeserver;

import pku.edu.dataAccess.Doctor;

/**
 * DoctorEvent, used to send events among GUI elements internally in the CACTool
 */
public class DoctorEvent extends ObserverEvent {
	// fields
	private DoctorEventType _type;
	private Doctor doctor;

	// constructor
	public DoctorEvent(DoctorEventType type, Doctor doctor) {
		super();
		this._type = type;
		this.doctor = doctor;
	}

	/************
	 * Getters
	 ************/

	public DoctorEventType type() {
		return _type;
	}

	public Doctor getDoctor() {
		return doctor;
	}
}
