// Author: Sayeed Gulmahamad
// This contains all simulator constants
public class Constants {
	// General constants
	public static final int INVALID_VALUE = -1;
	public static final float INVALID_TIME = Integer.MAX_VALUE;
	// Because of float's unpredictable behavior (because it is a real number), time has a fixed minimum unit
	public static final int TIME_UNIT_CHUNKS = 100;

	// Elevator constants
	public static final float ELEVATOR_ENTER_TIME = 1 * TIME_UNIT_CHUNKS;
	public static final float ELEVATOR_LEAVE_TIME = 1 * TIME_UNIT_CHUNKS;
	public static final float ELEVATOR_FLOOR_TIME = 2 * TIME_UNIT_CHUNKS;

	// Simulation constants
	public static final int NUMBER_OF_FLOORS = 10;
	public static final int NUMBER_OF_ELEVATORS = 4;
	public static final int NUMBER_OF_PEOPLE = 500000;
	public static final int NUMBER_OF_SIMULATIONS = 2;
}
