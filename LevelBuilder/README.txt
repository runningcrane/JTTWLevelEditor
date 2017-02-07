Start program:
	Run RunLevelBuilder.launch. If you need the json simple jar, it is on the root level.
	Three windows have popped up. Minimize the layer window that has nothing on it; it's not implemented yet.
	Resize the window via the resize button on the controls window. Click & Drag resizing is causing swing to lag horribly currently.
	
Add a platform: 
	Click on one of the platforms on the controls window.
	Now click somewhere on the output window (where you can see the level). Three pop-ups appear: the first asks for width, the second height, and the third its collision box.
	If you need to redo the collision box, hit the clear button. Although the points look like they're still there they've been erased.
	When done with the collision box, hit "finish".

Add/change a character's location:
	Click on the character in the control box. In the output window, click somewhere on the screen.

Change background:
	Click one of the backgrounds.

Read-in JSON: 
	*Read and Write JSON are not in sync at the moment, so if you read in JSON, it might not set all the variables needed to output a JSON file. Be careful. server/LevelManager contains the JSON writing out & reading in.

Output JSON location:
	The launch is set with /bin as its root. Look in bin for outputted JSON.


Bugs:
	swing's origin is in the upper left, while cocos's origin is in the bottom left. I have not fully mashed out the flipping of the y coordinate. Thus, some positions may seem upside down in cocos at the minute.