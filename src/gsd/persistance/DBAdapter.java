package gsd.persistance;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import pku.edu.dataAccess.Doctor;
import pku.edu.dataAccess.DoctorDAO;
import gsd.model.CACImage;
import gsd.model.Images;
import gsd.model.Doctors;

/**
 * Class for handling all queries to the database holding doctor and image
 * information. This class acts as an adapter for the DataBase access
 * implemented at PKU. Implemeted as a singleton
 */
public class DBAdapter implements InformationStorage {

	// fields
	// Singleton instance
	private static DBAdapter instance;
	// Testing images
	private Images _images;

	/**
	 * Constructor
	 */
	private DBAdapter() {

	}

	/**
	 * Singleton access
	 * 
	 * @return this instance
	 */
	public static DBAdapter getInstance() {
		if (instance == null)
			instance = new DBAdapter();
		return instance;
	}

	/**
	 * Update the location of a doctor
	 * 
	 * @param doctor
	 */
	public void updateDoctor(Doctor doctor) {
		DoctorDAO ddao = new DoctorDAO();
		try {
			ddao.updateDoctor(doctor);
		} catch (SQLException e) {
			// TODO: Handle
		}
	}

	/**
	 * Get a list of all online doctors excluding yourself
	 */
	@SuppressWarnings("unchecked")
	public Doctors getDoctors(Doctor self) {
		DoctorDAO ddao = new DoctorDAO();
		Doctors doctors = new Doctors();
		try {
			ArrayList<Doctor> doctorList = (ArrayList<Doctor>) ddao
					.findOnlineDoctor();
			for (int i = doctorList.size() - 1; i >= 0; i--) {
				if (doctorList.get(i).getId() == self.getId()) {
					doctorList.remove(i);
					continue;
				}
			}
			doctors.addAll(doctorList);
		} catch (SQLException e) {
			// TODO: Handle
		}
		return doctors;
	}

	/**
	 * Get a doctor based on bluetooth mac-address. Returns null if no doctor is
	 * found
	 */
	public Doctor getDoctor(String btMac) {
		DoctorDAO ddao = new DoctorDAO();
		Doctor doctor = new Doctor();
		try {
			doctor = ddao.findDoctorByMac(btMac);
		} catch (SQLException e) {
			System.out.println("SQL Exception");
			// TODO: Handle
		}
		return doctor;
	}

	/**
	 * Placeholder Images, until real implementation is made
	 * 
	 * @return HashMap<Integer,BufferedImage>
	 */

	public Images getImages() {
		// TODO: Uncomment when access to MicrosoftSQL is enabled and make sure
		// to use pictures instead of images
		// ArrayList<Picture> images =
		// (ArrayList<Picture>)pictureAccess.findAll();
		// return images;
		if (_images == null) {
			populateImages();
			return _images;
		} else {
			return _images;
		}
	}

	/**
	 * private method used to populate the image array with local images.
	 */
	private void populateImages() {
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

}
