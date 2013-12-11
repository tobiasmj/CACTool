package gsd.model;

import java.awt.image.BufferedImage;

/**
 * Class that represents a internal image in the CACTool
 */
public class CACImage {
	// fields
	private BufferedImage _img;
	private int _id;

	/**
	 * Constructor for CACImage
	 * 
	 * @param id
	 *            the identification of the image
	 * @param img
	 *            a BufferedImage instance
	 * @throws Exception
	 *             thrown if instantiated without a BufferedImage
	 */
	public CACImage(int id, BufferedImage img) throws Exception {
		if (img != null) {
			_img = img;
			_id = id;
		} else {
			throw new Exception("Invalid input : _img = " + _img + ", id :"
					+ id);
		}
	}

	public BufferedImage get_img() {
		return _img;
	}

	public int get_id() {
		return _id;
	}
}
