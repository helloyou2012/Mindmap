package com.helloyou.view;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLayeredPane;

import com.helloyou.model.Paintable;

public class MindmapView extends JLayeredPane{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8821924272283741078L;
	
	private Paintable paintObj;
	
	public MindmapView(){
		setSize(800, 600);
	}
	
	public void display(Paintable obj){
		this.paintObj=obj;
		repaint();
	}
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g0=(Graphics2D)g;
		g0.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		g0.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(paintObj!=null)
			paintObj.paint(g0);
	}
	
}
