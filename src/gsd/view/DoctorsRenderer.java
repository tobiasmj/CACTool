package gsd.view;

import gsd.model.Doctor;
import gsd.settings.Settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * Rendering class for the list of doctors in DoctorsView
 */
public class DoctorsRenderer extends JPanel implements ListCellRenderer {
	// fields
	private static final long serialVersionUID = 1384805051788080369L;
	private Doctor _value;
	private Vector<Color> _colors = new Vector<Color>();
	private int _index;
	private int _spacing;
	private int _selectedIndex = -1;
	private int _selectedImageIndex = -1;

	/***
	 * Constructor
	 * 
	 * @param spacing
	 *            the spacing between elements
	 */
	public DoctorsRenderer(int spacing) {
		super();
		_colors = Settings.getInstance().getColors();
		_spacing = spacing;
	}

	/**
	 * Overwritten method for handling the list elements.
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof Doctor) {
			_value = (Doctor) value;
			_index = index;
		}

		return this;
	}

	/**
	 * overwritten method to control drawing of the content
	 */
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		Dimension size = getSize();
		// if we are viewing a specific image (_selectedImageIndex) and the
		// doctor represented
		// by the _value is currently watching the same image draw their color.
		// otherwise draw in gray.
		if (_selectedImageIndex != -1 && _selectedImageIndex == _value.getImageId()) {
			if (_value.getId() > _colors.size()) {
				int safeIndex = _colors.size() % _value.getId();
				g2.setColor(_colors.get(safeIndex));
				
			} else {
				g2.setColor(_colors.get(_value.getId()));
			}

		} else {
			// default color button
			g2.setColor(new Color(150, 150, 150));
		}
		g2.fillRoundRect(0, _spacing, size.width, size.height - _spacing, 10,
				10);

		FontMetrics fm = g2.getFontMetrics(getFont());
		int textWidth = fm.stringWidth(_value.getName());
		int x = (size.width - textWidth) / 2;
		int y = (size.height + fm.getAscent() + _spacing - fm.getDescent()) / 2;

		if (_selectedIndex == _index) {
			g2.setColor(Color.WHITE);
		} else {
			g2.setColor(Color.BLACK);
		}

		g2.drawString(_value.getName(), x, y);

	}

	/**************************
	 * Getters
	 **************************/

	public int getSelectedIndex() {
		return _selectedIndex;
	}

	public void setSelectedIndex(int _selectedIndex) {
		this._selectedIndex = _selectedIndex;
	}

	public void setSingleViewImageId(int id) {
		_selectedImageIndex = id;

	}
}