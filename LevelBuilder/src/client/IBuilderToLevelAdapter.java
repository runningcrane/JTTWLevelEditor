package client;

import java.awt.Component;
import java.awt.Graphics;

public interface IBuilderToLevelAdapter {
	
	public void render(Component panel, Graphics g);
	
	public void setBg(Component panel);

}
