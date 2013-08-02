package com.helloyou.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.helloyou.listener.PaintListener;
import com.helloyou.model.Mindmap;
import com.helloyou.model.Paintable;
import com.helloyou.view.MindmapView;

public class MindmapController extends MouseAdapter implements PaintListener{
	private MindmapView view;
	private Mindmap mindmap;
	
	public MindmapController(MindmapView view, Mindmap mindmap) {
		this.view = view;
		this.mindmap = mindmap;
	}

	@Override
	public void show(Paintable obj) {
		view.display(obj);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mindmap.drag(e.getX(), e.getY());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mindmap.chooseNode(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//Do nothing
		mindmap.setDragNode(null);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		mindmap.clicked(e.getX(), e.getY());
	}
	
}
