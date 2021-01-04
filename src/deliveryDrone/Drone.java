package deliveryDrone;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Drone {
	private int x;
	private int y;
	private int droneSize = 125;
	private BufferedImage droneImg;
	private int[] location = new int[] { 3, 3 }; // This specifies the starting location
	private int SPEED = 10; // slow = 10, fast = 30 .SPEED = 10 means 15 steps for a next location
	private int STOCK_FRAMES = 7; // slow = 7, fast - 2/3
	private DroneAnimationState droneState = DroneAnimationState.IDLE;
	private int[] stopPosition = new int[] { 3, 3 };
	/*
	 * DIRECTION GOES CLOCK-WISE: (3) UP - ADD 1 (2) RIGHT - ADD 10 (1) DOWN - ADD
	 * 100 (0) LEFT - ADD 1000 0 STAY PUT (NONE) NORTH : 0001, NORTH-EAST: 0011,
	 * EAST: 0010, SOUTH-EAST: 0110, SOUTH: 0100 , SOUTH-WEST: 1100, WEST: 1000,
	 * NORTH-WEST: 1001
	 */
	private int direction = 0;
	private int currStockFrame = 0;

	private static final int SQUARE_SIZE = 150;
	int[] chargingStationLocation;

	public Drone(int[] chargingStationLocation) {
		this.chargingStationLocation = chargingStationLocation;
		
		try {
			droneImg = ImageIO.read(new File("img/drone.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Setup x and y , notice location[0] is row goes with y, location[1] goes with x
		this.y = this.location[0] * SQUARE_SIZE;
		this.x = this.location[1] * SQUARE_SIZE;
		calcStopPosition();
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getSize() {
		return this.droneSize;
	}

	public BufferedImage getImage() {
		return this.droneImg;
	}

	public boolean isMoving() {
		return (droneState == DroneAnimationState.MOVING);
	}

	public boolean isStocking() {
		return (droneState == DroneAnimationState.STOCKING);
	}

	public void toggleMoving(boolean toMove, boolean isStocking) {
		if (toMove) {
			this.droneState = DroneAnimationState.MOVING;
		} else {
			if (isStocking) {
				this.droneState = DroneAnimationState.STOCKING;
			} else {
				this.droneState = DroneAnimationState.IDLE;
			}
		}
	}

	public boolean shouldMakeStop() {
		if (this.x == this.stopPosition[0] && this.y == this.stopPosition[1]) {
			return true;
		}
		return false;
	}

	public void setNewDestination(int newRow, int newCol) {
		calcDirection(newRow, newCol);
		this.location[0] = newRow;
		this.location[1] = newCol;
		calcStopPosition();
	}

	public void move() {
		switch (this.direction) {
		case 1:
			this.y -= SPEED;
			break;
		case 11:
			this.y -= SPEED;
			this.x += SPEED;
			break;
		case 10:
			this.x += SPEED;
			break;
		case 110:
			this.x += SPEED;
			this.y += SPEED;
			break;
		case 100:
			this.y += SPEED;
			break;
		case 1100:
			this.x -= SPEED;
			this.y += SPEED;
			break;
		case 1000:
			this.x -= SPEED;
			break;
		case 1001:
			this.x -= SPEED;
			this.y -= SPEED;
			break;
		default:
			System.out.println("Something went wrong moving...");
			break;
		}

	}

	public boolean stock() {
		this.currStockFrame++;
		if (this.currStockFrame == STOCK_FRAMES) {
			this.currStockFrame = 0;
			return true;
		}
		return false;
	}

	private void calcStopPosition() {
		this.stopPosition[0] = this.location[1] * SQUARE_SIZE;
		this.stopPosition[1] = this.location[0] * SQUARE_SIZE;
	}

	private void calcDirection(int newRow, int newCol) {
		int newDir = 0;
		if (newCol > location[1]) {
			// Move EAST
			newDir += 10;
		} else if (newCol < location[1]) {
			// Move WEST
			newDir += 1000;
		}

		if (newRow > location[0]) {
			// Move SOUTH
			newDir += 100;
		} else if (newRow < location[0]) {
			// MOVE NORTH
			newDir += 1;
		}

		this.direction = newDir;

	}
	
	public boolean isCharging() {
		return this.location[0] == this.chargingStationLocation[0] && this.location[1] == this.chargingStationLocation[1];
	}
	
	public boolean isAtWarehouse(){
		return this.location[0] == 3 && this.location[1] == 3;
	}

	public void setTurboMode(boolean faster) {
		if(faster) {
			this.SPEED = 30;
			this.STOCK_FRAMES = 2;
		} else {
			this.SPEED = 10;
			this.STOCK_FRAMES = 7;
		}
	}
	
	/* DEBUGGING */
	@Override
	public String toString() {
		return "Drone [x=" + x + ", y=" + y + ", location=" + Arrays.toString(location) + ", droneState=" + droneState
				+ ", stopPosition=" + Arrays.toString(stopPosition) + ", direction=" + direction + "]";
	}
	
	public int getStockFrame() {
		return currStockFrame;
	}
}

enum DroneAnimationState {
	IDLE, MOVING, STOCKING
}
