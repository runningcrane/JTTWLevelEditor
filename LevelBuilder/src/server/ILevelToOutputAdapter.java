package server;

public interface ILevelToOutputAdapter {
	
	/**
	 * Set dimensions of the level's JFrame.
	 * @param wm width in pixels
	 * @param hm height in pixels
	 */
	public void setDimensions(int wm, int hm);
	
	/**
	 * Refresh the level image.
	 */
	public void redraw();
	
	/**
	 * Set the flavor text of the level.
	 * @param levelFile name of the level file loaded in.
	 */
	public void setLevelFile(String levelFile);
	
	/**
	 * Set the flavor text of the level.
	 * @param levelName name of the level loaded in
	 */
	public void setLevelName(String levelName);
	
	/**
	 * Set the flavor text of what the next level is.
	 * @param nextName name of the next level following this
	 */
	public void setNextName(String nextName);

	/**
	 * Set the flavor text for the level number.
	 * @param levelNumber
	 */
	public void setLevelNumber(int levelNumber);

	/**
	 * Set flavor text for the end-of-level quote or help text.
	 * @param endQuote
	 */
	public void setEndQuote(String endQuote);
	
	public static ILevelToOutputAdapter VoidPattern = new ILevelToOutputAdapter() {
		@Override
		public void setDimensions(int wm, int hm) {}

		@Override
		public void redraw() {}

		@Override
		public void setLevelName(String levelName) {}
		
		@Override
		public void setNextName(String nextName) {}

		@Override
		public void setLevelFile(String levelFile) {}

		@Override
		public void setLevelNumber(int levelNumber) {}

		@Override
		public void setEndQuote(String endQuote) {}
	};
}
