package ar.edu.itba.cg.tpe2.rayTracer;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.cg.tpe2.core.camera.Camera;
import ar.edu.itba.cg.tpe2.core.scene.Scene;
import ar.edu.itba.cg.tpe2.rayTracer.ImagePartitioners.HorizontalImagePartitioner;

/**
 * RayCaster creates an image form a scene viewed from a camera
 */
public class RayCaster {

	public static final int COLOR_VARIATION_LINEAR = 0;
	public static final int COLOR_VARIATION_LOG = 0;
	private List<RayCasterThread> threads;
	protected BufferedImage image = null;
	private IImagePartitioner imagePartitioner;
	private int bucketsSize;
	
	/**
	 * RayCaster constructor
	 * 
	 * @param scene Scene representation to work with
	 * @param camera Actual camera where the viewer is   
	 * @param numberOfThreads Number of threads to create
	 * @param numberOfBuckets Number of buckets to make 
	 * @param colorProvider Color mode: Random or Ordered
	 * @param colorVariation Color variation type: Linear or Log
	 */
	public RayCaster(Scene scene, Camera camera, int numberOfThreads, int numberOfBuckets, int colorVariation, boolean progressBar) {
		threads = new ArrayList<RayCasterThread>(numberOfThreads);
		this.bucketsSize = numberOfBuckets;
		for (int i=0; i < numberOfThreads; i++){
			RayCasterThread rayCasterThread = new RayCasterThread(scene, camera, this, progressBar);
			rayCasterThread.setColorVariation(colorVariation);
			rayCasterThread.start();
			threads.add(rayCasterThread);
		}
		imagePartitioner = new HorizontalImagePartitioner();
	}

	/**
	 * Get the image viewed through a viewport
	 * 
	 * @param width Width for the new image viewed from the viewport
	 * @param height Height for the new image viewed from the viewport
	 * @param imageType Format for the new image
	 *
	 * @return A BufferedImage representation  viewed from the viewport
	 * 
	 * @see The BufferedImage documentation for correct imageTypes
	 */
	public BufferedImage getImage(int width, int height, int imageType) {
		if ( thereAreDeadThreads() )
			return null;

		image = new BufferedImage(width, height, imageType);

		List<Task> tasks = imagePartitioner.getPortions(bucketsSize,width,height,imageType);
//		cb = new CyclicBarrier(2);
		synchronized (RayCasterThread.class) {
			RayCasterThread.setImage(image);
			RayCasterThread.setTasks(tasks);
		}
		
		try {
			synchronized (this) {
				this.wait();	
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fillImageFromTasks(tasks);
		return image;
	}

	private void fillImageFromTasks(List<Task> tasks) {
		if ( ! tasks.isEmpty() ){
			for(Task t:tasks){
				Rectangle region = t.getRegion();
				BufferedImage taskImage = t.getImage();
				int fromX = (int) region.getX();
				int fromY = (int) region.getY();
				float width = (float)region.getWidth();
				float height = (float)region.getHeight();
				for(int i = 0; i < width; i++){
					for(int j = 0; j < height; j++){
						image.setRGB(i+fromX, j+fromY, taskImage.getRGB(i, j));
					}
				}
			}
		}
	}

	private boolean thereAreDeadThreads() {
		for (RayCasterThread rct:threads){
			if ( !rct.isAlive() )
				return true;
		}
		return false;
	}

	/**
	 * Synchronization for the BufferedImage
	 * 
	 * @return Actual working image
	 */
	synchronized protected BufferedImage getImage() {
		return image;
	}

	/**
	 * Cleanup for the RayCaster class
	 */
	public void cleanup() {
		// Only need to interrupt one thread. Then the Barrier is broken and they all finish.
		threads.get(0).interrupt();
	}
	
	/**
	 * Finalize method
	 */
	protected void finalize() throws Throwable {
		this.cleanup();
		super.finalize();
	}
	
}