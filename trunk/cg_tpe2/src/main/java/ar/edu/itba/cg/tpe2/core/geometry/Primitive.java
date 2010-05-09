package ar.edu.itba.cg.tpe2.core.geometry;

import java.util.List;

import javax.vecmath.Point3d;

import ar.edu.itba.cg.tpe2.core.shader.Shader;

/**
 * Primitives 
 */
public abstract class Primitive {

	// Name
	String name;
	
	// Shader
	Shader shader;
	
	public Primitive(String name, Shader shader) {
		this.name = name;
		this.shader = shader;
		if ( shader != null )
			shader.setPrimitive(this);
	}
	
	/**
	 * Get primitive's name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set primitive's name
	 * 
	 * @return
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get primitive's shader
	 * 
	 * @return Shader
	 */
	public Shader getShader() {
		return shader;
	}

	/**
	 * Set primitive's shader
	 * 
	 */
	public void setShader(Shader shader) {
		this.shader = shader;
	}

	/**
	 * Rotate object in X axis 
	 * 
	 */
	public abstract void rotatex(double angle);
	
	/**
	 * Rotate object in Y axis 
	 * 
	 */
	public abstract void rotatey(double angle); 
	
	/**
	 * Rotate object in Z axis 
	 * 
	 */
	public abstract void rotatez(double angle); 
	
	/**
	 * Scale primitive in X axis
	 * 
	 * 
	 */
	public abstract void scalex(double scale); 
	
	/**
	 * Scale primitive in Y axis
	 * 
	 */
	public abstract void scaley(double scale); 
	
	/**
	 * Scale primitive in Z axis
	 * 
	 */
	public abstract void scalez(double scale); 
	
	/**
	 * Scale primitive in U
	 * 
	 */
	public abstract void scaleu(double scale); 
	
	/**
	 * Get the intersection between the figure and a ray
	 */
	public abstract Point3d intersect(Ray ray);

	/**
	 * Get the list of boundary points of the primitive
	 * 
	 * @return List of boundary points
	 */
	public abstract List<Point3d> getBoundaryPoints();
	
	public abstract double [] getUV(Point3d p);
	
	public abstract Vector3 getNormalAt(Point3d p);
}
