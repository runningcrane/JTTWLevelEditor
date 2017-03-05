package interactable;

import java.awt.geom.Point2D;

import org.json.simple.JSONObject;

public class BoulderJoint {
	
	int oldID;
	
	int newID;

	int ticket1;
	
	int ticket2;
	
	/**
	 * In [cocos,m].
	 */
	double offsetB1X;
	
	/**
	 * In [cocos,m].
	 */
	double offsetB1Y;
	
	/**
	 * In [cocos,m].
	 */
	double offsetB2X;
	
	/**
	 * In [cocos,m].
	 */
	double offsetB2Y;
	
	public BoulderJoint(int oldID, int ticket1, int ticket2, double ob1x, double ob1y, double ob2x, double ob2y) {
		this.oldID = oldID;
		this.ticket1 = ticket1;
		this.ticket2 = ticket2;		
		this.offsetB1X = ob1x;
		this.offsetB1Y = ob1y;
		this.offsetB2X = ob2x;
		this.offsetB2Y = ob2y;
		System.out.println("New boulder joint made between boulders " + ticket1 + " and " + ticket2);
	}
	
	public int getBoulder1() {
		return this.ticket1;
	}
	
	public int getBoulder2() {
		return this.ticket2;
	}
	
	public int getOldID() {
		return this.oldID;
	}
	
	public int getNewID() {
		return this.newID;
	}
	
	public void setNewID(int newID) {
		this.newID = newID;
	}
	
	public void setBoulder1(int newB1) {
		this.ticket1 = newB1;
	}
	
	public void setBoulder2(int newB2) {
		this.ticket2 = newB2;
	}
	
	public void setOffsetB1(double x, double y) {
		this.offsetB1X = x;
		this.offsetB1Y = y;
	}
	
	public void setOffsetB2(double x, double y) {
		this.offsetB2X = x;
		this.offsetB2Y = y;
	}
	
	public JSONObject makeJSON() {
		JSONObject obj = new JSONObject();
		this.oldID = this.newID;
		obj.put("jointID",  this.oldID);
		obj.put("id1", this.ticket1);
		obj.put("id2", this.ticket2);
		obj.put("anchor1x", offsetB1X);
		obj.put("anchor1y", offsetB1Y);
		obj.put("anchor2x", offsetB2X);
		obj.put("anchor2y", offsetB2Y);
		return obj;
	}
}
