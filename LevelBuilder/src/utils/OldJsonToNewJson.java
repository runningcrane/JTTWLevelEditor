package utils;

import static utils.Constants.LEVELS_PATH;
import static utils.Constants.ASSETS_PATH;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import new_interactable.PropertyBook;
import new_server.ILevelToControlAdapter;
import new_server.ILevelToLayerAdapter;
import new_server.ILevelToOutputAdapter;

public class OldJsonToNewJson {
	
	/**
	 * 
	 * @param args[0]: from level file (always in old json format).
	 *        args[1]: to level file (not yet created, etc)
	 */
    @SuppressWarnings("unchecked")
	public static void main(String args[]) {
    	if (args.length < 2) {
    		System.err.println("Please pass in 2 arguments: the input file and the output file.");
    		return;
    	}
    	
    	String inputFile = args[0];
    	String outputFile = args[1];

    	String levelPath = LEVELS_PATH + inputFile;
    	
    	new_server.LevelManager lm = new new_server.LevelManager(ILevelToControlAdapter.VoidPattern, ILevelToOutputAdapter.VoidPattern, ILevelToLayerAdapter.VoidPattern);
    	
    	/////////////// LEGACY CODE /////////////////////
    	JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(new FileReader(levelPath));
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + levelPath);
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.out.println("Illegal path: " + levelPath);
			e.printStackTrace();
			return;
		} catch (ParseException e) {
			System.out.println("Cannot parse JSON at: " + levelPath);
			e.printStackTrace();
			return;
		}

		// Begin parsing
		lm.clearAndSet();

		JSONObject level = (JSONObject) obj;
		String name = (String) level.get("levelName");
		// Default case.
		if (name == null) {
			name = "TODO";
		}
    
		lm.setLevelName(name);

		String nextName = (String) level.get("nextLevelName");
		// Default case.
		if (nextName == null) {
			nextName = "TODO";
		}
		
		lm.setNextName(nextName);

		JSONObject bg = (JSONObject) level.get("background");
		// Default case.
		if (bg == null) {
			bg = new JSONObject();
			bg.put("imageName", "TODO");
			bg.put("width", 0);
			bg.put("height", 0);
		}
		double lvwm = (double) bg.get("width");
		double lvhm = (double) bg.get("height");
		
		lm.setBg(ASSETS_PATH + (String) bg.get("imageName"));
		lm.setLevelDimensions(lvwm, lvhm);
	
		Double eolXMD = (Double) level.get("levelEndX");
		double eolXM;
		// Default case.
		if (eolXMD == null) {
			eolXM = 14;
		} else {
			eolXM = eolXMD.doubleValue();
		}

		Double eolYMD = (Double) level.get("levelEndY");
		double eolYM;
		// Default case.
		if (eolYMD == null) {
			eolYM = 14;
		} else {
			eolYM = eolYMD.doubleValue();
		}

		lm.setEOL(new Point2D.Double(eolXM, eolYM));

		Boolean polygon = (Boolean) level.get("polygonCollision");
		// Default case.
		if (polygon == null) {
			polygon = false;
		}
		System.out.println("Collision: " + polygon);

		JSONArray plats = (JSONArray) level.get("platforms");
		// Default case.
		if (plats == null) {
			plats = new JSONArray();
		}
		
		for (Object o : plats) {
            addPlatform((JSONObject) o, lm, polygon);
		}

		JSONArray vines = (JSONArray) level.get("vines");
		// Default case.
		if (vines != null) {
			vines.forEach((v) -> addVine((JSONObject)v, lm)); 
		}

		JSONArray boulders = (JSONArray) level.get("boulders");
		// Default case.
		if (boulders != null) {
			boulders.forEach((b) -> addBoulder((JSONObject)b, lm));
		}
		
		/*
		JSONArray joints = (JSONArray) level.get("boulderJoints");
		// Default case.
		if (joints == null) {
			joints = new JSONArray();
		}
		readBoulderJointJSON(joints);
		*/
		
		JSONArray pegs = (JSONArray) level.get("goldenPegs");
		// Default case.
		if (pegs == null) {
			pegs = new JSONArray();
		}
		readInPegs(pegs, lm);

		JSONArray rps = (JSONArray) level.get("respawnPoints");
		// Default case.
		if (rps == null) {
			rps = new JSONArray();
		}
		makeRPs(rps, lm);
		
		JSONObject characters = (JSONObject) level.get("characters");
		// Default case: don't change any of the characters' positions, etc if
		// no info found.
		if (characters != null) {
			setCharacters(characters, lm);
		}				

		// Resize last
		Double mToPixelD = (Double) level.get("mToPixel");
		double mToPixel;
		// Default case.
		if (mToPixelD == null) {
			mToPixel = 100;
		} else {
			mToPixel = mToPixelD.doubleValue();
		}
		lm.setMToPixel(mToPixel);
    	
		lm.makeJSON(LEVELS_PATH + outputFile, nextName);
		
    }
    
    public static void addPlatform(JSONObject plat, new_server.LevelManager lm, boolean polygon) {
		// Collision box array
		List<Point2D.Double> points = new ArrayList<Point2D.Double>();

		// Further parsing here
		String path = ASSETS_PATH + (String) plat.get("imageName");
		double cxm = (double) plat.get("centerX");
		double cym = (double) plat.get("centerY");

		// The width and height are already scaled.
		double swm = (double) plat.get("imageSizeWidth");
		double shm = (double) plat.get("imageSizeHeight");

		Double scaleD = (Double) plat.get("scale");
		double scale;
		if (scaleD == null) {
			scale = 1;
		} else {
			scale = scaleD.doubleValue();
		}
		
		if (polygon) {
			JSONArray collisionPoints = (JSONArray) plat.get("collisionPoints");

			// Get the points out of the array
			for (Object p : collisionPoints) {
				JSONObject point = (JSONObject) p;
				points.add(new Point2D.Double((double) point.get("x"), (double) point.get("y")));
			}
		} else {
			// Box collision.
			double collisionWidth = (double) plat.get("collisionWidth");
			double collisionHeight = (double) plat.get("collisionHeight");

			JSONArray collisionPoints = (JSONArray) plat.get("collisionPoints");
			// Default case.
			if (collisionPoints == null) {
				// Use the collisionWidth and collisionHeight to make the
				// points.
				points.add(new Point2D.Double(0 - collisionWidth / 2, 0 - collisionHeight / 2));
				points.add(new Point2D.Double(collisionWidth / 2, collisionHeight / 2));
			} else {
				for (Object p : collisionPoints) {
					JSONObject point = (JSONObject) p;
					points.add(new Point2D.Double((double) point.get("x"), (double) point.get("y")));
				}
			}
		}

		boolean disappears = (boolean) plat.get("disappears");
		boolean moveable = (boolean) plat.get("moveable");
		boolean sinkable = (boolean) plat.get("sinkable");
		boolean climbable = (boolean) plat.get("climbable");
		
		Boolean collidable = (Boolean) plat.get("collidable");
		if (collidable == null) {
			collidable = true;
		}

		double scK = (double) plat.get("springCK");
		double velocity = (double) plat.get("velocity");

		double endX = (double) plat.get("endX");
		double endY = (double) plat.get("endY");

        PropertyBook pb = new PropertyBook();
		pb.getDoubList().put("Scale", scale);
	    pb.getDoubList().put("Velocity", velocity);
	    pb.getDoubList().put("Spring Constant K", scK);
		
		pb.getCollPoints().addAll(points);

		pb.getBoolList().put("Collidable", collidable);
		pb.getBoolList().put("Polygon collision", polygon);
		pb.getBoolList().put("Moving",  moveable);
		pb.getBoolList().put("Climbable",  climbable);
		pb.getBoolList().put("Sinkable",  sinkable);
		pb.getBoolList().put("Disappears", disappears);
		
        int tick= lm.makeInteractable(path, pb, cxm, cym, "Platform");
        lm.setEndPoint(new Point2D.Double(endX,  endY), tick);
    }
    
    public static void addVine(JSONObject vine, new_server.LevelManager lm) {
		// Further parsing here
		String path = (String) vine.get("imageName");
		// Default case.
		if (path == null) {
			path = ASSETS_PATH + "vine1.png";
		} else {
			path = ASSETS_PATH + path;
		}

		Double cxmD = (Double) vine.get("swingCenterX");
		double cxm;
		// Default case.
		if (cxmD == null) {
			cxm = 3;
		} else {
			cxm = cxmD.doubleValue();
		}

		Double cymD = (Double) vine.get("swingCenterY");
		double cym;
		// Default case.
		if (cymD == null) {
			cym = 3;
		} else {
			cym = cymD.doubleValue();
		}

		Double wmD = (Double) vine.get("width");
		double wm;
		// Default case.
		if (wmD == null) {
			wm = 0.7;
		} else {
			wm = wmD.doubleValue();
		}

		Double hmD = (Double) vine.get("length");
		double hm;
		// Default case.
		if (hmD == null) {
			hm = 3;
		} else {
			hm = hmD.doubleValue();
		}

		Double arcLimitD = (Double) vine.get("arcLimit");
		double arcLimit;
		// Default case.
		if (arcLimitD == null) {
			arcLimit = 180;
		} else {
			arcLimit = arcLimitD.doubleValue();
		}

		Double startingVelD = (Double) vine.get("startingVelocity");
		double startingVel;
		// Default case.
		if (startingVelD == null) {
			startingVel = 0;
		} else {
			startingVel = startingVelD.doubleValue();
		}
		PropertyBook pb = new PropertyBook();
		pb.getDoubList().put("Velocity", startingVel);
		pb.getDoubList().put("Arc Length", arcLimit);		
		pb.getDoubList().put("Length", hm);
		pb.getDoubList().put("Width",  wm);
		
		lm.makeInteractable(path, pb, cxm, cym, "Vine");
    }
    
    public static void addBoulder(JSONObject boulder, new_server.LevelManager lm) {
    	// Collision box array
    	ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();

    	// Further parsing here
    	String path = ASSETS_PATH + (String) boulder.get("imageName");
    	double cxm = (double) boulder.get("centerX");
    	double cym = (double) boulder.get("centerY");

    	// The width and height are already scaled.
    	double swm = (double) boulder.get("imageSizeWidth");
    	double shm = (double) boulder.get("imageSizeHeight");

    	Double scaleD = (Double) boulder.get("scale");
    	double scale;
    	if (scaleD == null) {
    		scale = 1;
    	} else {
    		scale = scaleD.doubleValue();
    	}
    				
    	Double massD = (Double) boulder.get("mass");
    	double mass;
    	if (massD == null) {
    		mass = 1000;
    	} else {
    		mass = massD.doubleValue();
    	}	
    	
    	Double rotation = (Double) boulder.get("rotation");
    	if (rotation == null) {
    		rotation = 0.0;
    	} else {
    		rotation = rotation.doubleValue();
    	}	
    			
    	JSONArray collisionPoints = (JSONArray) boulder.get("collisionPoints");

    	// Get the points out of the array
    	for (Object p : collisionPoints) {
    	    JSONObject point = (JSONObject) p;
    	    points.add(new Point2D.Double((double) point.get("x"), (double) point.get("y")));
    	} 
    				
    	// Get the old ticket value, if it exists.
    	Long ticketL = (Long)(boulder.get("ticket"));			
    	int oldTicket;
    	if (ticketL == null) {
    		oldTicket = -1;
    	} else {
    		oldTicket= ticketL.intValue();
    	}

    	String type = (String) boulder.get("type");
    	boolean polygon = (type.toLowerCase().equals("polygon")) ? true : false;

    	// makePlatform takes swing coordinates, so m is translated to px
    	// and y is flipped.
    	PropertyBook pb = new PropertyBook();
    	pb.getDoubList().put("Mass", mass);
    	pb.getDoubList().put("Radius", 1.0);
    	
    	pb.getCollPoints().addAll(points);
    	pb.getBoolList().put("Polygon collision", polygon);
    	
    	pb.getDoubList().put("Rotation (rad)", rotation);
    	
    	lm.makeBoulder(path, pb, cxm, cym, oldTicket);
    }
    
    public static void readInPegs(JSONArray pegs, new_server.LevelManager lm) {
    	for (Object o : pegs) {
    		JSONObject peg = (JSONObject) o;
    		JSONArray bouldersAffected = (JSONArray)peg.get("bouldersAffected");
            
    		PropertyBook pb = new PropertyBook();
    		
    		int bNum = 0;
    		
    		for (Object o2 : bouldersAffected) {
    			bNum += 1;
    			int bTicket = Math.toIntExact((long) o2);
    			int newTicket = lm.getNewBoulderTicketFromOld(bTicket);
    			if (newTicket != -1) {
    				// TODO: HOW THE HELL DO WE STORE MULTIPLE BOULDERS?
    			    // ?????
    				// Giving each a unique id string for now.
    				pb.getIntList().put("Boulder ID " + bNum, newTicket);
    			}
    		}
		
			String imageName = (String)peg.get("imageName");
			double centerXm = (double)peg.get("centerX");
			double centerYm = (double)peg.get("centerY");
			double rotation = (double)peg.get("rotation");
		    double scale = (double)peg.get("scale");
			//double imageWidth = (double)peg.get("imageWidth");
			//double imageHeight = (double)peg.get("imageHeight");			
			
			pb.getDoubList().put("Rotation", rotation);
			lm.makeInteractable(ASSETS_PATH + imageName, pb, centerXm, centerYm, "Peg");	
    	}
    }
    
	@SuppressWarnings("unchecked")
	public static void makeRPs(JSONArray list, new_server.LevelManager lm) {
		list.forEach((obj) -> {
			JSONObject jsonObj = (JSONObject) obj;
			double x = (double)jsonObj.get("x");
			double y = (double)jsonObj.get("y");
			lm.addRP(new Point2D.Double(x, y));
		});
	}
	
	public static void setCharacters(JSONObject chars, new_server.LevelManager lm) {
		JSONObject monkey = (JSONObject) chars.get("Monkey");
		// Default case: If monkey not found, don't change the current monkey at
		// all.
		if (monkey != null) {
			Double cxmD = (Double) monkey.get("startingXPos");
			double cxm;
			// Default case.
			if (cxmD == null) {
				cxm = 0;
			} else {
				cxm = cxmD.doubleValue();
			}

			Double cymD = (Double) monkey.get("startingYPos");
			double cym;
			// Default case.
			if (cymD == null) {
				cym = 0;
			} else {
				cym = cymD.doubleValue();
			}

			Boolean present = (Boolean) monkey.get("present");
			// Default case.
			if (present == null) {
				present = false;
			}
			lm.getCharacter("Monkey").setCenter(cxm, cym);
			lm.getCharacter("Monkey").setPresent(present);
		}

		JSONObject monk = (JSONObject) chars.get("Monk");
		// Default case: If monk not found, don't change the current monk at
		// all.
		if (monk != null) {
			Double monkcxmD = (Double) monk.get("startingXPos");
			double monkcxm;
			// Default case.
			if (monkcxmD == null) {
				monkcxm = 0;
			} else {
				monkcxm = monkcxmD.doubleValue();
			}

			Double monkcymD = (Double) monk.get("startingYPos");
			double monkcym;
			// Default case.
			if (monkcymD == null) {
				monkcym = 0;
			} else {
				monkcym = monkcymD.doubleValue();
			}

			Boolean monkpresent = (Boolean) monk.get("present");
			// Default case.
			if (monkpresent == null) {
				monkpresent = false;
			}

			lm.getCharacter("Monk").setCenter(monkcxm, monkcym);
			lm.getCharacter("Monk").setPresent(monkpresent);
		}

		JSONObject pig = (JSONObject) chars.get("Piggy");
		// Default case: If pig not found, don't change the current pig at all.
		if (pig != null) {
			// TODO: defaults
			double pigcxm = (double) pig.get("startingXPos");
			double pigcym = (double) pig.get("startingYPos");
			boolean pigpresent = (boolean) pig.get("present");
			lm.getCharacter("Piggy").setCenter(pigcxm, pigcym);
			lm.getCharacter("Piggy").setPresent(pigpresent);
		}

		JSONObject sandy = (JSONObject) chars.get("Sandy");
		// Default case: If sandy not found, don't change the current sandy at
		// all.
		if (sandy != null) {
			// TODO: defaults
			double sandycxm = (double) sandy.get("startingXPos");
			double sandycym = (double) sandy.get("startingYPos");
			boolean sandypresent = (boolean) sandy.get("present");
			lm.getCharacter("Sandy").setCenter(sandycxm, sandycym);
			lm.getCharacter("Sandy").setPresent(sandypresent);
		}
	}
}


