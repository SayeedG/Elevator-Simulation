// Author: Sayeed Gulmahamad
import java.util.ArrayList;

// This calculates the statistics for the entire simulation
// After the simulation finishes, data is processed and calculated
// It includes subclasses used for storing data during the simulation
public class Statistic {
	// This is the data extracted from a single person
	private static class PersonData {
		private float _WaitTime;
		private float _TravelTime;

		public PersonData(Person person) {
			_WaitTime = person.WaitTime();
			_TravelTime = person.TravelTime();
		}

		public float WaitTime() {
			return _WaitTime;
		}
		public float TravelTime() {
			return _TravelTime;
		}
	}

	// This is the data extracted from every person traveling from one floor to another
	private static class StatisticData {
		private ArrayList<PersonData> _Data;

		private float _WaitTimeAverage;
		private float _TravelTimeAverage;
		private float _WaitTimeStandardDeviation;
		private float _TravelTimeStandardDeviation;

		public StatisticData() {
			_Data = new ArrayList<PersonData>();
		}

		public void Add(PersonData personData) {
			_Data.add(personData);
		}

		public void Calculate() {
			float[] waitTimes = new float[_Data.size()];
			float[] travelTimes = new float[_Data.size()];
			for (int i = 0; i < _Data.size(); ++i) {
				waitTimes[i] = _Data.get(i).WaitTime();
				travelTimes[i] = _Data.get(i).TravelTime();
			}
			_WaitTimeAverage = Methods.Average(waitTimes);
			_TravelTimeAverage = Methods.Average(travelTimes);
			_WaitTimeStandardDeviation = Methods.StandardDeviation(waitTimes, _WaitTimeAverage);
			_TravelTimeStandardDeviation = Methods.StandardDeviation(travelTimes, _TravelTimeAverage);
		}

		public int NumberOfPeople() {
			return _Data.size();
		}
		public float WaitTimeAverage() {
			return Methods.FormatTime(_WaitTimeAverage);
		}
		public float TravelTimeAverage() {
			return Methods.FormatTime(_TravelTimeAverage);
		}
		public float WaitTimeStandardDeviation() {
			return Methods.FormatTime(_WaitTimeStandardDeviation);
		}
		public float TravelTimeStandardDeviation() {
			return Methods.FormatTime(_TravelTimeStandardDeviation);
		}
	}

	private static StatisticData[][] _Data;

	public static void Initialize() {
		_Data = new StatisticData[Constants.NUMBER_OF_FLOORS][Constants.NUMBER_OF_FLOORS];
		for (int i = 0; i < Constants.NUMBER_OF_FLOORS; ++i) {
			for (int j = 0; j < Constants.NUMBER_OF_FLOORS; ++j) {
				_Data[i][j] = new StatisticData();
			}
		}
	}

	public static void Add(Person person) {
		PersonData personData = new PersonData(person);
		_Data[person.EnterFloor()][person.LeaveFloor()].Add(personData);
	}

	public static void Calculate() {
		for (int i = 0; i < Constants.NUMBER_OF_FLOORS; ++i) {
			for (int j = 0; j < Constants.NUMBER_OF_FLOORS; ++j) {
				_Data[i][j].Calculate();
			}
		}
	}

	public static String GetOutput() {
		String output = "";
		for (int i = 0; i < Constants.NUMBER_OF_FLOORS; ++i) {
			for (int j = 0; j < Constants.NUMBER_OF_FLOORS; ++j) {
				StatisticData data = _Data[i][j];
				if (data.NumberOfPeople() == 0) {
					continue;
				}
				output = output.concat(String.format("From floor %d to floor %d%n\t People: Average = %d Standard Deviation = %.2f%n\t Wait time: Average = %.2f Standard Deviation = %.2f%n\t Travel time: Average = %.2f Standard Deviation = %.2f%n",
						i + 1, j + 1,
						data.NumberOfPeople(), ((float)data.NumberOfPeople() / (Constants.NUMBER_OF_SIMULATIONS * Constants.NUMBER_OF_PEOPLE)) * 100,
						data.WaitTimeAverage(), data.WaitTimeStandardDeviation(),
						data.TravelTimeAverage(), data.TravelTimeStandardDeviation()));
			}
		}
		return output;
	}
}
