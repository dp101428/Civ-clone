package code;

public class Camera {
	int x, y;
	private static Camera camera;
	
	private Camera() {
		x = 0;
		y = 0;
	}
	
	public static Camera getInstance(){
		if(camera == null)
			camera = new Camera();
		return camera;
	}
	
	public int[] getOffest(int x, int y){
		x -= this.x;
		y -= this.y;
		return new int[]{x,y};
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}

}
