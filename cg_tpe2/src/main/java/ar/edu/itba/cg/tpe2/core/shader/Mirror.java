package ar.edu.itba.cg.tpe2.core.shader;

import java.awt.Color;
import java.util.List;

import javax.vecmath.Point3f;

import ar.edu.itba.cg.tpe2.core.colors.Specular;
import ar.edu.itba.cg.tpe2.core.geometry.Primitive;
import ar.edu.itba.cg.tpe2.core.geometry.Ray;
import ar.edu.itba.cg.tpe2.core.light.Light;
import ar.edu.itba.cg.tpe2.core.scene.Scene;

public class Mirror extends Shader {

	private Specular spec;

	public Mirror(String name, String type, Specular spec) {
		super(name, type);
		this.spec = spec;
	}

	public Specular getSpec() {
		return spec;
	}

	@Override
	public String toString() {
		return "Mirror [spec=" + spec + ", getName()=" + getName()
				+ ", getType()=" + getType() + "]";
	}

	@Override
	public Color getColorAt(Point3f aPoint, Primitive primitive, List<Light> lights, Ray viewRay, Scene scene) {
		return this.getSpec().getColor();
	}

	public float getReflectionK(){
		return 1.0f;
	}
	
}
