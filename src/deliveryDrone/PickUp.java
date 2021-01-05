package deliveryDrone;

public enum PickUp {
	NO_PICKUP(0),
	PICKUP_FROM_HOUSE1(1),PICKUP_FROM_HOUSE2(2),PICKUP_FROM_HOUSE3(3),PICKUP_FROM_HOUSE4(4),
	PICKUP_TO_HOUSE1(5),PICKUP_TO_HOUSE2(6),PICKUP_TO_HOUSE3(7),PICKUP_TO_HOUSE4(8);
	
	int index;
	
	PickUp(int i) {
		index = i;
	}
	public int getIndex() {
		return index;
	}
	
	public static PickUp parsePickUpEnum(String s) {
		switch(s) {
		case "NO_PICKUP":
			return PickUp.NO_PICKUP;
		case "PICKUP_FROM_HOUSE1":
			return PickUp.PICKUP_FROM_HOUSE1;
		case "PICKUP_FROM_HOUSE2":
			return PickUp.PICKUP_FROM_HOUSE2;
		case "PICKUP_FROM_HOUSE3":
			return PickUp.PICKUP_FROM_HOUSE3;
		case "PICKUP_FROM_HOUSE4":
			return PickUp.PICKUP_FROM_HOUSE4;
		case "PICKUP_TO_HOUSE1":
			return PickUp.PICKUP_TO_HOUSE1;
		case "PICKUP_TO_HOUSE2":
			return PickUp.PICKUP_TO_HOUSE2;
		case "PICKUP_TO_HOUSE3":
			return PickUp.PICKUP_TO_HOUSE3;
		case "PICKUP_TO_HOUSE4":
			return PickUp.PICKUP_TO_HOUSE4;
		default:
			return PickUp.NO_PICKUP;
		}
	}
}
