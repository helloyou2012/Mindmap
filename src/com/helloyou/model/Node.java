package com.helloyou.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Node implements Paintable{
	private String name;
	private Mindmap mindmap;
	private Node parent;
	private ArrayList<Node> children;
	private double x = 1;
	private double y = 1;
	private double width=100;
	private double height=100;
	private double dx = 0;
	private double dy = 0;
	private boolean moving = false;
	private boolean visible = true;
	private boolean hasPosition = false;
	private Option options;
	private Timer taskTimer=null;

	public Node(Mindmap map, String name, Node parent, Option opts) {
		this.mindmap = map;
		this.name = name;
		this.parent = parent;
		this.options = opts;
		this.children = new ArrayList<Node>();
		this.mindmap.setMovementStopped(false);
		if (parent == null) {
			mindmap.setActiveNode(this);
		} else {
			Line line=new Line(mindmap, this, parent);
			mindmap.getLines().add(line);
			this.parent.children.add(this);
		}
	}

	public boolean pointInNode(int x0,int y0){
		if(x0>x-width/2&&x0<x+width/2&&y0>y-height/2&&y0<y+height/2){
			return true;
		}
		return false;
	}
	
	public void drag(int x, int y) {
		if (mindmap.getDragNode()==this) {
			this.x = x;
			this.y = y;
			this.mindmap.getRoot().animateToStatic();
		}
	}
	// ROOT NODE ONLY: control animation loop
	public void animateToStatic() {
		TimerTask task=new TimerTask() {
			@Override
			public void run() {
				mindmap.setMovementStopped(true);
			}
		};
		if(taskTimer==null){
			taskTimer=new Timer();
		}else{
			taskTimer.cancel();
			taskTimer=new Timer();
		}
		taskTimer.schedule(task, options.getTimeout() * 1000);
		if (this.moving) {
			return;
		}
		this.moving = true;
		this.mindmap.setMovementStopped(false);
		this.animateLoop();
	}

	public void animateLoop() {
		this.mindmap.repaint();
		if (this.findEquilibrium() || this.mindmap.isMovementStopped()) {
			this.moving = false;
			return;
		}
		TimerTask task=new TimerTask() {
			@Override
			public void run() {
				Node.this.animateLoop();
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 10);
	}

	// find the right position for this node
	public boolean findEquilibrium() {
		boolean stable = this.display();
		for (Node node : children) {
			stable = node.findEquilibrium() && stable;
		}
		return stable;
	}

	public boolean display() {
		if (this.visible) {
			// if: I'm not active AND my parent's not active AND my children
			// aren't active ...
			if (this.mindmap.getActiveNode() != this
					&& this.mindmap.getActiveNode() != this.parent
					&& this.mindmap.getActiveNode().parent != this) {
				this.setVisible(false);
			}
		} else {
			if (this.mindmap.getActiveNode() == this
					|| this.mindmap.getActiveNode() == this.parent
					|| this.mindmap.getActiveNode().parent == this) {
				this.setVisible(true);
			}
		}
		// am I positioned? If not, position me.
		if (!this.hasPosition) {
			this.x = this.options.getMapArea().getWidth() / 2;
			this.y = this.options.getMapArea().getHeight() / 2;
			this.hasPosition = true;
		}
		// are my children positioned? if not, lay out my children around me
		if (this.children.size() > 0) {
			double stepAngle = Math.PI * 2/this.children.size();
			double angle = stepAngle;
			for (Node node : children) {
				angle += stepAngle;
				if (!node.hasPosition) {
					node.x = (50 * Math.cos(angle)) + this.x;
					node.y = (50 * Math.sin(angle)) + this.y;
					node.hasPosition = true;
				}
			}
		}
		// update my position
		return this.updatePosition();
	}

	public boolean updatePosition() {
		if (mindmap.getDragNode()==this) {
			this.dx = 0;
			this.dy = 0;
			return false;
		}

		// apply accelerations
		Point2D forces = this.getForceVector();
		this.dx += forces.getX() * this.options.getTimeperiod();
		this.dy += forces.getY() * this.options.getTimeperiod();

		// damp the forces
		this.dx = this.dx * this.options.getDamping();
		this.dy = this.dy * this.options.getDamping();

		// ADD MINIMUM SPEEDS
		if (Math.abs(this.dx) < this.options.getMinSpeed()) {
			this.dx = 0;
		}
		if (Math.abs(this.dy) < this.options.getMinSpeed()) {
			this.dy = 0;
		}
		if (Math.abs(this.dx) + Math.abs(this.dy) == 0) {
			return true;
		}
		// apply velocity vector
		this.x += this.dx * this.options.getTimeperiod();
		this.y += this.dy * this.options.getTimeperiod();
		this.x = Math
				.min(this.options.getMapArea().getWidth(), Math.max(1, this.x));
		this.y = Math
				.min(this.options.getMapArea().getHeight(), Math.max(1, this.y));
		return false;
	}

	public Point2D getForceVector(){
		double x1, y1, xsign, dist, theta, f;
		double fx = 0,fy = 0;
		// Calculate the repulsive force from every other node
		for(Node node:mindmap.getNodes()){
			if (node==this) {
				continue;
			}
			if (!node.visible) {
				continue;
			}
			// Repulsive force (coulomb's law)
			x1 = (node.x - this.x);
			y1 = (node.y - this.y);
			//adjust for variable node size
			dist = Math.sqrt((x1 * x1) + (y1 * y1));
			//parents stand further away
			if (Math.abs(dist) < 500) {
				if (x1 == 0) {
					theta = Math.PI / 2;
					xsign = 0;
				} else {
					theta = Math.atan(y1 / x1);
					xsign = x1 / Math.abs(x1);
		        }
		        // force is based on radial distance
		        f = (this.options.getRepulse() * 500) / (dist * dist);
		        fx += -f * Math.cos(theta) * xsign;
		        fy += -f * Math.sin(theta) * xsign;
		    }
		}
		// add repulsive force of the "walls"
	    //left wall
	    f = (this.options.getWallrepulse() * 500) / (this.x * this.x);
	    fx += Math.min(2, f);
	    //right wall
	    double rightdist = (this.options.getMapArea().getWidth() - this.x);
	    f = -(this.options.getWallrepulse() * 500) / (rightdist * rightdist);
	    fx += Math.max(-2, f);
	    //top wall
	    f = (this.options.getWallrepulse() * 500) / (this.y * this.y);
	    fy += Math.min(2, f);
	    //bottom wall
	    double bottomdist = (this.options.getMapArea().getHeight() - this.y);
	    f = -(this.options.getWallrepulse() * 500) / (bottomdist * bottomdist);
	    fy += Math.max(-2, f);
	    
	    // for each line, of which I'm a part, add an attractive force.
	    for (Line line:this.mindmap.getLines()) {
	    	Node otherend=null;
	    	if (line.getStart()==this) {
	    		otherend = line.getEnd();
	    	} else if (line.getEnd()==this) {
	    		otherend = line.getStart();
	    	} else {
	    		continue;
	    	}
	    	// Ignore the pull of hidden nodes
	    	if (!otherend.visible) {
	    		continue;
	    	}
	    	// Attractive force (hooke's law)
	    	x1 = (otherend.x - this.x);
	    	y1 = (otherend.y - this.y);
	    	dist = Math.sqrt((x1 * x1) + (y1 * y1));
	    	if (Math.abs(dist) > 0) {
	    		if (x1 == 0) {
	    			theta = Math.PI / 2;
	    			xsign = 0;
	    		}
	    		else {
	    			theta = Math.atan(y1 / x1);
	    			xsign = x1 / Math.abs(x1);
	    		}
	    		// force is based on radial distance
	    		f = (this.options.getAttract() * dist) / 10000;
	    		fx += f * Math.cos(theta) * xsign;
	    		fy += f * Math.sin(theta) * xsign;
	    	}
	    }
	    // if I'm active, attract me to the centre of the area
	    if (this.mindmap.getActiveNode()==this) {
	    	// Attractive force (hooke's law)
	    	x1 = ((this.options.getMapArea().getWidth()/2) - this.options.getCentreOffset() - this.x);
	    	y1 = ((this.options.getMapArea().getHeight()/2) - this.y);
	    	dist = Math.sqrt((x1 * x1) + (y1 * y1));
	    	if (Math.abs(dist) > 0) {
	    		if (x1 == 0) {
	    			theta = Math.PI / 2;
	    			xsign = 0;
	    		} else {
	    			xsign = x1 / Math.abs(x1);
	    			theta = Math.atan(y1 / x1);
	    		}
	    		// force is based on radial distance
	    		f = (0.1 * this.options.getAttract() * dist * this.options.getCenterForce()) / 1000;
	    		fx += f * Math.cos(theta) * xsign;
	    		fy += f * Math.sin(theta) * xsign;
	    	}
	    }
	    if (Math.abs(fx) > this.options.getMaxForce()) {
	    	fx = this.options.getMaxForce() * (fx / Math.abs(fx));
	    }
	    if (Math.abs(fy) > this.options.getMaxForce()) {
	      fy = this.options.getMaxForce() * (fy / Math.abs(fy));
	    }
		return new Point2D.Double(fx, fy);
	}

	public void removeNode(){
		ArrayList<Node> oldNodes=new ArrayList<Node>();
		ArrayList<Line> oldLines=new ArrayList<Line>();
		for(Node node:this.mindmap.getNodes())
			oldNodes.add(node);
		for(Line line:this.mindmap.getLines())
			oldLines.add(line);
		for(Node node:this.children)
			node.removeNode();
		this.mindmap.getNodes().clear();
		for(Node node:oldNodes){
			if(node==this)
				continue;
			this.mindmap.getNodes().add(node);
		}
		this.mindmap.getLines().clear();
		for(Line line:oldLines){
			if(line.getStart()==this || line.getEnd()==this)
				continue;
			this.mindmap.getLines().add(line);
		}
	}

	public Mindmap getMindmap() {
		return mindmap;
	}

	public void setMindmap(Mindmap mindmap) {
		this.mindmap = mindmap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public ArrayList<Node> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

	public boolean getMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean getHasPosition() {
		return hasPosition;
	}

	public void setHasPosition(boolean hasPosition) {
		this.hasPosition = hasPosition;
	}

	public Option getOptions() {
		return options;
	}

	public void setOptions(Option options) {
		this.options = options;
	}
	
	public Color getBackgroundColor(){
		if(mindmap.getActiveNode()==this)
			return Color.blue;
		if(mindmap.getActiveNode()==this.parent)
			return new Color(139, 0, 0);
		return Color.gray;
	}

	@Override
	public void paint(Graphics g) {
		if(!visible)
			return;
		Font font=new Font("default", Font.BOLD, 12);
		FontRenderContext context=((Graphics2D)g).getFontRenderContext();
		TextLayout layout=new TextLayout(name, font, context);
		double widthT=layout.getBounds().getWidth();
		double heightT=layout.getBounds().getHeight();
		
		this.width=(widthT+20);
		this.height=(heightT+20);
		int drawx=(int)(x-width/2);
		int drawy=(int)(y-height/2);
		g.setColor(getBackgroundColor());
		g.fillRoundRect(drawx, drawy, (int)(width), (int)(height), 30, 30);
		g.setColor(Color.white);
		g.drawRoundRect(drawx, drawy, (int)(width), (int)(height), 30, 30);
		
		drawx=(int)(x-widthT/2);
		drawy=(int)(y+heightT/2);
		g.setColor(Color.yellow);
		g.drawString(name, drawx, drawy);
	}
}
