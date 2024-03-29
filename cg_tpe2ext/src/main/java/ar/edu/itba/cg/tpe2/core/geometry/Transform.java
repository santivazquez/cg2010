package ar.edu.itba.cg.tpe2.core.geometry;

import javax.vecmath.Point3f;

public class Transform {
	Point3f translate;
	Float scaleu, scalex, scaley, scalez;
	Float	rotatex, rotatey, rotatez;
	
	public Transform(){
		
	}
	
	public Point3f getTranslate() {
		return translate;
	}
	public void setTranslate(Point3f translate) {
		this.translate = translate;
	}
	public Float getScaleu() {
		return scaleu;
	}
	public void setScaleu(Float scaleu) {
		this.scaleu = scaleu;
	}
	public Float getScalex() {
		return scalex;
	}
	public void setScalex(Float scalex) {
		this.scalex = scalex;
	}
	public Float getScaley() {
		return scaley;
	}
	public void setScaley(Float scaley) {
		this.scaley = scaley;
	}
	public Float getScalez() {
		return scalez;
	}
	public void setScalez(Float scalez) {
		this.scalez = scalez;
	}
	public Float getRotatex() {
		return rotatex;
	}
	public void setRotatex(Float rotatex) {
		this.rotatex = rotatex;
	}
	public Float getRotatey() {
		return rotatey;
	}
	public void setRotatey(Float rotatey) {
		this.rotatey = rotatey;
	}
	public Float getRotatez() {
		return rotatez;
	}
	public void setRotatez(Float rotatez) {
		this.rotatez = rotatez;
	}

	@Override
	public String toString() {
		return "Transform [rotatex=" + rotatex + ", rotatey=" + rotatey
				+ ", rotatez=" + rotatez + ", scaleu=" + scaleu + ", scalex="
				+ scalex + ", scaley=" + scaley + ", scalez=" + scalez
				+ ", translate=" + translate + "]";
	}
	
	public void rotatex(Primitive aPrimitive){
		if(this.rotatex != null)
			aPrimitive.rotatex(this.rotatex*(float)Math.PI/180);
	}
	
	public void rotatey(Primitive aPrimitive){
		if(this.rotatey != null)
			aPrimitive.rotatey(this.rotatey*(float)Math.PI/180);
	}
	
	public void rotatez(Primitive aPrimitive){
		if(this.rotatez != null)
			aPrimitive.rotatez(this.rotatez*(float)Math.PI/180);
	}
	
	public void scalex(Primitive aPrimitive){
		if(this.scalex != null)
			aPrimitive.scalex(this.scalex);
	}
	
	public void scaley(Primitive aPrimitive){
		if(this.scaley != null)
			aPrimitive.scaley(this.scaley);
	}
	
	public void scalez(Primitive aPrimitive){
		if(this.scalez != null)
			aPrimitive.scalez(this.scalez);
	}
	
	public void scaleu(Primitive aPrimitive){
		if(this.scaleu != null)
			aPrimitive.scaleu(this.scaleu);
	}
	
	public void translate(Primitive aPrimitive){
		if(this.translate != null)
			aPrimitive.translate(this.translate);
	}
	
	public void applyTransform(Primitive aPrimitive){
		this.scalex(aPrimitive);
		this.scaley(aPrimitive);
		this.scalez(aPrimitive);
		this.scaleu(aPrimitive);
		
		this.rotatex(aPrimitive);
		this.rotatey(aPrimitive);
		this.rotatez(aPrimitive);
		
		this.translate(aPrimitive);
	}
	
}
