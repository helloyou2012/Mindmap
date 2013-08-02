package com.helloyou.test;

import javax.swing.JFrame;
import com.helloyou.controller.MindmapController;
import com.helloyou.model.Mindmap;
import com.helloyou.model.Node;
import com.helloyou.model.Option;
import com.helloyou.view.MindmapView;

public class Test {
	
	public static void main(String[] args) {
		JFrame frame=new JFrame("MindMap");
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//create model
		Option option=new Option();
		option.setMapArea(0, 0, 800, 600);
		Mindmap mindmap=new Mindmap(option);
		//init nodes
		Node root=mindmap.addRootNode("Test");
		Node model_=mindmap.addNode(root, "model");
		Node view_=mindmap.addNode(root, "view");
		Node controller_=mindmap.addNode(root, "controller");
		Node listener_=mindmap.addNode(root, "listener");
		mindmap.addNode(model_, "Mindmap");
		mindmap.addNode(model_, "Node");
		mindmap.addNode(model_, "Line");
		mindmap.addNode(model_, "Option");
		mindmap.addNode(model_, "Paintable");
		mindmap.addNode(view_, "MindmapView");
		mindmap.addNode(controller_, "MindmapController");
		mindmap.addNode(listener_, "PaintListener");
		//create view
		MindmapView view=new MindmapView();
		//create controller
		MindmapController controller=new MindmapController(view, mindmap);
		mindmap.addListener(controller);
		view.addMouseListener(controller);
		view.addMouseMotionListener(controller);
		frame.getContentPane().add(view);
		frame.setResizable(false);
		frame.setVisible(true);
	}
}
