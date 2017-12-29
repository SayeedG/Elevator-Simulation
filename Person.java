// Author: Sayeed Gulmahamad
// This represents a single person going from one floor to another
// It is a passive component in Elevator-Person relationship
// Person can only perform two actions: it can enter and leave elevator
// Elevator entering and leaving time is recorded and used in statistics
// After leaving, person is passed to statistics for further calculations
public class Person {
	private float _CreateTime;
	private float _ElevatorEnterTime;
	private float _ElevatorLeaveTime;

	private int _EnterFloor;
	private int _LeaveFloor;

	public Person(float createTime,
					int enterFloor,
					int leaveFloor) {
		if (enterFloor == leaveFloor) {
			System.out.println("ERROR: Person with same enter and leave level");
			return;
		}
		_CreateTime = createTime;
		_EnterFloor = enterFloor;
		_LeaveFloor = leaveFloor;
	}

	public float WaitTime() {
		return _ElevatorEnterTime - _CreateTime;
	}
	public float TravelTime() {
		return _ElevatorLeaveTime - _ElevatorEnterTime;
	}

	public int EnterFloor() {
		return _EnterFloor;
	}
	public int LeaveFloor() {
		return _LeaveFloor;
	}
	public int Direction() {
		return _LeaveFloor > _EnterFloor ? 1 : -1;
	}

	public void EnterElevator(float time) {
		_ElevatorEnterTime = time;
	}
	public void LeaveElevator(float time) {
		_ElevatorLeaveTime = time;
		Statistic.Add(this);
	}
}
