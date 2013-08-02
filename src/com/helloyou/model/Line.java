package com.helloyou.model;

import java.awt.Color;
import java.awt.Graphics;

public class Line implements Paintable{
	private Mindmap mindmap;
	private Node start;
	private Node end;
	
	public Line(Mindmap map,Node start,Node end){
		this.mindmap=map;
		this.start=start;
		this.end=end;
	}

	public Mindmap getMindmap() {
		return mindmap;
	}


	public void setMindmap(Mindmap mindmap) {
		this.mindmap = mindmap;
	}


	public Node getStart() {
		return start;
	}


	public void setStart(Node start) {
		this.start = start;
	}


	public Node getEnd() {
		return end;
	}


	public void setEnd(Node end) {
		this.end = end;
	}

	@Override
	public void paint(Graphics g) {
		if (!this.start.getVisible() || !this.end.getVisible()) {
			return;
		}
		g.setColor(Color.blue);
		g.drawLine((int)start.getX(), (int)start.getY(), (int)end.getX(), (int)end.getY());
	}
	
	
}
