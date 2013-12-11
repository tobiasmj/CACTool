package gsd.view;

import gsd.model.CACImage;
import gsd.model.Images;
import gsd.service.obeserver.CollaborationEvent;
import gsd.service.obeserver.CollaborationEventType;
import gsd.service.obeserver.GazeEvent;
import gsd.service.obeserver.IObservable;
import gsd.service.obeserver.IObserver;
import gsd.service.obeserver.ObserverEvent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

/**
 * Class to display images in the main view
 */
public class ImagesView extends JPanel implements MouseInputListener,
		IObserver, IObservable {

	private static final long serialVersionUID = 3214941761709603868L;
	// list of the images to be displayed
	ArrayList<CACImage> _imageArray;
	// list of icons to be displayed
	ArrayList<BufferedImage> _iconArray;
	// width and height of component
	int _width, _height;
	// maximum images pr. column and the amount of rows
	// used when displaying the list of images
	int _maxPerCol, _rows;
	// spacing between images in list view
	int _spacing;
	// currently selected image (by single clicking on a image in list view)
	int _selectedIndex = -1;
	// single view or list view
	Boolean _singleView = false;
	// the image id for single image view
	int _singleViewImageId = -1;
	// Whether or not to highlight the close button for single view
	Boolean _highlightClose = false;
	// the close button shape
	Shape _closeButton;
	// the mouseEvent point (used in single view to track the cursor)
	Point _mePoint;
	// List of observers
	private List<IObserver> _observers;
	private int _waitingForDoctor = -1;

	/**
	 * ImageView component constructor
	 * 
	 * @param images
	 *            the images to display
	 * @param minWidth
	 *            minimum width of thumbnails in list view mode
	 * @param minHeight
	 *            minimum height of thumbnails in list view mode
	 * @param spacing
	 *            the spacing between images in list view mode
	 */
	public ImagesView(Images images, int minWidth, int minHeight, int spacing) {

		_width = minWidth;
		_height = minHeight;
		_imageArray = images.getImages();
		_spacing = spacing;
		setOpaque(false);
		_iconArray = new ArrayList<BufferedImage>();
		// create thumbnails for list view
		for (CACImage img : _imageArray) {
			BufferedImage iconImage = getFasterScaledInstance(img.get_img(),
					_width, _height,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
			_iconArray.add(iconImage);
		}
		// Instantiate observer list
		_observers = new ArrayList<IObserver>();

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * Overridden method for drawing the component
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// get G2D version of the graphics component
		Graphics2D g2 = (Graphics2D) g.create();

		// sets the rendering to use antialiasing, nice for text and shapes
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// background color
		g2.setColor(new Color(103, 103, 103));
		// draw background
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		// get the bounds for the component
		Rectangle clip = g2.getClipBounds();
		if (_singleView == false) {
			// multiple image view

			// set the maximum images pr. column and row amount
			_maxPerCol = clip.width / _width;
			if (_maxPerCol > 0) {
				_rows = _imageArray.size() / _maxPerCol + 1;
			} else {
				_rows = _imageArray.size();
			}

			int currentRowCounter = 0;
			int indentation;

			// draw images in list view
			for (int i = 0; i < _imageArray.size(); i++) {
				// draw element
				if (i > _maxPerCol - 1) {
					if (_maxPerCol > 0) {
						currentRowCounter = i / _maxPerCol;
					} else {
						currentRowCounter = i;
					}
				}
				if (_maxPerCol > 0) {
					indentation = i % _maxPerCol;
					if (_selectedIndex == i) {
						drawSubComponent(g2, _spacing * indentation
								+ indentation * _width, currentRowCounter
								* (_height + _spacing), _width, _height,
								_iconArray.get(i), true);
					} else {
						drawSubComponent(g2, _spacing * indentation
								+ indentation * _width, currentRowCounter
								* (_height + _spacing), _width, _height,
								_iconArray.get(i), false);
					}

				} else {
					if (_selectedIndex == i) {
						drawSubComponent(g2, 0, i * _height + i * _spacing,
								_width, _height, _iconArray.get(i), true);
					} else {
						drawSubComponent(g2, 0, i * _height + i * _spacing,
								_width, _height, _iconArray.get(i), false);
					}
				}
			}
		} else {
			// single view image
			// image to draw
			if (_singleViewImageId != -1) {
				CACImage singleImage = _imageArray.get(_singleViewImageId);
				// scaled version of imageToDraw
				BufferedImage scaledImage;

				// compare sizes of window & image and scale accordingly
				int imageWidth = singleImage.get_img().getWidth();
				int imageHeight = singleImage.get_img().getHeight();

				int thisWidth = this.getWidth();
				int thisHeight = this.getHeight();

				// scaling stuff...
				double wscale, hscale;
				wscale = (double) singleImage.get_img().getWidth()
						/ (double) this.getWidth();
				hscale = (double) singleImage.get_img().getHeight()
						/ (double) this.getHeight();

				double minHscale, minWscale;
				if (wscale > 1) {
					minWscale = wscale - 1;
				} else {
					minWscale = 1 - wscale;
				}
				if (hscale > 1) {
					minHscale = hscale - 1;
				} else {
					minHscale = 1 - hscale;
				}

				double scaleW = ((double) thisWidth / (double) imageWidth);
				double scaleH = ((double) thisHeight / (double) imageHeight);
				int newWidth, newHeight;
				boolean scaleHorizontal;
				if (imageWidth > thisWidth || imageHeight > thisHeight) {
					// down scaling
					if (minHscale < minWscale) {
						newWidth = (int) (scaleW * (double) imageWidth - 60);
						newHeight = (int) (scaleW * (double) imageHeight);
						scaleHorizontal = true;
					} else {
						newWidth = (int) (scaleH * (double) imageHeight);
						newHeight = (int) (scaleH * (double) imageWidth - 60);
						scaleHorizontal = false;
					}
				} else {
					// up scaling
					if (minHscale > minWscale) {
						newWidth = (int) (scaleW * (double) imageWidth - 60);
						newHeight = (int) (scaleW * (double) imageHeight);
						scaleHorizontal = true;
					} else {
						newWidth = (int) (scaleH * (double) imageHeight);
						newHeight = (int) (scaleH * (double) imageWidth) - 60;
						scaleHorizontal = false;
					}
				}
				// get new image // there is a bug in the fast version of the
				// scaling algorithm when upscaling... TODO fix bug
				scaledImage = getFasterScaledInstance(singleImage.get_img(),
						newWidth, newHeight,
						RenderingHints.VALUE_INTERPOLATION_BICUBIC, false);

				// set the stroke and color for close button drawing
				g2.setStroke(new BasicStroke(3f));
				g2.setColor(new Color(200, 200, 200));

				// align horizontal/vertical in usable space
				if (scaleHorizontal) {
					// draw component (image)
					drawSubComponent(g2, 30,
							((thisHeight - scaledImage.getHeight()) / 2),
							this.getWidth(), this.getHeight(), scaledImage,
							false);
					// draw border for component
					g2.drawRoundRect(30,
							((thisHeight - scaledImage.getHeight()) / 2),
							scaledImage.getWidth(), scaledImage.getHeight(),
							15, 15);
					// draw closing button
					drawCloseButton(g2, 30,
							((thisHeight - scaledImage.getHeight()) / 2));
				} else {
					drawSubComponent(g2,
							((thisWidth - scaledImage.getWidth()) / 2), 30,
							this.getWidth(), this.getHeight(), scaledImage,
							false);
					g2.drawRoundRect(
							((thisWidth - scaledImage.getWidth()) / 2), 30,
							scaledImage.getWidth(), scaledImage.getHeight(),
							15, 15);
					drawCloseButton(g2,
							((thisWidth - scaledImage.getWidth()) / 2), 30);
				}
				// tidy up!
				g2.dispose();
			} else {
				// single view but no picture to show yet, waiting for gazeEvent
				// update imageId for the selected doctor.
				String message = "Waiting for gaze events...";
				FontMetrics fm = g2.getFontMetrics(getFont());
				int textWidth = fm.stringWidth(message);
				if (this.getWidth() > textWidth + 120) {
					g2.setColor(new Color(150, 150, 150));
					g2.fillRoundRect(60, (this.getHeight() - 120) / 2,
							this.getWidth() - 120, 100, 15, 15);
					int x = (this.getWidth() - 120 - textWidth) / 2 + 60;
					int y = ((this.getHeight() - fm.getHeight()
							+ fm.getAscent() - fm.getDescent()) / 2);
					g2.setColor(Color.BLACK);
					g2.drawString(message, x, y);
					drawCloseButton(g2, 60, (this.getHeight() - 120) / 2);
				}
				g2.dispose();
			}
		}
	}

	/**
	 * draws the closebutton at the specified location within the component
	 * 
	 * @param g
	 *            graphics object
	 * @param xCord
	 *            x-coordinate
	 * @param yCord
	 *            y-coordinate
	 */
	private void drawCloseButton(Graphics g, int xCord, int yCord) {
		// closeButton
		xCord = xCord - 15;
		yCord = yCord - 15;
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.BLACK);
		g2.fillOval(xCord, yCord, 30, 30);
		if (_highlightClose) {
			g2.setColor(new Color(230, 230, 230));
		} else {
			g2.setColor(new Color(200, 200, 200));
		}
		g2.setStroke(new BasicStroke(3f));
		_closeButton = new Ellipse2D.Float(xCord, yCord, 30, 30);
		g2.drawOval(xCord, yCord, 30, 30);
		g2.drawLine(xCord + 8, yCord + 8, xCord + 22, yCord + 22);
		g2.drawLine(xCord + 8, yCord + 22, xCord + 22, yCord + 8);

	}

	/**
	 * Draw the image component both in single image view and in list view
	 * 
	 * @param g
	 *            graphics component
	 * @param xCord
	 *            x-coordinate
	 * @param yCord
	 *            y-coordinate
	 * @param width
	 *            the width of the image
	 * @param height
	 *            the height of the image
	 * @param imageToDraw
	 *            the image
	 * @param isSelected
	 *            if the image is selected.
	 */
	private void drawSubComponent(Graphics g, int xCord, int yCord, int width,
			int height, BufferedImage imageToDraw, Boolean isSelected) {
		int x = xCord;
		int y = yCord;
		int w = width;
		int h = height;
		int arc = 30;

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		if (imageToDraw != null && _singleView == false) {
			g2.setClip(new java.awt.geom.RoundRectangle2D.Double(x, y, w, h,
					arc, arc));
			g2.drawImage(imageToDraw, null, x, y);
			g2.setClip(null);
			g2.setStroke(new BasicStroke(2f));
			if (isSelected == true) {
				g2.setColor(Color.BLUE);
				g2.drawRoundRect(x, y, w, h, arc, arc);
			}
		} else if (imageToDraw != null && _singleView == true) {
			g2.drawImage(imageToDraw, null, x, y);
		}
		g2.dispose();
	}

	/**
	 * Stolen from Filthy Rich Clients 2007, Chet Haase, Romain Guy,
	 * Addison-Wesly
	 * 
	 * @param img
	 *            origianl image
	 * @param targetWidth
	 *            the scaled image width
	 * @param targetHeight
	 *            the scaled image height
	 * @param hint
	 *            the rendering hints
	 * @param progressiveBilinear
	 *            use progressiveBilinear algorithm
	 * @return
	 */
	private BufferedImage getFasterScaledInstance(BufferedImage img,
			int targetWidth, int targetHeight, Object hint,
			boolean progressiveBilinear) {
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		BufferedImage scratchImage = null;
		Graphics2D g2 = null;
		int w, h;
		int prevW = ret.getWidth();
		int prevH = ret.getHeight();
		if (progressiveBilinear) {
			// Use multistep technique: start with original size,
			// then scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}
		do {
			if (progressiveBilinear && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}
			if (progressiveBilinear && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			if (scratchImage == null) {
				// Use a single scratch buffer for all iterations
				// and then copy to the final, correctly sized image
				// before returning
				scratchImage = new BufferedImage(w, h, type);
				g2 = scratchImage.createGraphics();
			}
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);
			prevW = w;
			prevH = h;
			ret = scratchImage;
		} while (w != targetWidth || h != targetHeight);

		if (g2 != null) {
			g2.dispose();
		}
		// If we used a
		// target size,
		// the results into it
		if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
			scratchImage = new BufferedImage(targetWidth, targetHeight, type);
			g2 = scratchImage.createGraphics();
			g2.drawImage(ret, 0, 0, null);
			g2.dispose();
			ret = scratchImage;
		}
		return ret;
	}

	/**
	 * Overwritten method for detecting mouse clicks on component
	 * 
	 * @params me the mouse event
	 */
	@Override
	public void mouseClicked(MouseEvent me) {
		if (!_singleView) {
			// multiple ImageView
			// locate the image id that was clicked.
			if (me.getClickCount() == 1) {
				Point p = me.getPoint();
				int x = p.x / (_width + _spacing);
				int y = p.y / (_height + _spacing);
				int indexClicked = (x) + _maxPerCol * (y);
				if (_selectedIndex != indexClicked) {
					_selectedIndex = indexClicked;
				}
			}
			if (me.getClickCount() == 2) {
				// locate the image id that was double clicked
				Point p = me.getPoint();
				int x = p.x / (_width + _spacing);
				int y = p.y / (_height + _spacing);
				int indexClicked = (x) + _maxPerCol * (y);
				if (_selectedIndex != -1) {
					_selectedIndex = indexClicked;
					_singleView = true;
					_singleViewImageId = _selectedIndex;
					_highlightClose = false;

					// notify observers of change to single image view
					notifyObservers(_singleViewImageId);
				}
			}
			// repaint the components
			this.repaint();

		} else {
			// Single image view
			Point p = me.getPoint();
			if (_closeButton != null && _closeButton.contains(p)) {
				_singleView = false;
				_singleViewImageId = -1;
				_selectedIndex = -1;
				_closeButton = null;
				notifyObservers(-1);
				this.repaint();
			}

		}
	}

	@Override
	public void mouseEntered(MouseEvent me) {
	}

	@Override
	public void mouseExited(MouseEvent me) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		// track cursor movement
		if ((_singleView && _singleViewImageId != -1)
				|| _waitingForDoctor != -1) {

			_mePoint = me.getPoint();
			// if me within the close button then hightlight
			if (_closeButton != null && _closeButton.contains(_mePoint)) {
				_highlightClose = true;

				this.repaint();
			} else if (_highlightClose == true) {
				_highlightClose = false;
				repaint();
			}
		}

	}

	/**
	 * IObserver method for observing Events
	 */
	@Override
	public void onNotify(ObserverEvent event) {
		if (event instanceof CollaborationEvent) {
			// cast first
			CollaborationEvent collabEvent = (CollaborationEvent) event;
			// switch on type of event
			switch (collabEvent.type()) {
			case DOCTOR:
				// clear the imageId, waiting for next gazeEvent from
				// gazeTracker to reset it.
				_singleViewImageId = -1;
				_singleView = true;
				_waitingForDoctor = collabEvent.getDoctor().getId();
				break;
			case END:
				_singleViewImageId = -1;
				_singleView = false;
				_waitingForDoctor = -1;
				break;
			}
			// repaint the component
			this.repaint();
		} else if (event instanceof GazeEvent) {
			GazeEvent gazeEvent = (GazeEvent) event;
			if (gazeEvent.getDoctorid() == _waitingForDoctor) {
				_singleViewImageId = gazeEvent.getImageid();
				_waitingForDoctor = -1;
				this.repaint();
			}
		}
	}

	@Override
	public void addObserver(IObserver observer) {
		_observers.add(observer);
	}

	@Override
	public void removeObserver(IObserver observer) {
		_observers.remove(observer);
	}

	/**
	 * Notify all observers of collaborationEvents;
	 */
	public void notifyObservers(int imgId) {
		CollaborationEvent collabEvent;
		if (imgId != -1) {
			collabEvent = new CollaborationEvent(CollaborationEventType.IMAGE,
					null, imgId);
		} else {
			collabEvent = new CollaborationEvent(CollaborationEventType.END,
					null, -1);
		}
		for (Iterator<IObserver> it = _observers.iterator(); it.hasNext();) {
			IObserver observer = (IObserver) it.next();
			observer.onNotify(collabEvent);
		}
	}
}
