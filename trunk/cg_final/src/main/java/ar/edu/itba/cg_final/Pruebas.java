package ar.edu.itba.cg_final;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Logger;

import ar.edu.itba.cg_final.map.SkyBox;
import ar.edu.itba.cg_final.utils.ResourceLoader;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.input.KeyInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
 
/**
 * Started Date: Jul 24, 2004 <br>
 * <br>
 * Demonstrates intersection testing, sound, and making your own controller.
 * 
 * @author Jack Lindamood
 */
public class Pruebas extends SimpleGame {
	private static final Logger logger = Logger.getLogger(Pruebas.class.getName());
 
	/** Material for my bullet */
	MaterialState bulletMaterial;
 
	/** Target you're trying to hit */
	Box target;
 
	/** Location of laser sound */
	URL laserURL;
 
	/** Location of hit sound */
	URL hitURL;
 
	/** Used to move target location on a hit */
	Random r = new Random();
 
	/** A sky box for our scene. */
	Skybox sb;
 
	/**
	 * The sound tracks that will be in charge of maintaining our sound effects.
	 */
	AudioTrack laserSound;
	AudioTrack targetSound;
 
	public static void main(String[] args) {
		Pruebas app = new Pruebas();
		app.setConfigShowMode(ConfigShowMode.ShowIfNoConfig);
		app.start();
	}
 
	protected void simpleInitGame() {
		setupSound();
 
		/** Create a + for the middle of the screen */
		Text cross = Text.createDefaultTextLabel("Crosshairs", "+");
 
		// 8 is half the width of a font char
		/** Move the + to the middle */
		cross.setLocalTranslation(new Vector3f(display.getWidth() / 2f - 8f,
				display.getHeight() / 2f - 8f, 0));
		statNode.attachChild(cross);
		target = new Box("MyCar", new Vector3f(0, 0, 0), new Vector3f(1, 1, 2));
		target.setModelBound(new BoundingBox());
		target.updateModelBound();
		rootNode.attachChild(target);

		cam.setLocation(target.getCenter().add(0, 5, -20));
		cam.lookAt(target.getCenter(), new Vector3f(0, 1, 1));
		
//		input = new InputHandler();

		
		
		Box floor = new Box("floor", new Vector3f(0, 0, 0), 200, -0.1f, 200);
		floor.setDefaultColor(ColorRGBA.blue.clone());
		rootNode.attachChild(floor);
		
		/** Create a skybox to suround our world */
//		setupSky();
		
		sb = SkyBox.getNightSkyBox(display, 200, 200, 200);

		
		// Attach the skybox to our root node, and force the rootnode to show
		// so that the skybox will always show
		rootNode.attachChild(sb);
		rootNode.setCullHint(Spatial.CullHint.Never);
 
		/**
		 * Set the action called "firebullet", bound to KEY_F, to performAction
		 * FireBullet
		 */
		input.addAction(new FireBullet(), "firebullet", KeyInput.KEY_F, false);
 
		input.addAction(new MoveCar(), "moveme", KeyInput.KEY_SPACE, false);
		
		/** Make bullet material */
		bulletMaterial = display.getRenderer().createMaterialState();
		bulletMaterial.setEmissive(ColorRGBA.green.clone());
 
		/** Make target material */
		MaterialState redMaterial = display.getRenderer().createMaterialState();
		redMaterial.setDiffuse(ColorRGBA.red.clone());
		target.setRenderState(redMaterial);
	}
	
	
	class MoveCar extends KeyInputAction {
		public void performAction(InputActionEvent evt) {
			target.translatePoints(1, 0, 0);
			cam.setLocation(cam.getLocation().add(1, 0, 0));
		}
	}
 
	private void setupSound() {
		/** Set the 'ears' for the sound API */
		AudioSystem audio = AudioSystem.getSystem();
		audio.getEar().trackOrientation(cam);
		audio.getEar().trackPosition(cam);
 
		/** Create program sound */
		try {
			targetSound = audio.createAudioTrack(ResourceLoader.getURL("sound/explosion.ogg"), false);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		targetSound.setMaxAudibleDistance(1000);
		targetSound.setVolume(1.0f);

		try {
			laserSound = audio.createAudioTrack(ResourceLoader.getURL("sound/laser.ogg"), false);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		laserSound.setMaxAudibleDistance(1000);
		laserSound.setVolume(1.0f);
	}
 
//	private void setupSky() {
//		sb = new Skybox("skybox", 200, 200, 200);
//  
//		try {		
//			sb.setTexture(Skybox.Face.North, TextureManager.loadTexture(ResourceLoader.getURL("texture/north.jpg"), 
//					Texture.MinificationFilter.BilinearNearestMipMap,
//					Texture.MagnificationFilter.Bilinear));
//			sb.setTexture(Skybox.Face.West, TextureManager.loadTexture(ResourceLoader.getURL("texture/west.jpg"),
//					Texture.MinificationFilter.BilinearNearestMipMap,
//					Texture.MagnificationFilter.Bilinear));
//			sb.setTexture(Skybox.Face.South, TextureManager.loadTexture(ResourceLoader.getURL("texture/south.jpg"), 
//					Texture.MinificationFilter.BilinearNearestMipMap,
//					Texture.MagnificationFilter.Bilinear));
//			sb.setTexture(Skybox.Face.East, TextureManager.loadTexture(ResourceLoader.getURL("texture/east.jpg"),
//					Texture.MinificationFilter.BilinearNearestMipMap,
//					Texture.MagnificationFilter.Bilinear));
//			sb.setTexture(Skybox.Face.Up, TextureManager.loadTexture(ResourceLoader.getURL("texture/top.jpg"),
//					Texture.MinificationFilter.BilinearNearestMipMap,
//					Texture.MagnificationFilter.Bilinear));
//			sb.setTexture(Skybox.Face.Down, TextureManager.loadTexture(ResourceLoader.getURL("texture/bottom.jpg"), 
//					Texture.MinificationFilter.BilinearNearestMipMap,
//					Texture.MagnificationFilter.Bilinear));
//			sb.preloadTextures();
//	 
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//		
//		CullState cullState = display.getRenderer().createCullState();
//		cullState.setCullFace(CullState.Face.None);
//		cullState.setEnabled(true);
//		sb.setRenderState(cullState);
// 
//		sb.updateRenderState();
//	}
 
	class FireBullet extends KeyInputAction {
		int numBullets;
 
		public void performAction(InputActionEvent evt) {
			logger.info("BANG");
			/** Create bullet */
			Sphere bullet = new Sphere("bullet" + numBullets++, 8, 8, .25f);
			bullet.setModelBound(new BoundingSphere());
			bullet.updateModelBound();
			/** Move bullet to the camera location */
			bullet.setLocalTranslation(new Vector3f(cam.getLocation()));
			bullet.setRenderState(bulletMaterial);
			/**
			 * Update the new world locaion for the bullet before I add a
			 * controller
			 */
			bullet.updateGeometricState(0, true);
			/**
			 * Add a movement controller to the bullet going in the camera's
			 * direction
			 */
			bullet.addController(new BulletMover(bullet, new Vector3f(cam
					.getDirection())));
			rootNode.attachChild(bullet);
			bullet.updateRenderState();
			/** Signal our sound to play laser during rendering */
			laserSound.setWorldPosition(cam.getLocation());
			laserSound.play();
		}
	}
 
	class BulletMover extends Controller {
		private static final long serialVersionUID = 1L;
		/** Bullet that's moving */
		TriMesh bullet;
 
		/** Direciton of bullet */
		Vector3f direction;
 
		/** speed of bullet */
		float speed = 10;
 
		/** Seconds it will last before going away */
		float lifeTime = 5;
 
		BulletMover(TriMesh bullet, Vector3f direction) {
			this.bullet = bullet;
			this.direction = direction;
			this.direction.normalizeLocal();
		}
 
		public void update(float time) {
			lifeTime -= time;
			/** If life is gone, remove it */
			if (lifeTime < 0) {
				rootNode.detachChild(bullet);
				bullet.removeController(this);
				return;
			}
			/** Move bullet */
			Vector3f bulletPos = bullet.getLocalTranslation();
			bulletPos.addLocal(direction.mult(time * speed));
			bullet.setLocalTranslation(bulletPos);
			/** Does the bullet intersect with target? */
			if (bullet.getWorldBound().intersects(target.getWorldBound())) {
				logger.info("OWCH!!!");
				targetSound.setWorldPosition(target.getWorldTranslation());
 
				target.setLocalTranslation(new Vector3f(r.nextFloat() * 10, r
						.nextFloat() * 10, r.nextFloat() * 10));
 
				lifeTime = 0;
 
				targetSound.play();
			}
		}
	}
 
	/**
	 * Called every frame for updating
	 */
	protected void simpleUpdate() {
		// Let the programmable sound update itself.
		AudioSystem.getSystem().update();
		// Move the skybox into position
		sb.getLocalTranslation().set(cam.getLocation().x, cam.getLocation().y,
				cam.getLocation().z);
	}
 
	@Override
	protected void cleanup() {
		super.cleanup();
		if (AudioSystem.isCreated()) {
			AudioSystem.getSystem().cleanup();
		}
	}
}