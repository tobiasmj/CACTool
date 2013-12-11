package gsd.model;

import gsd.service.obeserver.GazeEvent;
import gsd.service.obeserver.IObserver;
import gsd.service.obeserver.ObserverEvent;
import gsd.model.Doctor;
import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 * This class holds a concurrent list of online doctors
 */
public class Doctors extends AbstractListModel implements IObserver {
	// fields
	private static final long serialVersionUID = 2947612491110189305L;
	private ArrayList<Doctor> doctors;

	// Constructor
	public Doctors() {
		doctors = new ArrayList<Doctor>();
	}

	/**
	 * Add a doctor
	 * 
	 * @param doctor
	 */
	public synchronized void addDoctor(Doctor doctor) {
		for (Doctor d : doctors) {
			if (d.getId() == doctor.getId()) {
				return;
			}
		}
		doctors.add(doctor);
	}

	/**
	 * Add a PKU doctor
	 * 
	 * @param doctor
	 */
	public synchronized void addDoctor(pku.edu.dataAccess.Doctor doctor) {
		for (Doctor d : doctors) {
			if (d.getId() == doctor.getId()) {
				return;
			}
		}

		doctors.add(clonePKUDoc(doctor));
	}

	/**
	 * Remove a doctor
	 * 
	 * @param doctor
	 */
	public synchronized void removeDoctor(Doctor doctor) {
		for (Doctor d : doctors) {
			if (d.getId() == doctor.getId()) {
				doctors.remove(d);
				return;
			}
		}
	}

	/**
	 * Remove a PKU doctor
	 * 
	 * @param doctor
	 */
	public synchronized void removeDoctor(pku.edu.dataAccess.Doctor doctor) {
		for (Doctor d : doctors) {
			if (d.getId() == doctor.getId()) {
				doctors.remove(d);
				return;
			}
		}
	}

	/**
	 * Add a list of doctors
	 * 
	 * @param d
	 */
	public void addAll(ArrayList<pku.edu.dataAccess.Doctor> d) {
		for (pku.edu.dataAccess.Doctor doctor : d) {
			addDoctor(doctor);
		}
	}

	/**
	 * get the size of the internal doctors arrayList
	 * 
	 * @return
	 * 
	 */
	public int getSize() {
		return doctors.size();
	}

	/**
	 * Get the doctor at index
	 */
	@Override
	public Object getElementAt(int index) {
		return doctors.get(index);
	}

	@Override
	/**
	 * Notification method invoked when observing using the IObserver interface  
	 */
	public void onNotify(ObserverEvent event) {
		if (event instanceof GazeEvent) {
			GazeEvent gazeEvent = (GazeEvent) event;
			Doctor doc;
			for (int i = 0; i < doctors.size(); i++) {
				doc = doctors.get(i);
				if (doc.getId() == gazeEvent.getDoctorid()) {
					doc.setImageId(gazeEvent.getImageid());
					// notify model listeners that content has changed
					fireContentsChanged(this, i, i);
				}
			}
		}
	}

	/**
	 * Private method to convert from PKU doctor to CACTool internal doctor
	 * model.
	 * 
	 * @param doc
	 * @return Doctor as defined internally in the model
	 */
	private Doctor clonePKUDoc(pku.edu.dataAccess.Doctor doc) {
		return new Doctor(doc.getId(), doc.getName(), doc.getMac());
	}
}
