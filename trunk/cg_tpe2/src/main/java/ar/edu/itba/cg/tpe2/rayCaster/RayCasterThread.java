package ar.edu.itba.cg.tpe2.rayCaster;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import ar.edu.itba.cg.tpe2.core.camera.Camera;
import ar.edu.itba.cg.tpe2.core.geometry.Primitive;
import ar.edu.itba.cg.tpe2.core.geometry.Ray;
import ar.edu.itba.cg.tpe2.core.geometry.Triangle;
import ar.edu.itba.cg.tpe2.core.geometry.Vector3;
import ar.edu.itba.cg.tpe2.core.light.Light;
import ar.edu.itba.cg.tpe2.core.light.PointLight;
import ar.edu.itba.cg.tpe2.core.scene.Scene;

/**
 * RayCasterThread is an optimization using threads 
 */
class RayCasterThread extends Thread {

	private Scene scene;
	private List<Light> lights;
	private Camera camera;
	private int fromX;
	private int toX;
	private int fromY;
	private int toY;
	private static int width;
	private static int height;
	private RayCaster rayCaster;
	private IColorProvider colorMode;
	private int colorVariation = RayCaster.COLOR_VARIATION_LINEAR;
	static private double farthestDistance = 20.0;
	private static final int MAX_REBOUNDS=1;

	private static List<Rectangle> tasks = null;
	private static Iterator<Rectangle> taskIterator;
	private static int finishedTasks;
	
	private static int i=0;
	private static BufferedImage image;
	private int id;
	private double totalLight;
	/**
	 * Constructor for RayCasterThread class
	 * 
	 * @param scene Scene representation to work with
	 * @param camera Actual camera where the viewer is 
	 * @param rayCaster RayCaster class parent
	 * @param cb CyclicBarrier for the threads
	 */
	public RayCasterThread(Scene scene, Camera camera, RayCaster rayCaster) {
		this.scene = scene;
		this.lights = scene.getLights();
		this.camera = camera;
		this.rayCaster = rayCaster;
		this.totalLight = calculateTotalLight();
		id = i;
		i++;
	}

	private double calculateTotalLight() {
		double t = 0;
		for(Light l:lights)
			t += l.getPower();
		return t;
	}

	/**
	 * Get the color mode for the ray caster
	 * 
	 * @return Color mode
	 */
	public IColorProvider getColorMode() {
		return colorMode;
	}
	
	/**
	 * Set the color mode for the ray caster
	 * 
	 * @param colorProvider Color mode to be set to the ray caster
	 */
	public void setColorMode(IColorProvider colorProvider) {
		this.colorMode = colorProvider;
	}
	
	/**
	 * Get color variation set to the ray caster
	 * 
	 * @return Color variation type
	 */
	public int getColorVariation() {
		return colorVariation;
	}
	
	/**
	 * Set the color variation used by the ray caster
	 * 
	 * @param colorVariation Color variation type
	 */
	public void setColorVariation(int colorVariation) {
		this.colorVariation = colorVariation;
	}
	
	/**
	 * Set image portion to work with
	 * 
	 * @param fromX First column
	 * @param toX Last column
	 * @param fromY First row
	 * @param toY Last row
	 * @param width Viewport width
	 * @param height Viewport height
	 */
	public void setPortion(Rectangle portion, int width, int height) {
		this.fromX = portion.x;
		this.toX = portion.x+portion.width;
		this.fromY = portion.y;
		this.toY = portion.y+portion.height;
	}

	/**
	 * Get last column of the image to work with
	 * 
	 * @return last column
	 */
	public int getToX() {
		return toX;
	}
	
	/**
	 * Get first column of the image to work with
	 * 
	 * @return first column
	 */
	public int getFromX() {
		return fromX;
	}

	/**
	 * Get first row of the image to work with
	 * 
	 * @return first row
	 */
	public int getFromY() {
		return fromY;
	}
	
	/**
	 * Get last row of the image to work with
	 * 
	 * @return last row
	 */
	public int getToY() {
		return toY;
	}
	
	synchronized public static void setTasks(List<Rectangle> tasks) {
		RayCasterThread.tasks = tasks;
		RayCasterThread.taskIterator = tasks.iterator();
		finishedTasks = 0;
	}

	public boolean allTasksFinished(){
		return tasks.size() == finishedTasks;
	}
	
	private Rectangle getTask() {
		Rectangle r;
		synchronized (this.getClass()) {
			if ( tasks == null || !taskIterator.hasNext() )
				return null;
			r = taskIterator.next();
		}
		return r;
	}

	public static void setImage(BufferedImage img) {
		image = img;
		width = img.getWidth();
		height = img.getHeight();
	}
	
	/**
	 * Start the rayCaster
	 * 
	 */
	public void run() {
		while ( true ){
			Rectangle task = getTask();
			while ( task == null ){
				task = getTask();
			}
//			System.out.println("Thread "+id+" processing ( "+task.x+" , "+task.y+" ) ( "+task.width+" , "+task.height+" ) ");
			setPortion(task, width, height);
			Point3d origin = camera.getEye();

			Color color;
			Ray ray;
			for (int i = fromX; i < toX; i++) {
				for (int j = fromY; j < toY; j++) {
					// Set infinite color
					color = Color.BLACK;
					
					Point3d po = camera.getPointFromXY(width, height, i, j);
					
					// Create a new Ray from camera, i, j
					ray = new Ray(origin, po);
					
					if ( i == 512 && j == 200)
						System.out.println("");
					// Get pixel color
					color = getColor(ray, MAX_REBOUNDS);
					
					// Set color to image(i, j)
					synchronized (image) {
						image.setRGB(i, j, color.getRGB());
					}
				}
			}
			synchronized( this.getClass() ){
				finishedTasks++;
				if ( allTasksFinished() )
					synchronized (rayCaster) {
						rayCaster.notify();	
					}
			}
		}
	}

    private Color getColor(Ray ray, int maxRebounds) {
    	
    	Point3d intersectionPoint = new Point3d();
    	Primitive impactedFigure;
		Color refractColor, reflectColor, ilumColor;
    	
		impactedFigure = scene.getFirstIntersection(ray, intersectionPoint);
		
		if (impactedFigure == null ) {
			return new Color(0.0f, 0.0f, 0.0f);
		}

    	float refleccion=0.0f;
    	float refraccion=0.0f;
		
    	/*
    	 * Calcula los rayos de refleccion y refraccion (los que atraviesan y se reflejan)
    	 */
//		Ray refractRay = getRefractRay(ray, impactedFigure, intersectionPoint, refraccion);
//		Ray reflectRay = getReflectRay(ray, impactedFigure, intersectionPoint);    	
    	Ray refractRay = null;
		Ray reflectRay = null;
    	
		float [] reflactRGBArray = {0.0f, 0.0f, 0.0f};
		float [] reflectRGBArray = {0.0f, 0.0f, 0.0f};
		
		// Stopping recursive calls and gets only ilumination color
		if (maxRebounds-- != 0) {
			if ( refractRay != null ){
				refractColor = getColor(refractRay, maxRebounds);
				reflactRGBArray = refractColor.getRGBColorComponents(null);
			}
			if ( reflectRay != null ){
				reflectColor = getColor(reflectRay, maxRebounds);
				reflectRGBArray = reflectColor.getRGBColorComponents(null);
			}
		}
    	
    	ilumColor = ilumination(ray, impactedFigure, intersectionPoint);
    	float [] ilumRGBArray = ilumColor.getRGBColorComponents(null);
    	
    	float [] resultingRGBArray = new float [3];
    	
    	//TODO cambiar esto por los valores posta

    	
    	resultingRGBArray[0] = ilumRGBArray[0]; //+ refraccion*reflactRGBArray[0] + refleccion*reflectRGBArray[0];
    	resultingRGBArray[1] = ilumRGBArray[1]; //+ refraccion*reflactRGBArray[1] + refleccion*reflectRGBArray[1];
    	resultingRGBArray[2] = ilumRGBArray[2]; //+ refraccion*reflactRGBArray[2] + refleccion*reflectRGBArray[2];
    	
    	return new Color(resultingRGBArray[0],resultingRGBArray[1], resultingRGBArray[2]);
	}

	private Color ilumination(Ray ray, Primitive impactedFigure, Point3d intersectionPoint) {
		Vector3 figureNormal = impactedFigure.getNormalAt(intersectionPoint);
		float [] figureRGBComponents = impactedFigure.getColorAt(intersectionPoint).getRGBColorComponents(null);
		float [] rgbs = {0,0,0};

		if ( impactedFigure.getClass().equals(Triangle.class) )
			System.out.println("LALA");
		
		for(Light l:lights){
			if ( l instanceof PointLight ){
				PointLight pl = (PointLight) l;
				Primitive p = null;
				Point3d intersectionP = new Point3d(intersectionPoint);
				Ray rayFromLight = new Ray(intersectionP,pl.getP(),true);
				
				p = scene.getFirstIntersection(rayFromLight, new Point3d());
				
				// No object between light and impactedFigure :D
				if ( p == null ){
					Vector3 dirToLight = new Vector3(intersectionP,pl.getP());
					double distanceToLight = intersectionP.distance(pl.getP());
					dirToLight.normalize();
					figureNormal.normalize();
					float angleToLight = (float) figureNormal.dot(dirToLight);
					if ( angleToLight >= 0 ){
						float [] lightContribution = {0,0,0};
						float[] lightRGBComponents = pl.getASpec().getColor().getRGBColorComponents(null);
						float fallOff = pl.getFallOff(distanceToLight);
						
						lightContribution[0] = angleToLight * lightRGBComponents[0] * figureRGBComponents[0] * fallOff;
						lightContribution[1] = angleToLight * lightRGBComponents[1] * figureRGBComponents[1] * fallOff;
						lightContribution[2] = angleToLight * lightRGBComponents[2] * figureRGBComponents[2] * fallOff;
						
	//					if ( lightContribution[0] > 0 && lightContribution[0] < 1 )
						rgbs[0] += lightContribution[0];
						
	//					if ( lightContribution[1] > 0 && lightContribution[1] < 1 ) 
						rgbs[1] += lightContribution[1];
						
	//					if ( lightContribution[2] > 0 && lightContribution[2] < 1 )
						rgbs[2] += lightContribution[2];
					}
				}
			}
		}
		//TODO hacer bien, deberia usar el rayo!!!
//		return impactedFigure.getColorAt(intersectionPoint);
		return clamp(rgbs);
	}

	private Color clamp(float [] rgbs){
		return new Color(clamp(rgbs[0]),clamp(rgbs[1]),clamp(rgbs[2]));
	}
	
	private float clamp(float channel){
		if ( channel > 1.0 )
			return 1;
		if ( channel < 0 )
			return 0;
		return channel;
	}
	
	private Ray getReflectRay(Ray ray, Primitive p, Point3d intersectionPoint) {
		return ray.reflectFrom(p.getNormalAt(intersectionPoint), intersectionPoint);
	}

	private Ray getRefractRay(Ray ray, Primitive p, Point3d intersectionPoint, double refraction) {
		return ray.refractFrom(p.getNormalAt(intersectionPoint), intersectionPoint, refraction);
	}

	

}