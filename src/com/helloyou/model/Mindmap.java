package com.helloyou.model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.helloyou.listener.PaintListener;

public class Mindmap implements Paintable{
	private Node root=null;
	private Node activeNode=null;
	private Node dragNode=null;
	private ArrayList<Node> nodes;
	private ArrayList<Line> lines;
	private boolean movementStopped=false;
	private Option options;
	private ArrayList<PaintListener> listeners;
	
	public Mindmap(Option options){
		nodes=new ArrayList<Node>();
		lines=new ArrayList<Line>();
		listeners=new ArrayList<PaintListener>();
		this.options=options;
	}
	
	public void addListener(PaintListener l){
		listeners.add(l);
	}
	
	public ArrayList<PaintListener> getListeners(){
		return listeners;
	}
	
	public void repaint(){
		for(PaintListener l:listeners)
			l.show(this);
	}
	
	public void chooseNode(int x,int y){
		dragNode=null;
		for(Node node:nodes){
			if(node.pointInNode(x, y))
				dragNode=node;
		}
	}
	
	public void drag(int x,int y){
		if(dragNode!=null)
			dragNode.drag(x, y);
	}
	
	public void clicked(int x,int y){
		chooseNode(x, y);
		if(dragNode!=null){
			this.activeNode=dragNode;
			this.root.animateToStatic();
			dragNode=null;
		}
	}
	
	public Node addNode(Node parent, String name){
		Node node=new Node(this, name, parent, options);
		this.nodes.add(node);
		this.root.animateToStatic();
		return node;
	}
	
	public Node addRootNode(String name){
		Node node=new Node(this, name, null, options);
		this.nodes.add(node);
		this.root=node;
	    this.root.animateToStatic();
	    return node;
	}
	
	public Node getDragNode() {
		return dragNode;
	}

	public void setDragNode(Node dragNode) {
		this.dragNode = dragNode;
	}

	public boolean isMovementStopped() {
		return movementStopped;
	}

	public void setMovementStopped(boolean movementStopped) {
		this.movementStopped = movementStopped;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public Node getActiveNode() {
		return activeNode;
	}

	public void setActiveNode(Node activeNode) {
		this.activeNode = activeNode;
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}

	public ArrayList<Line> getLines() {
		return lines;
	}

	public void setLines(ArrayList<Line> lines) {
		this.lines = lines;
	}
	@Override
	public void paint(Graphics g) {
		int x=(int)options.getMapArea().getX();
		int y=(int)options.getMapArea().getY();
		int w=(int)options.getMapArea().getWidth();
		int h=(int)options.getMapArea().getHeight();
		g.setColor(Color.black);
		g.fillRect(x,y,w,h);
		for(Line line:lines)
			line.paint(g);
		for(Node node:nodes)
			node.paint(g);
	}
}
