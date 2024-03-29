package ar.edu.itba.cg.tpe2.core.geometry;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import ar.edu.itba.cg.tpe2.core.shader.Shader;

/**
 * Ray Intersection algorithm based on http://softsurfer.com/Archive/algorithm_0105/algorithm_0105.htm 
 *
 */
public class Triangle extends Primitive {

	private static final float DISTANCE_TOLE  = 0.00000000000001f;
	Point3f p1, p2, p3;
	
	Vector3 u, v, n;
	
	float uu, uv, vv;
	
	float minX, minY, maxX, maxY;
	float uvMinX, uvMinY, uvMaxX, uvMaxY;
	
	//Vectores Normales a cada punto
	private Vector3f n1, n2, n3;
	
	//Mapeos para Texturas para cada punto
	private Point2f uv1, uv2, uv3;
	
	Transform transform;
	
	public Transform getTransform() {
		return transform;
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	public void setUVs(Point2f uv1, Point2f uv2, Point2f uv3) {
		this.uv1 = uv1;
		this.uv2 = uv2;
		this.uv3 = uv3;
		
		uvMinX = (float)Math.min(uv1.x, Math.min(uv2.x, uv3.x));
		uvMinY = (float)Math.min(uv1.y, Math.min(uv2.y, uv3.y));
		uvMaxX = (float)Math.max(uv1.x, Math.max(uv2.x, uv3.x));
		uvMaxY = (float)Math.max(uv1.y, Math.max(uv2.y, uv3.y));
	}
	
	public Triangle(String name, Shader shader, Point3f p1, Point3f p2, Point3f p3, Vector3f n1, Vector3f n2, Vector3f n3, Point2f uv1, Point2f uv2, Point2f uv3, Transform trans) throws IllegalArgumentException {
		this(name,shader, p1, p2, p3, trans);
		
		this.n1 = n1;
		this.n2 = n2;
		this.n3 = n3;
		
		setUVs(uv1, uv2, uv3);
	}
	
	public Triangle(String name, Shader shader, Point3f p1, Point3f p2, Point3f p3, Vector3f n1, Vector3f n2, Vector3f n3, Transform trans) throws IllegalArgumentException {
		this(name,shader, p1, p2, p3, trans);
		
		this.n1 = n1;
		this.n2 = n2;
		this.n3 = n3;

		setUVs(uv1, uv2, uv3);

	}
	
	public Triangle(String name, Shader shader, Point3f p1, Point3f p2, Point3f p3, Point2f uv1, Point2f uv2, Point2f uv3, Transform trans) throws IllegalArgumentException {
		this(name, shader, p1, p2, p3, trans);

		setUVs(uv1, uv2, uv3);
	}
	
	public Triangle(String name, Shader shader, Point3f p1, Point3f p2, Point3f p3, Transform trans) throws IllegalArgumentException {
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
		
		setUVs(new Point2f(0.1f,0.1f), new Point2f(0.5f,0.1f), new Point2f(0.1f,0.5f));
	}
	
	public Point3f intersect(Ray ray) {
		Point3f destiny = (Point3f) ray.getOrigin().clone();
		destiny.add(ray.getDirection());

		// ray direction vector
		Vector3 dir = new Vector3(ray.getOrigin(),destiny);
	    
		Vector3 w0 = new Vector3(p1,ray.getOrigin());
		
		float a = -n.dot(w0);
		float b = n.dot(dir);
		
        // Check if ray is parallel to triangle plane
	    if ( Math.abs(b) < DISTANCE_TOLE ) {
	    	// Ray lies in triangle plane ( Determinant is near zero )
	        if ( a < DISTANCE_TOLE && a > -DISTANCE_TOLE )
	            return null;
	        // Ray disjoint from plane
	        else
	        	return null;
	    }
	    
	    float r = a/b;
	    
	    // Check if ray goes away from triangle
	    if ( r < 0.0 )
	    	return null;
	    
	    Point3f intersectionPoint = new Point3f ( ray.getOrigin() );
	    dir.scale(r);
	    intersectionPoint.add(dir);
	    
	    Vector3 w = new Vector3(p1,intersectionPoint);
	    
	    if ( ! containsPoint(w.dot(u),w.dot(v)) )
	    	return null;
	    return intersectionPoint;
	}
	
	private boolean containsPoint(float wu, float wv) {
		float D;
	    D = uv * uv - uu * vv;

	    // Get and test parametric coordinates
	    float s, t;
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

	public void transformWith(Matrix4f m) {
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
	public float[] getUV(Point3f point) {

		Vector3 p12 = new Vector3(p1, p2);
		Vector3 p13 = new Vector3(p1, p3);
		
		Vector3 w = new Vector3(p1,point);
    
		float wp12 = w.dot(p12);
		float wp13 = w.dot(p13);
		
	    float p12p12 = u.dot(u);
	    float p12p13 = u.dot(v);
	    float p13p13 = v.dot(v);
		
	    float D = p12p13 * p12p13 - p12p12 * p13p13;
		
		float alpha = (p12p13 * wp13 - p13p13 * wp12) / D;
		float beta  = (p12p13 * wp12 - p12p12 * wp13) / D;
    
        Vector2f v12 = new Vector2f(uv2.x - uv1.x, uv2.y - uv1.y);
        Vector2f v13 = new Vector2f(uv3.x - uv1.x, uv3.y - uv1.y);         

        v12.scale(alpha);
        v13.scale(beta);
        
        v12.add(v13);
                
        float u = v12.x;
        float v = v12.y;
        
        if (v < 0) {
	        v = (1 - Math.abs(v));
        }
        
        u = Math.abs(u);
        
        return new float[]{u,v};
}



	@Override
	public Vector3 getNormalAt(Point3f p, Point3f from) {
        Vector3 vector3 = new Vector3(n);
        Vector3 back = new Vector3(n);
        Vector3 v = new Vector3(p, from);
        back.scale(-1);

        float cross_AB = v.dot(vector3), cross_AC = v.dot(back);
        float length_A = v.length(), length_B = vector3.length(), length_C = back.length();

        float angleA = (float)Math.acos(cross_AB/(length_A*length_B));
        float angleB = (float)Math.acos(cross_AC/(length_A*length_C));

        if(angleA < angleB){
                return vector3;
        } else return back;

	}

	@Override
	public float[] getBoundaryPoints() {
		float [] extremes = {Math.min(Math.min(p1.x, p2.x),p3.x),  Math.max(Math.max(p1.x, p2.x),p3.x),
				Math.min(Math.min(p1.y, p2.y),p3.y),  Math.max(Math.max(p1.y, p2.y),p3.y),
				Math.min(Math.min(p1.z, p2.z),p3.z),  Math.max(Math.max(p1.z, p2.z),p3.z)};
		return extremes;
	}
	
}
