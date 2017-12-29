// Author: Sayeed Gulmahamad
// Elevator has a finite number of possible states
public enum ElevatorState {
	// When elevator is empty and not moving
	Idle,
	// When elevator is empty but moving
	MovingUnstoppable,
	// When people are entering elevator
	PeopleEntering,
	// When elevator is not empty and moving
	MovingStoppable,
	// When people are leaving elevator
	PeopleLeaving
}

// State transitions:
//	Idle --> MovingUnstoppable: 			Someone calls empty elevator
//	MovingUnstoppable --> PeopleEntering:	Someone enters empty elevator
//	PeopleEntering --> MovingStoppable:		Elevator moves after people enter
//	MovingStoppable --> PeopleLeaving:		People leave after elevator stops
//	MovingStoppable --> PeopleEntering:		People enter after elevator stops
//	PeopleLeaving --> PeopleEntering:		People enter after people leave
//	PeopleLeaving --> MovingStoppable:		Elevator moves after people leave
//	PeopleLeaving --> Idle:					Empty elevator after people leave
