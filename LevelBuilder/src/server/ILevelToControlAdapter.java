package server;

public interface ILevelToControlAdapter {

	/**
	 * Show this level's mToPixel.
	 * @param mToPixel
	 */
	public void setMToPixel(double mToPixel);
	
	public void setNewCameraLimits(double xmin, double xmax, double ymin, double ymax);

	public static ILevelToControlAdapter VoidPattern = new ILevelToControlAdapter() {

		@Override
		public void setMToPixel(double mToPixel) {}
		
		@Override
		public void setNewCameraLimits(double xmin, double xmax, double ymin, double ymax) {}
	};
}


