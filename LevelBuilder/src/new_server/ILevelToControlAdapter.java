package new_server;

public interface ILevelToControlAdapter {

	/**
	 * Show this level's mToPixel.
	 * @param mToPixel
	 */
	public void setMToPixel(double mToPixel);
	
	public static ILevelToControlAdapter VoidPattern = new ILevelToControlAdapter() {

		@Override
		public void setMToPixel(double mToPixel) {}
	};
}


