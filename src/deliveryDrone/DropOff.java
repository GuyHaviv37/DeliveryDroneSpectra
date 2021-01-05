package deliveryDrone;

public enum DropOff {
	NO_DROPOFF(0),
	DROPOFF_AT_HOUSE1(1),DROPOFF_AT_HOUSE2(2),
	DROPOFF_AT_HOUSE3(3),DROPOFF_AT_HOUSE4(4),
	DROPOFF_AT_WH_PACKAGE(5), DROPOFF_AT_WH_ENVELOPE(6);
	
	int index;
	
	DropOff(int i) {
		index = i;
	}
	
	public int getIndex() {
		return index;
	}

	public static DropOff parseDropOffEnum(String s) {
		switch(s) {
		case "NO_DROPOFF":
			return DropOff.NO_DROPOFF;
		case "DROPOFF_AT_HOUSE1":
			return DropOff.DROPOFF_AT_HOUSE1;
		case "DROPOFF_AT_HOUSE2":
			return DropOff.DROPOFF_AT_HOUSE2;
		case "DROPOFF_AT_HOUSE3":
			return DropOff.DROPOFF_AT_HOUSE3;
		case "DROPOFF_AT_HOUSE4":
			return DropOff.DROPOFF_AT_HOUSE4;
		case "DROPOFF_AT_WH_PACKAGE":
			return DropOff.DROPOFF_AT_WH_PACKAGE;
		case "DROPOFF_AT_WH_ENVELOPE":
			return DropOff.DROPOFF_AT_WH_ENVELOPE;
		default:
			return DropOff.NO_DROPOFF;
		}
	}
}
