package gsd.model;

import java.util.ArrayList;

/**
 * Class that holds a collection of CACImages for display in the CACTool
 */
public class Images {
	// fields
	private static final long serialVersionUID = -7555879432061617345L;
	ArrayList<CACImage> _images = new ArrayList<CACImage>();

	/**
	 * Method for adding images to the collection.
	 * 
	 * @param img
	 *            CACImage to add to the collection.
	 * @throws Exception
	 *             thrown if image reference is null.
	 */
	public void addImage(CACImage img) throws Exception {
		if (img != null) {
			_images.add(img);
		} else {
			throw new Exception("input image = " + img);
		}
	}

	/**
	 * Get all images
	 * 
	 * @return ArrayList of CACImages.
	 */
	public ArrayList<CACImage> getImages() {
		return _images;
	}

	/**
	 * Method for returning an image from the collection based on its Id.
	 * 
	 * @param id
	 * @return CACImage
	 * @throws Exception
	 *             thrown if a image with the id does not exist.
	 */
	public CACImage getImageById(int id) throws Exception {
		for (CACImage img : _images) {
			if (img.get_id() == id) {
				return img;
			}
		}
		throw new Exception("Image does not exsist - id : " + id);
	}
}
