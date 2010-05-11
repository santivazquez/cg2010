package ar.edu.itba.cg.tpe2.core.geometry;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import ar.edu.itba.cg.tpe2.core.shader.Shader;

/**
 * Ray Intersection algorithm based on http://softsurfer.com/Archive/algorithm_0105/algorithm_0105.htm 
 *
 */
public class Triangle extends Primitive {

	private static final double DISTANCE_TOLE  = 0.00000000000001;
	Point3d p1, p2, p3;
	
	Vector3 u, v, n;
	
	double uu, uv, vv;
	
	double minX, minY, maxX, maxY;
	double uvMinX, uvMinY, uvMaxX, uvMaxY;
	
	//Vectores Normales a cada punto
	private Vector3d n1, n2, n3;
	
	//Mapeos para Texturas para cada punto
	private Point2d uv1, uv2, uv3;
	
	Transform transform;
	
	public Transform getTransform() {
		return transform;
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	public void setUVs(Point2d uv1, Point2d uv2, Point2d uv3) {
		this.uv1 = uv1;
		this.uv2 = uv2;
		this.uv3 = uv3;
		
		uvMinX = Math.min(uv1.x, Math.min(uv2.x, uv3.x));
		uvMinY = Math.min(uv1.y, Math.min(uv2.y, uv3.y));
		uvMaxX = Math.max(uv1.x, Math.max(uv2.x, uv3.x));
		uvMaxY = Math.max(uv1.y, Math.max(uv2.y, uv3.y));
	}
	
	public Triangle(String name, Shader shader, Point3d p1, Point3d p2, Point3d p3, Vector3d n1, Vector3d n2, Vector3d n3, Point2d uv1, Point2d uv2, Point2d uv3, Transform trans) throws IllegalArgumentException {
		this(name,shader, p1, p2, p3, trans);
		
		this.n1 = n1;
		this.n2 = n2;
		this.n3 = n3;
		
		setUVs(uv1, uv2, uv3);
	}
	
	public Triangle(String name, Shader shader, Point3d p1, Point3d p2, Point3d p3, Vector3d n1, Vector3d n2, Vector3d n3, Transform trans) throws IllegalArgumentException {
		this(name,shader, p1, p2, p3, trans);
		
		this.n1 = n1;
		this.n2 = n2;
		this.n3 = n3;

	}
	
	public Triangle(String name, Shader shader, Point3d p1, Point3d p2, Point3d p3, Point2d uv1, Point2d uv2, Point2d uv3, Transform trans) throws IllegalArgumentException {
		this(name, shader, p1, p2, p3, trans);

		setUVs(uv1, uv2, uv3);
	}
	
	public Triangle(String name, Shader shader, Point3d p1, Point3d p2, Point3d p3, Transform trans) throws IllegalArgumentException {
		super(name,shader);
		u = new Vector3(p1,p2);
		v = new Vector3(p1,p3);
		n = new Vector3();
		n.cross(u, v);
		if ( n.equals(new Vector3()) )
			// Triangle is either a segment or a point
			throw new IllegalArgumentException("Triangle is either a segment or a point");
	    uu = u.dot(u);
	    uv = u.dot(v);
	    vv = v.dot(v);
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		
		this.transform = trans;
		
		if(this.transform != null){
			// Debo correr la transformacion
			this.transform.applyTransform(this);
		}
		
		minX = Math.min(p1.x, Math.min(p2.x, p3.x));
		minY = Math.min(p1.y, Math.min(p2.y, p3.y));
		maxX = Math.max(p1.x, Math.max(p2.x, p3.x));
		maxY = Math.max(p1.y, Math.max(p2.y, p3.y));
	}
	
	public Point3d intersect(Ray ray) {
		Point3d destiny = (Point3d) ray.getOrigin().clone();
		destiny.add(ray.getDirection());

		// ray direction vector
		Vector3 dir = new Vector3(ray.getOrigin(),destiny);
	    
		Vector3 w0 = new Vector3(p1,ray.getOrigin());
		
		double a = -n.dot(w0);
		double b = n.dot(dir);
		
        // Check if ray is parallel to triangle plane
	    if ( Math.abs(b) < DISTANCE_TOLE ) {
	    	// Ray lies in triangle plane ( Determinant is near zero )
	        if ( a < DISTANCE_TOLE && a > -DISTANCE_TOLE )
	            return null;
	        // Ray disjoint from plane
	        else
	        	return null;
	    }
	    
	    double r = a/b;
	    
	    // Check if ray goes away from triangle
	    if ( r < 0.0 )
	    	return null;
	    
	    Point3d intersectionPoint = new Point3d ( ray.getOrigin() );
	    dir.scale(r);
	    intersectionPoint.add(dir);
	    
	    Vector3 w = new Vector3(p1,intersectionPoint);
	    
	    if ( ! containsPoint(w.dot(u),w.dot(v)) )
	    	return null;
	    return intersectionPoint;
	}
	
	private boolean containsPoint(double wu, double wv) {
		double D;
	    D = uv * uv - uu * vv;

	    // Get and test parametric coordinates
	    double s, t;
	    s = (uv * wv - vv * wu) / D;
	    t = (uv * wu - uu * wv) / D;
	    if(Math.abs(s) < 0.00001) s = 0;
	    if(Math.abs(t) < 0.00001) t = 0;
	    // Check if is outside the Triangle
	    if (s < 0.0 || s > 1.0 || t < 0.0 || (s + t) > (1.0 + 0.00001))
	        return false;

	    return true;
	}

	@Override
	public String toString() {
		return "Triangle [n=" + n + ", n1=" + n1 + ", n2="
				+ n2 + ", n3=" + n3 + ", p1=" + p1 + ", p2=" + p2 + ", p3="
				+ p3 + ", u=" + u + ", uu=" + uu + ", uv=" + uv + ", uv1="
				+ uv1 + ", uv2=" + uv2 + ", uv3=" + uv3 + ", v=" + v + ", vv="
				+ vv + ", getName()=" + getName() + ", getShader()="
				+ getShader() + "]";
	}

	public void transformWith(Matrix4d m) {
		m.transform(p1);
		m.transform(p2);
		m.transform(p3);
		recalculate();
	}
	
	private void recalculate(){
		u = new Vector3(p1,p2);
		v = new Vector3(p1,p3);
		n = new Vector3();
		n.cross(u, v);
	    uu = u.dot(u);
	    uv = u.dot(v);
	    vv = v.dot(v);
	}
	
	@Override
	public List<Point3d> getBoundaryPoints() {
		// TODO Auto-generated method stub
		return new ArrayList<Point3d>();
	}
	
	@Override
	public double[] getUV(Point3d point) {

		/*Matrix3d md = new Matrix3d(p1.x, p2.x, p3.x, p1.y, p2.y, p3.y, p1.z, p2.z, p3.z);
		Matrix3d ma1 = new Matrix3d(point.x, p2.x, p3.x, point.y, p2.y, p3.y, point.z, p2.z, p3.z);
		Matrix3d ma2 = new Matrix3d(p1.x, point.x, p3.x, p1.y, point.y, p3.y, p1.z, point.z, p3.z);
		Matrix3d ma3 = new Matrix3d(p1.x, p2.x, point.x, p1.y, p2.y, point.y, p1.z, p2.z, point.z);
		
		double d = md.determinant();
		double a1 = ma1.determinant();
		double a2 = ma2.determinant();
		double a3 = ma3.determinant();

		double u = a1 / d;
		double v = a2 / d;

		double w = a3 / d; 
		
		return new double[]{u,v};*/
		
		//double u = uvMinX + Math.abs((minX - point.x) / (maxX - minX)) * (uvMaxX - uvMinX);
		double u = Math.abs((minX - point.x) / (maxX - minX));

		//double v = uvMinY + Math.abs((maxY - point.y) / (maxY - minY)) * (uvMaxY - uvMinY);
		double v = Math.abs((maxY - point.y) / (maxY - minY));
		
		return new double[]{u,v};
	}

	@Override
	public Vector3 getNormalAt(Point3d p) {
		Vector3 vector3 = new Vector3(n);
		vector3.scale(-1);
		return vector3;
	}

}