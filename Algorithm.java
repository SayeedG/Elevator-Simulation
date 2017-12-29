// Author: Sayeed Gulmahamad
import java.util.ArrayList;

// This implements the entire logic for the Elevator-Person relationship
public class Algorithm {
	private float _Time;
	private float _TimeUntilPersonCreate;
	private int _NumberOfFloors;
	private int _PeopleToCreate;

	private ArrayList<Elevator> _Elevators;
	private ArrayList<Person> _PeoplePending;

	public Algorithm(int numberOfFloors,
					int numberOfElevators,
					int numberOfPeople) {
		// Set everything up
		_Time = 0;
		_TimeUntilPersonCreate = 0;
		_NumberOfFloors = numberOfFloors;
		_PeopleToCreate = numberOfPeople;
		_Elevators = new ArrayList<Elevator>();
		for (int i = 0; i < numberOfElevators; ++i) {
			_Elevators.add(new Elevator());
		}
		_PeoplePending = new ArrayList<Person>();
	}

	// Starts entire simulation
	public void Run() {
		float deltaTime = Process();
		while (Update(deltaTime)) {
			deltaTime = Process();
		}
	}

	// Process everything and returns time until next event
	private float Process() {
		// If it is time to create new person, do it
		if (_TimeUntilPersonCreate == 0) {
			CreatePerson();
		}
		// In any case, process pending/waiting people and update afterwards
		// People that are left as pending will wait next event
		ProcessPeople();
		return ProcessTime();
	}

	// Update simulates passing time returned from Process in all components
	private boolean Update(float deltaTime) {
		if (deltaTime == Constants.INVALID_TIME) {
			return false;
		}
		// Only process person creation time if more people need to be created
		if (_TimeUntilPersonCreate != Constants.INVALID_TIME) {
			_TimeUntilPersonCreate -= deltaTime;
		}
		for (int i = 0; i < _Elevators.size(); ++i) {
			_Elevators.get(i).Update(_Time, deltaTime);
		}
		_Time += deltaTime;
		return true;
	}

	private void CreatePerson() {
		int enterFloor = Methods.NextInt(_NumberOfFloors, _NumberOfFloors);
		int leaveFloor = Methods.NextInt(_NumberOfFloors, enterFloor);
		Person person = new Person(_Time,
									enterFloor,
									leaveFloor);
		// Everyone is put in people pending and the algorithm tries to assign them to elevator
		_PeoplePending.add(person);
		// Set time for creating the next person and decrease people as needed
		// If no more people should be created, invalidate creation time
		--_PeopleToCreate;
		if (_PeopleToCreate == 0) {
			_TimeUntilPersonCreate = Constants.INVALID_TIME;
		} else {
			_TimeUntilPersonCreate = Methods.NextFloatExponentialDistribution(_NumberOfFloors);
		}
	}

	// ****************************************************************************************************
	// PEOPLE PROCESSING
	private void ProcessPeople() {
		// Create local copy of all elevators since it will be filtered
		ArrayList<Elevator> elevators = new ArrayList<Elevator>(_Elevators);
		int count = _PeoplePending.size();
		int index = 0;
		for (int i = 0; i < count; ++i) {
			// Filter stoppable elevators prior to processing anyone
			FilterElevators(elevators);
			if (elevators.isEmpty()) {
				return;
			}
			Person person = _PeoplePending.get(index);
			// Pair every person against every stoppable elevator and chose best match, if any
			Elevator bestElevator = null;
			float bestTime = Constants.INVALID_VALUE;
			for (int j = 0; j < elevators.size(); ++j) {
				Elevator elevator = elevators.get(j);
				// Check if an elevator and person can be assigned to each other
				if (CanAssign(elevator, person)) {
					// Find best assignable pair, if any
					float time = CalculateTime(elevator, person);
					if (bestTime == Constants.INVALID_VALUE ||
						time < bestTime) {
						bestElevator = elevator;
						bestTime = time;
					}
				}
			}
			// If there is any match, assign person to best matching elevator
			if (bestElevator != null) {
				bestElevator.AddWaitingPerson(_PeoplePending.remove(index),
												_Time);
			} else {
				++index;
			}
		}
	}

	// This removes elevators that cannot stop, from all elevators
	private void FilterElevators(ArrayList<Elevator> elevators) {
		int count = elevators.size();
		int index = 0;
		for (int i = 0; i < count; ++i) {
			if (!elevators.get(index).CanStop(null)) {
				elevators.remove(index);
			} else {
				++index;
			}
		}
	}

	// This checks if a person can be assigned to an elevator
	private boolean CanAssign(Elevator elevator,
								Person person) {
		// If an elevator is not going anywhere, it can be assigned
		if (elevator.IsStopped()) {
			return true;
		}
		// If an elevator cannot stop, it cannot be assigned
		if (!elevator.CanStop(person)) {
			return false;
		}
		// Check if an elevator and person are going in same direction
		if (elevator.Direction() != person.Direction()) {
			return false;
		}
		// If the directions match, check if the elevator has already passed the person
		// Case for going up
		if (elevator.Direction() == 1 &&
			elevator.Floor() > person.EnterFloor()) {
			return false;
		}
		// Case for going down
		if (elevator.Direction() == -1 &&
			elevator.Floor() < person.EnterFloor()) {
			return false;
		}
		// In all other cases, assigning is possible
		return true;
	}

	// This calculates the time it would take for elevator to collect a person
	// This method is only called for assignable pairs
	private float CalculateTime(Elevator elevator,
								Person person) {
		float time = 0;
		// Add moving time
		time += Math.abs(person.EnterFloor() - elevator.Floor()) * Constants.ELEVATOR_FLOOR_TIME;
		// Add time for people entering before person
		time += elevator.WaitStopFloorsBeforeEnter(person).size() * Constants.ELEVATOR_ENTER_TIME;
		// Add time for people entering before person
		time += elevator.WaitStopFloorsBeforeEnter(person).size() * Constants.ELEVATOR_LEAVE_TIME;
		return time;
	}
	// ****************************************************************************************************

	// ****************************************************************************************************
	// TIME PROCESSING
	private float ProcessTime() {
		// After processing is over, determine when will the next event occur
		// There are two kinds of events:
		//	1.New person creation
		//	2.Elevator state change
		// The important thing is to always take the least amount of time out of all
		// If invalid time is returned, that means simulation is over
		float deltaTime = _TimeUntilPersonCreate;
		for (int i = 0; i < _Elevators.size(); ++i) {
			float elevatorDeltaTime = _Elevators.get(i).DeltaTime(_Time);
			if (elevatorDeltaTime < deltaTime) {
				deltaTime = elevatorDeltaTime;
			}
		}
		return deltaTime;
	}
	// ****************************************************************************************************
}
