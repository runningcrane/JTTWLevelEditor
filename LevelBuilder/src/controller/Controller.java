package controller;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;

import org.json.simple.JSONObject;

import client.IControlToLevelAdapter;
import client.ILayerToLevelAdapter;
import client.IOutputToLevelAdapter;
import client.LayerWindow;
import client.OutputWindow;
import client.ControlWindow;
import server.LevelManager;
import server.ILevelToLayerAdapter;
import server.ILevelToOutputAdapter;
import server.ILevelToControlAdapter;

/**
 * Controller of this MVC system. Start the program here.
 * 
 * @author Melinda Crane
 */
public class Controller {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() { // Java specs say that the
												// system must be constructed on
												// the GUI event thread.
			public void run() {
				try {
					Controller controller = new Controller(); // instantiate
					// system
					controller.start(); // start the system
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Control panel gives level-editing/placement options.
	 */
	ControlWindow controlWindow;

	/**
	 * Layer panel informs which objects are on the screen and allows postedits.
	 */
	LayerWindow layerWindow;

	/**
	 * Panel on which the mock level is displayed.
	 */
	OutputWindow outputWindow;

	/**
	 * Model that manages the various parts of the Level.
	 */
	LevelManager level;

	/**
	 * Constructor for the Controller.
	 */
	public Controller() {
		controlWindow = new ControlWindow(new IControlToLevelAdapter() {
			@Override
			public void makePlatform(String path) {
				level.setActive(path);
			}

			@Override
			public void makeRock(String path) {
				level.setRockPosition(path);
			}

			@Override
			public void makeVine(String path) {
				level.setVinePosition(path);
			}

			@Override
			public void markEOL() {
				level.markEOL();
			}

			@Override
			public void setBg(String path) {
				level.setBg(path);
			}

			@Override
			public void setLevelDimensions(double wm, double hm) {
				level.setLevelDimensions(wm, hm);
			}

			@Override
			public void setMToPixel(double mToPixel) {
				level.setMToPixel(mToPixel);
			}

			@Override
			public void setViewportDimensions(double wm, double hm) {
				level.setViewportDimensions(wm, hm);
			}

			@Override
			public void togglePlayer(String name, boolean status) {
				level.togglePlayer(name, status);
			}

			@Override
			public void markRP() {
				level.markRP();				
			}

			@Override
			public void removeRP() {
				level.requestRemoveRP();				
			}

			@Override
			public void makeBoulderJoint() {
				level.makeBoulderJoint();
			}

			@Override
			public void makePeg(String path) {
				level.requestPeg(path);
			}

		});

		layerWindow = new LayerWindow(new ILayerToLevelAdapter() {

			@Override
			public void changeDimVine(int ticket, double wm, double hm) {
				level.editVineDim(ticket, wm, hm);
			}

			@Override
			public void editBoulderCenter(int ticket) {
				level.editBoulderCenter(ticket);
			}

			@Override
			public void editBoulderMass(int ticket, double mass) {
				level.editBoulderMass(ticket, mass);
			}

			@Override
			public void editBoulderScale(int ticket, double scale) {
				level.editBoulderScale(ticket, scale);
			}

			@Override
			public void editPlatCenter(int ticket) {
				level.editPlatCenter(ticket);
			}

			@Override
			public void editPlatScale(int ticket, double scale) {
				level.editPlatScale(ticket, scale);
			}

			@Override
			public void editVineArcl(int ticket, double arcl) {
				level.editVineArcl(ticket, arcl);
			}

			@Override
			public void editVineCenter(int ticket) {
				level.editVineCenter(ticket);
			}

			@Override
			public void editVineStartVel(int ticket, double startVel) {
				level.editVineStartVel(ticket, startVel);
			}

			@Override
			public void makeEndpointPlat(int ticket) {
				level.makeEndpointPlat(ticket);
			}

			@Override
			public void removePlat(int ticket) {
				level.removePlat(ticket);
			}

			@Override
			public void removeVine(int ticket) {
				level.removeVine(ticket);
			}

			@Override
			public void setPhysicsPlat(int ticket, double scK) {
				level.setPhysicsPlat(ticket, scK);
			}

			@Override
			public void setVelocityPlat(int ticket, double velocity) {
				level.setVelocityPlat(ticket, velocity);
			}

			@Override
			public void toggleClimbablePlat(int ticket, boolean selected) {
				level.toggleClimbablePlat(ticket, selected);
			}
			
			public void toggleCollidablePlat(int ticket, boolean selected) {
				level.toggleCollidablePlat(ticket, selected);
			}

			@Override
			public void toggleDisappearsPlat(int ticket, boolean selected) {
				level.toggleDisappearsPlat(ticket, selected);

			}

			@Override
			public void toggleMoveablePlat(int ticket, boolean selected) {
				level.toggleMoveablePlat(ticket, selected);
			}

			@Override
			public void togglePolygonBoulder(int ticket, boolean selected) {
				level.togglePolygonBoulder(ticket, selected);

			}

			@Override
			public void toggleSinkablePlat(int ticket, boolean selected) {
				level.toggleSinkablePlat(ticket, selected);
			}

			@Override
			public void removeBoulder(int ticket) {
				level.removeBoulder(ticket);				
			}

			@Override
			public void editBJOff(int ticket, double obx1, double oby1, double obx2, double oby2) {
				level.editBJOff(ticket, obx1, oby1, obx2, oby2);				
			}

			@Override
			public void changeBoulderJoint(int ticket, int b1, int b2) {
				level.changeBoulderJoint(ticket, b1, b2);				
			}

			@Override
			public void removeJoint(int ticket) {
				level.removeJoint(ticket);
			}

			@Override
			public void editPegScale(int ticket, double scale) {
				level.editPegScale(ticket, scale);				
			}

			@Override
			public void editPegCenter(int ticket) {
				level.editPegCenter(ticket);
			}

			@Override
			public void editPegJointID(int ticket, int jid) {
				level.editPegJointID(ticket, jid);
			}

			@Override
			public void editPegRotation(int ticket, double rotation) {
				level.editPegRotation(ticket, rotation);
			}

			@Override
			public void removePeg(int ticket) {
				level.removePeg(ticket);
			}

		});

		outputWindow = new OutputWindow(new IOutputToLevelAdapter() {
			@Override
			public void changeOffset(double xm, double ym) {
				level.changeOffset(xm, ym);
			}

			@Override
			public void editBoulderCenter(int ticket, double xp, double yp) {
				level.editBoulderCenterRes(ticket, xp, yp);

			}

			@Override
			public void editPlatCenter(int ticket, double xp, double yp) {
				level.editPlatCenterRes(ticket, xp, yp);
			}

			@Override
			public void editVineCenter(int ticket, double xp, double yp) {
				level.editVineCenterRes(ticket, xp, yp);
			}

			@Override
			public void makeBoulder(String path, double xp, double yp, double scale, int oldTicket) {
				level.makeBoulder(path, xp, yp, scale, oldTicket);
			}

			@Override
			public JSONObject makeJSON(String levelName, String nextName) {
				return level.makeJSON(levelName, nextName, false);
			}

			@Override
			public void makePlatform(String path, double xp, double yp, double scale) {
				level.makePlatform(path, xp, yp, scale);
			}

			@Override
			public void makeVine(String path, double xp, double yp, double wm, double hm, double arcl,
					double startingVel) {
				level.makeVine(path, xp, yp, wm, hm, arcl, startingVel);
			}

			@Override
			public void manualResize(double wp, double hp) {
				level.manualResize(wp, hp);
			}

			@Override
			public void readJSON(String levelPath) {
				level.readJSON(levelPath);
			}

			@Override
			public void render(Component panel, Graphics g) {
				level.render(panel, g);
			}

			@Override
			public void setEndpointPlat(int ticket, double xp, double yp) {
				level.setEndpointPlat(ticket, xp, yp);
			}

			@Override
			public void setEOL(double xp, double yp) {
				level.setEOL(xp, yp);
			}

			@Override
			public void setPlayerPosition(String name, double xp, double yp) {
				level.setCharacterPosition(name, xp, yp);
			}

			@Override
			public void setRP(double xp, double yp) {
				level.makeRP(xp, yp);				
			}

			@Override
			public void removeRP(double xp, double yp) {
				level.removeRP(xp, yp);				
			}

			@Override
			public void makeBoulderJoint(int ticket1, int ticket2, double xp, double yp) {
				level.makeBoulderJointRes(ticket1, ticket2, xp, yp);
			}

			@Override
			public void editPegCenter(int ticket, double xp, double yp) {
				level.editPegCenterRes(ticket, xp, yp);
			}

			@Override
			public void makePeg(String newPath, double xp, double yp, double radDouble, int jid, double scale) {
				level.makeGoldenPeg(newPath, xp, yp, scale, radDouble, jid);
			}

		});

		level = new LevelManager(new ILevelToControlAdapter() {

			@Override
			public void setMToPixel(double mToPixel) {
				controlWindow.setMToPixel(mToPixel);
			}

		}, new ILevelToOutputAdapter() {

			@Override
			public void makeEndpointPlat(int ticket) {
				outputWindow.setEndpointPlat(ticket);

			}

			@Override
			public void makePlatform(String path) {
				outputWindow.setActive(path);
			}

			@Override
			public void makeRock(String path) {
				outputWindow.makeBoulder(path);
			}

			@Override
			public void makeVine(String path) {
				outputWindow.makeVine(path);
			}

			@Override
			public void markEOL() {
				outputWindow.markEOL();
			}

			@Override
			public void redraw() {
				outputWindow.redraw();
			}

			@Override
			public void setBoulderPos(int ticket) {
				outputWindow.setBoulderPos(ticket);
			}

			@Override
			public void setCharPos(String playerName) {
				outputWindow.setCharPos(playerName);
			}

			@Override
			public void setDimensions(int wm, int hm) {
				outputWindow.setDimensions(wm, hm);
			}

			@Override
			public void setLevelName(String levelName) {
				outputWindow.setLevelName(levelName);
			}

			@Override
			public void setNextName(String nextName) {
				outputWindow.setNextName(nextName);

			}

			@Override
			public void setPlatPos(int ticket) {
				outputWindow.setPlatPos(ticket);
			}

			@Override
			public void setVinePos(int ticket) {
				outputWindow.setVinePos(ticket);
			}

			@Override
			public void markRP() {
				outputWindow.markRP();				
			}

			@Override
			public void requestRemoveRP() {
				outputWindow.removeRP();				
			}

			@Override
			public void makeBoulderJoint() {
				outputWindow.makeBoulderJoint();
			}

			@Override
			public void setPegPos(int ticket) {
				outputWindow.setPegPos(ticket);
			}

			@Override
			public void makePeg(String path) {
				outputWindow.makePeg(path);
			}

		}, new ILevelToLayerAdapter() {

			@Override
			public void addBoulderEdit(int ticket, double radius, double mass, double scale) {
				layerWindow.addBoulderEdit(ticket, radius, mass, scale);
			}

			@Override
			public void addPlatformEdit(int ticket, double wm, double hm, double scale) {
				layerWindow.addPlatformEdit(ticket, wm, hm, scale);
			}

			public void addVineEdit(int ticket, double wm, double hm, double arcl, double startVel) {
				layerWindow.addVineEdit(ticket, wm, hm, arcl, startVel);
			}
			
			@Override
			public void addJointsEdit(int ticket, int id1, int id2, double obx1, double oby1, double obx2, double oby2) {
				layerWindow.addJointsEdit(ticket, id1, id2, obx1, oby1, obx2, oby2);				
			}

			public void removeAllWindows() {
				layerWindow.removeAllWindows();
			}

			@Override
			public void setClimbable(int ticket, boolean selected) {
				layerWindow.setClimbable(ticket, selected);
			}
			
			public void setCollidable(int ticket, boolean selected) {
				layerWindow.setCollidable(ticket, selected);
			}

			@Override
			public void setDimensions(int ticket, double scale) {
				layerWindow.setDimensions(ticket, scale);
			}

			@Override
			public void setDisappears(int ticket, boolean selected) {
				layerWindow.setDisappears(ticket, selected);
			}

			@Override
			public void setMoveable(int ticket, boolean selected) {
				layerWindow.setMoveable(ticket, selected);
			}

			@Override
			public void setSCK(int ticket, double scK) {
				layerWindow.setSCK(ticket, scK);
			}

			@Override
			public void setSinkable(int ticket, boolean selected) {
				layerWindow.setSinkable(ticket, selected);
			}

			@Override
			public void setVelocity(int ticket, double velocity) {
				layerWindow.setVelocity(ticket, velocity);
			}

			@Override
			public void setPolygonBoulder(int ticket, boolean selected) {
				layerWindow.setPolygonBoulder(ticket, selected);				
			}

			@Override
			public void addPegEdit(int ticket, double rotation, int jid, double scale) {
				layerWindow.addPegEdit(ticket, rotation, jid, scale);
			}

		});

	}

	private void start() {

		outputWindow.start();
		level.start();
		controlWindow.start();
		layerWindow.start();
	}

}
