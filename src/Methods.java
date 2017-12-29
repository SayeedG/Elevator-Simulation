// Author: Sayeed Gulmahamad
import java.util.ArrayList;
import java.util.Random;

// This contains all simulator utility methods
public class Methods {
	private static final Random _Random = new Random();

	// Method for calculating average
	public static float Average(float[] data) {
		float total = 0;
		if (data.length == 0) {
			return total;
		}
		for (int i = 0; i < data.length; ++i) {
			total += data[i];
		}
		return total / data.length;
	}

	// Method for calculating standard deviation
	public static float StandardDeviation(float[] data,
											float average) {
 		float total = 0;
		if (data.length == 0) {
			return total;
		}
		for (int i = 0; i < data.length; ++i) {
			total += Math.pow(data[i] - average, 2);
		}
		return (float)Math.sqrt(total / data.length);
	}

	// Method for generating random float between 0 and 1 by using exponential distribution
	public static float NextFloatExponentialDistribution(float lambda) {
		if (lambda == 0) {
			return _Random.nextFloat();
		}
		return (int)(Math.ceil(-Math.log(_Random.nextDouble()) / lambda) * Constants.TIME_UNIT_CHUNKS);
	}

	// Method for generating random number from range excluding one value
	// If excluded value should be used, it must be lower than bound
	public static int NextInt(int bound,
								int excludeValue) {
		bound = excludeValue < bound ? bound - 1 : bound;
		int value = _Random.nextInt(bound);
		return value >= excludeValue ? value + 1 : value;
	}

	// Method for formating time from calculation view (with chunks) to statistic view (without them)
	public static float FormatTime(float time) {
		return time / Constants.TIME_UNIT_CHUNKS;
	}



	// Method for getting stop floor from elevator's data
	// first flag is used to know "should first or last stop floor be used"
	public static int StopFloor(ArrayList<Person> peopleWaiting,
								ArrayList<Person> peopleTraveling,
								float floor,
								boolean first) {
		// If the elevator is empty and moving, enter floor for first/last waiting person in next stop floor
		if (peopleTraveling.size() == 0) {
			return peopleWaiting.get(first ? 0 : peopleWaiting.size() - 1).EnterFloor();
		}
		// If the elevator carries people but no one is waiting for it, leave floor for first/last traveler is next
		else if (peopleWaiting.size() == 0) {
			return peopleTraveling.get(first ? 0 : peopleTraveling.size() - 1).LeaveFloor();
		}
		// If elevator carries people and is moving, check first/last waiting and traveling person
		else {
			if (Math.abs(peopleWaiting.get(first ? 0 : peopleWaiting.size() - 1).EnterFloor() - floor) > Math.abs(peopleTraveling.get(first ? 0 : peopleTraveling.size() - 1).LeaveFloor() - floor)) {
				return peopleTraveling.get(first ? 0 : peopleTraveling.size() - 1).LeaveFloor();
			} else {
				return peopleWaiting.get(first ? 0 : peopleWaiting.size() - 1).EnterFloor();
			}
		}
	}

	// Method for returning all different floors for people
	// waiting flag is used to know should "EnterFloor" or "LeaveFloor" be used
	public static ArrayList<Integer> DifferentFloors(ArrayList<Person> people,
													boolean waiting) {
		ArrayList<Integer> floors = new ArrayList<Integer>();
		if (people.size() == 0) {
			return floors;
		}
		// First persons floor is always distinct since it is the first one added
		floors.add(waiting ? people.get(0).EnterFloor() : people.get(0).LeaveFloor());
		// Check everyones floor and only add ones different, the last one added
		for (int i = 1; i < people.size(); ++i) {
			int floor = waiting ? people.get(i).EnterFloor() : people.get(i).LeaveFloor();
			if (floor != floors.get(floors.size() - 1)) {
				floors.add(floor);
			}
		}
		return floors;
	}

	// Method for returning all different floors before limit floor
	// goingUp flag is used to know should ">=" or "<=" be used for level comparison
	//		When elevator is in idle state, it cannot have any different floors
	public static ArrayList<Integer> FilterDifferentFloors(ArrayList<Integer> differentFloors,
															boolean goingUp,
															int limitFloor) {
		ArrayList<Integer> floors = new ArrayList<Integer>();
		if (differentFloors.size() == 0) {
			return floors;
		}
		for (int i = 0; i < differentFloors.size(); ++i) {
			// For every floor, check if it comes after limit floor for given direction
			int floor = differentFloors.get(i);
			// Case for going up
			if (goingUp &&
				floor >= limitFloor) {
				break;
			}
			// Case for going up
			if (!goingUp &&
				floor <= limitFloor) {
				break;
			}
			// If it is before limit floor, add it to filtered floors
			floors.add(floor);
		}
		return floors;
	}

	// Method for adding person in sorted waiting or traveling people list
	// waiting flag is used to know should "EnterFloor" or "LeaveFloor" be compared
	// goingUp flag is used to know should ">" or "<" be used for level comparison
	//		When elevator is in idle state it cannot have any people waiting or traveling
	public static void AddPersonSorted(ArrayList<Person> people,
										Person person,
										boolean waiting,
										boolean goingUp) {
		int index = 0;
		for (int i = 0; i < people.size(); ++i) {
			// First two cases are for people waiting (first going up second going down)
			if (waiting && goingUp) {
				if (people.get(i).EnterFloor() > person.EnterFloor()) {
					break;
				}
			} else if (waiting && !goingUp) {
				if (people.get(i).EnterFloor() < person.EnterFloor()) {
					break;
				}
			// Last two cases are for people traveling (first going up second going down)
			} else if (!waiting && goingUp) {
				if (people.get(i).LeaveFloor() > person.LeaveFloor()) {
					break;
				}
			} else {
				if (people.get(i).LeaveFloor() < person.LeaveFloor()) {
					break;
				}
			}
			++index;
		}
		people.add(index, person);
	}
}
