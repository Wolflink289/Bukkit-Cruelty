package com.wolflink289.setup.cruelty.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class WXListRenderer extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = -535469967609672719L;
	private boolean selected = false;
	private boolean bggrey = false;
	private String[] text;
	private JList list;
	
	@Override
	public Component getListCellRendererComponent(JList arg0, Object arg1, int arg2, boolean arg3, boolean arg4) {
		setText(arg1.toString());
		list = arg0;
		selected = arg3;
		
		if (arg1 instanceof WXListItem) {
			text = new String[] { ((WXListItem) arg1).getText(), ((WXListItem) arg1).getNote() };
		} else {
			text = new String[] { arg1.toString(), null };
		}
		
		bggrey = arg2 % 2 == 0;
		return this;
	}
	
	@Override
	public void paint(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		// Background
		g.setColor(selected ? new Color(7, 170, 240) : (bggrey ? new Color(245, 245, 245) : Color.white));
		g.fillRect(0, 0, list.getWidth(), list.getHeight());
		
		// Foreground
		g.setColor(selected ? Color.white : Color.black);
		g.setFont(g.getFont().deriveFont(Font.BOLD));
		g.drawString(text[0], 1, getHeight() - g.getFontMetrics().getHeight() / 2 + 3);
		
		// Foreground - 2
		if (text[1] != null) {
			g.setColor(selected ? Color.white : new Color(100, 100, 100));
			g.setFont(g.getFont().deriveFont(Font.PLAIN));
			g.drawString(text[1], getWidth() - 2 - g.getFontMetrics().stringWidth(text[1]), getHeight() - g.getFontMetrics().getHeight() / 2 + 3);
		}
	}
}
