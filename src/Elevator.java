//Author: Sayeed Gulmahamad
import java.util.ArrayList;

// This represents a single elevator taking people from one floor to another
// It is an active component in the Elevator-Person relationship processed by the algorithm

// The elevator keeps track of which floor it is on and in which direction it is moving
// Floor is a float because an event occurs while elevator is moving in most occasions
// Direction can be -1, 0 or 1 if it is moving down, not moving or moving up respectively
// Elevator also has a state and all states and their transitions are described in enum
// States are used for various getters checks and possible transitions to other states

// Another thing elevator keeps track of are the state updates and time when it happens

// Elevator also keeps track of people it carries and of ones that are waiting for it
// People that are traveling are sorted so the ones leaving first are on first positions
// People that are waiting are sorted so the ones entering first are on first positions
public class Elevator {
	public static final float SPEED = 1 / Constants.ELEVATOR_FLOOR_TIME;

	private float _Floor = 0;
	private int _Direction = 0;
	private ElevatorState _State = ElevatorState.Idle;
	private float _LastStateChangeTime = 0;

	private ArrayList<Person> _PeopleWaiting = new ArrayList<Person>();
	private ArrayList<Person> _PeopleTraveling = new ArrayList<Person>();

	public Elevator() {}

	public float Floor() {
		return _Floor;
	}
	public int Direction() {
		return _Direction;
	}

	// This returns if elevator is stopped or not
	public boolean IsStopped() {
		return _State == ElevatorState.Idle;
	}
	// This returns if an elevator can be stopped for anyone
	// The elevator can stop for a person with same enter floor and direction as first waiting person
	// To check if an elevator can be stopped without special case, pass null as parameter
	public boolean CanStop(Person person) {
		boolean isStoppable = _State != ElevatorState.MovingUnstoppable;
		if (person == null) {
			return isStoppable;
		}
		boolean floorAndDirectionMatch = false;
		if (_PeopleWaiting.size() > 0) {
			floorAndDirectionMatch = _PeopleWaiting.get(0).EnterFloor() == person.EnterFloor() &&
										_PeopleWaiting.get(0).Direction() == person.Direction();
		}
		return isStoppable || floorAndDirectionMatch;
	}
	// This returns which floor is the first one on which the elevator will stop
	public int FirstStopFloor() {
		// If elevator is empty and not moving it is already stopped
		if (_State == ElevatorState.Idle) {
			return Constants.INVALID_VALUE;
		}
		else {
			return Methods.StopFloor(_PeopleWaiting,
										_PeopleTraveling,
										_Floor,
										true);
		}
	}
	// This returns which floor is the last one on which elevator will stop
	// When talking about states, this is floor on which it will be idle
	public int LastStopFloor() {
		// If elevator is empty and not moving it is already stopped
		if (_State == ElevatorState.Idle) {
			return Constants.INVALID_VALUE;
		}
		else {
			return Methods.StopFloor(_PeopleWaiting,
										_PeopleTraveling,
										_Floor,
										false);
		}
	}
	public boolean IsNextPersonWaitingOnFloor(int floor) {
		return !_PeopleWaiting.isEmpty() && _PeopleWaiting.get(0).EnterFloor() == floor;
	}
	public boolean IsNextPersonTravelingToFloor(int floor) {
		return !_PeopleTraveling.isEmpty() && _PeopleTraveling.get(0).LeaveFloor() == floor;
	}

	// This returns all stop floors for people that are waiting
	public ArrayList<Integer> WaitStopFloors() {
		return Methods.DifferentFloors(_PeopleWaiting,
										true);
	}
	// This returns all stop floors for people that are traveling
	public ArrayList<Integer> TravelStopFloors() {
		return Methods.DifferentFloors(_PeopleTraveling,
										false);
	}
	// This returns all stop floors for people that are waiting before given person enter floor
	public ArrayList<Integer> WaitStopFloorsBeforeEnter(Person person) {
		return Methods.FilterDifferentFloors(WaitStopFloors(),
												_Direction == 1,
												person.EnterFloor());
	}
	// This returns all stop floors for people that are traveling before given person enter floor
	public ArrayList<Integer> TravelStopFloorsBeforeEnter(Person person) {
		return Methods.FilterDifferentFloors(TravelStopFloors(),
												_Direction == 1,
												person.EnterFloor());
	}
	// This adds person in people waiting
	// Time is passed when going from Idle to MovingUnstoppable
	public void AddWaitingPerson(Person person,
									float time) {
		Methods.AddPersonSorted(_PeopleWaiting,
								person,
								true,
								_Direction == 1);
		// Since idle state has to be changed instantly, do it here
		// It is needed instantly because of elevator filtering
		if (_State == ElevatorState.Idle) {
			_State = ElevatorState.MovingUnstoppable;
			_Direction = _Floor > person.EnterFloor() ? -1 : 1;
			_LastStateChangeTime = time;
		}
	}
	// This adds person in people traveling
	public void AddTravelingPerson(Person person,
									float time) {
		Methods.AddPersonSorted(_PeopleTraveling,
								person,
								false,
								_Direction == 1);
		person.EnterElevator(time);
	}

	// This updates the elevator states through transitions
	public void Update(float lastTime,
						float deltaTime) {
		switch (_State) {
		// Idle has only transition to MovingUnstoppable which is done in AddWaitingPerson
		case Idle:
			break;
		// MovingUnstoppable has only transition to PeopleEntering
		case MovingUnstoppable:
			_Floor += _Direction * deltaTime * SPEED;
			if (_Floor == FirstStopFloor()) {
				_State = ElevatorState.PeopleEntering;
				_LastStateChangeTime = lastTime + deltaTime;
			}
			break;
		// PeopleEntering has only transition to MovingStoppable
		// Check if enough time has passed for people to finish entering
		case PeopleEntering:
			if (lastTime + deltaTime - _LastStateChangeTime == Constants.ELEVATOR_ENTER_TIME) {
				while (!_PeopleWaiting.isEmpty() &&
						_PeopleWaiting.get(0).EnterFloor() == _Floor) {
					AddTravelingPerson(_PeopleWaiting.remove(0), _LastStateChangeTime);
				}
				_State = ElevatorState.MovingStoppable;
				_Direction = _Floor > FirstStopFloor() ? -1 : 1;
				_LastStateChangeTime = lastTime + deltaTime;
			}
			break;
		// MovingStoppable is stopped for two reasons: people entering and people leaving
		// When stopping floor is reached, make transition to one of possible states
		case MovingStoppable:
			_Floor += _Direction * deltaTime * SPEED;
			if (_Floor == FirstStopFloor()) {
				if (IsNextPersonWaitingOnFloor(FirstStopFloor())) {
					_State = ElevatorState.PeopleEntering;
					_LastStateChangeTime = lastTime + deltaTime;
				}
				else if (IsNextPersonTravelingToFloor(FirstStopFloor())) {
					_State = ElevatorState.PeopleLeaving;
					_LastStateChangeTime = lastTime + deltaTime;
				}
			}
			break;
		// After people leave, two transitions are possible:
		// If there are no more people in elevator and nobody is waiting, it becomes idle
		// If someone is waiting on that same floor, it goes to PeopleEntering state
		case PeopleLeaving:
			if (lastTime + deltaTime - _LastStateChangeTime == Constants.ELEVATOR_LEAVE_TIME) {
				while (!_PeopleTraveling.isEmpty() &&
						_PeopleTraveling.get(0).LeaveFloor() == _Floor) {
					_PeopleTraveling.remove(0).LeaveElevator(lastTime + deltaTime);
				}
				if (_PeopleWaiting.isEmpty() && _PeopleTraveling.isEmpty()) {
					_State = ElevatorState.Idle;
					_LastStateChangeTime = lastTime + deltaTime;
				}
				else if (IsNextPersonWaitingOnFloor(Math.round(_Floor))) {
					_State = ElevatorState.PeopleEntering;
					_LastStateChangeTime = lastTime + deltaTime;
				} else if (!IsNextPersonWaitingOnFloor(Math.round(_Floor)) &&
							FirstStopFloor() != Constants.INVALID_VALUE) {
					_State = ElevatorState.MovingStoppable;
					_Direction = _Floor > FirstStopFloor() ? -1 : 1;
					_LastStateChangeTime = lastTime + deltaTime;
				}
			}
			break;
		}
	}

	// This returns time to next elevator event (state transition)
	public float DeltaTime(float time) {
		switch (_State) {
		// Idle has invalid deltaTime
		case Idle:
			return Constants.INVALID_TIME;
		// If the elevator is moving, calculate the time needed for it to stop next time
		case MovingUnstoppable:
		case MovingStoppable:
			return Math.abs(FirstStopFloor() - _Floor) * Constants.ELEVATOR_FLOOR_TIME;
		// If people are entering, calculate how much longer will they be entering
		case PeopleEntering:
			return Constants.ELEVATOR_ENTER_TIME - (time - _LastStateChangeTime);
		// If people are leaving, calculate how much longer will they be leaving
		case PeopleLeaving:
			return Constants.ELEVATOR_LEAVE_TIME - (time - _LastStateChangeTime);
		}
		return Constants.INVALID_TIME;
	}
}
