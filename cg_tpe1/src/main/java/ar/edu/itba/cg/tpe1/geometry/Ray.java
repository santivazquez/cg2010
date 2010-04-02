package ar.edu.itba.cg.tpe1.geometry;

import javax.vecmath.Point3d;

public class Ray {
	
	private Point3d origin;
	
	private Point3d direction;
	
	public Ray(Point3d origin, Point3d direction) {
		this.origin = origin;
		this.direction = direction;
	}
	
	public Point3d getOrigin() {
		return origin;
	}

	public void setOrigin(Point3d origin) {
		this.origin = origin;
	}

	public Point3d getDirection() {
		return direction;
	}

	public void setDirection(Point3d direction) {
		this.direction = direction;
	}

}
