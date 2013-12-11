package gsd.persistance;

import gsd.model.CACImage;
import gsd.model.Doctors;
import gsd.model.Images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;

import pku.edu.dataAccess.Doctor;

public class FakeDB implements InformationStorage {
	// fields
	// Singleton instance
	private static FakeDB instance;

	private Vector<Doctor> _doctors;
	private Images _images;

	public FakeDB() {
		_doctors = new Vector<Doctor>();

		Doctor doc = new Doctor();
		doc.setId(0);
		doc.setName("Morten");
		doc.setMac("01:00");
		doc.setOnline(1);
		_doctors.add(doc);

		doc = new Doctor();
		doc.setId(2);
		doc.setName("Lise");
		doc.setMac("02:00");
		doc.setOnline(1);
		_doctors.add(doc);

		doc = new Doctor();
		doc.setId(3);
		doc.setName("Tobias");
		doc.setMac("03:00");
		doc.setOnline(1);
		_doctors.add(doc);

	}

	/**
	 * Singleton access
	 * 
	 * @return this instance
	 */
	public static FakeDB getInstance() {
		if (instance == null)
			instance = new FakeDB();
		return instance;
	}

	@Override
	public pku.edu.dataAccess.Doctor getDoctor(String btMac) {
		for (int i = 0; i < _doctors.size(); i++) {
			Doctor doc = (Doctor) _doctors.get(i);
			if (doc.getMac().equals(btMac)) {

				return doc;
			}
		}
		return null;
	}

	@Override
	public Doctors getDoctors(Doctor self) {
		Doctors tempDoctors = new Doctors();
		for (int i = 0; i < _doctors.size(); i++) {
			if (((Doctor) _doctors.get(i)).getOnline() == 1) {
					//&& ((Doctor) _doctors.get(i)).getId() != self.getId()) {
				tempDoctors.addDoctor((pku.edu.dataAccess.Doctor) _doctors
						.get(i));
			}
		}
		return tempDoctors;
	}

	@Override
	public Images getImages() {

		if (_images == null) {
			_images = new Images();
			BufferedImage img;
			try {
				// adding testing images
				img = ImageIO.read(new File("images/1.jpg"));
				_images.addImage(new CACImage(1, img));
				img = ImageIO.read(new File("images/2.jpg"));
				_images.addImage(new CACImage(2, img));
				img = ImageIO.read(new File("images/3.jpg"));
				_images.addImage(new CACImage(3, img));
				img = ImageIO.read(new File("images/4.jpg"));
				_images.addImage(new CACImage(4, img));
				img = ImageIO.read(new File("images/5.jpg"));
				_images.addImage(new CACImage(5, img));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return _images;
	}

	@Override
	public void updateDoctor(pku.edu.dataAccess.Doctor doctor) {
		for (int i = 0; i < _doctors.size(); i++) {
			if (doctor.getId() == ((Doctor) _doctors.get(i)).getId()) {
				Doctor tempDoc = (Doctor) _doctors.get(i);
				if (doctor.getOnline() != tempDoc.getOnline()) {
					tempDoc.setOnline(doctor.getOnline());
				}
			}
		}
	}

}
