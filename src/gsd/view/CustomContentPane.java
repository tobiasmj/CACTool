package gsd.view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Initial content pane for the CACTool used to display waiting message to the
 * user until a login message is recieved.
 */
public class CustomContentPane extends JPanel {
	// fields
	private static final long serialVersionUID = -686095374265959203L;
	private boolean loggedIn = false;

	/**
	 * class constructor
	 */
	public CustomContentPane() {
		super();
		this.setBackground(new Color(103, 103, 103));
	}

	/**
	 * setter for login.
	 */
	public void setLogin(boolean l) {
		loggedIn = l;
	}

	/**
	 * overwritten method to control drawing of the content
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!loggedIn) {
			String message = "Waiting for log in...";
			FontMetrics fm = g.getFontMetrics(getFont());
			int textWidth = fm.stringWidth(message);
			if (this.getWidth() > textWidth + 120) {
				g.setColor(new Color(150, 150, 150));
				g.fillRoundRect(60, (this.getHeight() - 120) / 2,
						this.getWidth() - 120, 100, 15, 15);

				int x = (this.getWidth() - 120 - textWidth) / 2 + 60;
				int y = ((this.getHeight() - fm.getHeight() + fm.getAscent() - fm
						.getDescent()) / 2);
				g.setColor(Color.BLACK);
				g.drawString(message, x, y);
			}
		}
	}

}
