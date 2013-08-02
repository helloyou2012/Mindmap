package com.helloyou.model;

import java.awt.geom.Rectangle2D;

public class Option {
	private Rectangle2D mapArea=null;
	private double timeperiod=10;
	private double damping=0.55;
	private double minSpeed=0.1;
	private int timeout=2;
	private int centerForce=3;
	private double repulse=6;
	private double wallrepulse=0.4;
	private double attract=15;
	private double centreOffset=0;
	private double maxForce=0.1;
	
	public double getMaxForce() {
		return maxForce;
	}

	public void setMaxForce(double maxForce) {
		this.maxForce = maxForce;
	}

	public double getCentreOffset() {
		return centreOffset;
	}

	public void setCentreOffset(double centreOffset) {
		this.centreOffset = centreOffset;
	}

	public double getAttract() {
		return attract;
	}

	public void setAttract(double attract) {
		this.attract = attract;
	}

	public double getWallrepulse() {
		return wallrepulse;
	}

	public void setWallrepulse(double wallrepulse) {
		this.wallrepulse = wallrepulse;
	}

	public double getRepulse() {
		return repulse;
	}

	public void setRepulse(double repulse) {
		this.repulse = repulse;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getCenterForce() {
		return centerForce;
	}

	public void setCenterForce(int centerForce) {
		this.centerForce = centerForce;
	}

	public double getMinSpeed() {
		return minSpeed;
	}

	public void setMinSpeed(double minSpeed) {
		this.minSpeed = minSpeed;
	}

	public double getDamping() {
		return damping;
	}

	public void setDamping(double damping) {
		this.damping = damping;
	}

	public double getTimeperiod() {
		return timeperiod;
	}

	public void setTimeperiod(double timeperiod) {
		this.timeperiod = timeperiod;
	}

	public Rectangle2D getMapArea() {
		return mapArea;
	}

	public void setMapArea(Rectangle2D mapArea) {
		this.mapArea = mapArea;
	}
	
	public void setMapArea(double x, double y, double w, double h){
		this.mapArea=new Rectangle2D.Double(x,y,w,h);
	}
	
}
