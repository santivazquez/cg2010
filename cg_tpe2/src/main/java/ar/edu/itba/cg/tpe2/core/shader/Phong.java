package ar.edu.itba.cg.tpe2.core.shader;

import java.awt.Color;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ar.edu.itba.cg.tpe2.core.colors.Diffuse;
import ar.edu.itba.cg.tpe2.core.colors.Specular;
import ar.edu.itba.cg.tpe2.core.geometry.Primitive;
import ar.edu.itba.cg.tpe2.core.geometry.Ray;
import ar.edu.itba.cg.tpe2.core.geometry.Vector3;
import ar.edu.itba.cg.tpe2.core.light.Light;
import ar.edu.itba.cg.tpe2.core.light.PointLight;
import ar.edu.itba.cg.tpe2.core.scene.Scene;
import ar.edu.itba.cg.tpe2.utils.Parser;

public class Phong extends Shader {

	private Diffuse diffuse;
	private int samples;
	private Specular spec;
	
	public Phong(String name, String type, Diffuse diffuse, int samples, Specular spec) {
		super(name, type);
		this.diffuse = diffuse;
		this.samples = samples;
		this.spec = spec;
	}
	
	public Diffuse getDiffuse() {
		return diffuse;
	}

	public int getSamples() {
		return samples;
	}

	public Specular getSpec() {
		return spec;
	}

	@Override
	public String toString() {
		return "Phong [samples=" + samples + ", spec=" + spec + ", texture="
				+ diffuse + ", getName()=" + getName() + ", getType()="
				+ getType() + "]";
	}

	@Override
	public Color getColorAt(Point3f primitivePoint, Primitive primitive, List<Light> lights, Ray viewRay, Scene scene) {
		float [] diffuseColor = diffuse.getColorAt(primitivePoint,primitive).getRGBColorComponents(null);
		float [] specularColor = spec.getColor().getRGBColorComponents(null);
		float [] out = Color.BLACK.getRGBColorComponents(null);
		float alpha = spec.getSpecularity();
		
		float coef = 1.0f;
		if ( Parser.SOFT_SHADOWS )
			coef /= Parser.LIGHT_COUNT;
		
		// Don't take into account the ambient light
		
		// V
		Vector3 viewDir = new Vector3(viewRay.getDirection());
		
		// N
		Vector3 objectNormal = new Vector3(primitive.getNormalAt(primitivePoint, viewRay.getOrigin()));
		
		if (!lights.isEmpty()) {
			for(Light l:lights){
				if ( l instanceof PointLight ){
					PointLight pl = (PointLight) l;
					Point3f intersectionP = new Point3f(primitivePoint);
					float[] lightRGBComponents = pl.getASpec().getColor().getRGBColorComponents(null);

					// Lm
					Vector3f vectorToLight = new Vector3f(pl.getP());
					vectorToLight.sub(intersectionP);
					Ray lightRay = new Ray(intersectionP,vectorToLight);
					float distanceToLight = intersectionP.distance(pl.getP());
					Point3f newIntersectionP = new Point3f();
					Primitive p = scene.getFirstIntersection(lightRay, newIntersectionP, primitive);
					
					float distanceToNewPrimitive = intersectionP.distance(newIntersectionP);
					// No object between light and impactedFigure :P
					if ( p == null || ( p != null && distanceToLight < distanceToNewPrimitive ) ){
						// Rm
						Ray reflectedLightRay = lightRay.reflectFrom(objectNormal, primitivePoint);
						
						Vector3f dirReflectedLight = reflectedLightRay.getDirection();
						
						dirReflectedLight.scale(-1);
						
						float angleToLight = (float) vectorToLight.dot(objectNormal);
						float angleToView = (float) dirReflectedLight.dot(viewDir);
						
						out[0] += diffuseColor[0]*angleToLight * 0.5 * coef + specularColor[0]*Math.pow(angleToView,alpha)*lightRGBComponents[0];
						out[1] += diffuseColor[1]*angleToLight * 0.5 * coef + specularColor[1]*Math.pow(angleToView,alpha)*lightRGBComponents[1];
						out[2] += diffuseColor[2]*angleToLight * 0.5 * coef + specularColor[2]*Math.pow(angleToView,alpha)*lightRGBComponents[2];
					}
				}
			}
		}
		return clamp(out);
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

	@Override
	public boolean hasIlumination() {
		return true;
	}
}
