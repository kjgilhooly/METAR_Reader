package xriva.xml.weather;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class WeatherConditions
{
	static String[] dirs =
	{
			"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW",
			"WSW", "W", "WNW", "NW", "NNW"
	};

	public static void main(String args[])
	{

		String stationsToRequest[] =
		{
				"KDFW", "KUKT", "KHDO", "KDAY", "KLGA"
		};

		if (args.length > 0)
			stationsToRequest = args;
		WeatherConditions reader = new WeatherConditions();
		for (int stn = 0; stn < stationsToRequest.length; stn++)
		{
			String station = stationsToRequest[stn];
			Hashtable<String, String> currConditions = reader
					.getMetarReading(station);
			System.out.println(reader.parseConditions(currConditions));
			// System.out.println(reader.parseConditionsCSV(currConditions));
			reader.dump(currConditions);
		}

	}

	public void dump(Hashtable<String, String> currConditions)
	{
		Enumeration<String> keys = currConditions.keys();
		while (keys.hasMoreElements())
		{
			String key = keys.nextElement();
			String value = currConditions.get(key);
			System.out.println(String.format("%-30s [%s]", key, value));
		}

	}

	@SuppressWarnings(
	{
		"unchecked"
	})
	public Hashtable<String, String> getMetarReading(String station)
	{
		String URL = "http://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&stationString="
				+ station + "&hoursBeforeNow=1";

		METAR_Parser weatherReader = new METAR_Parser(URL);
		if (weatherReader.parse())
			return weatherReader.getWeatherInfo();
		else
			return null;
	}

	public String parseConditions(Hashtable<String, String> currConditions)
	{
		String conditions = "None Available";

		try
		{
			if (currConditions != null)
			{
				float temp_c = 0.0f;
				float temp_f = 0.0f;
				String station = currConditions.get("station_id");
				String degrees = currConditions.get("wind_dir_degrees");
				String speed = currConditions.get("wind_speed_kt");
				String visible = currConditions.get("visibility_statute_mi");
				String timeStamp = currConditions.get("observation_time");
				StringTokenizer st = new StringTokenizer(timeStamp, "TZ");
				String date = st.nextToken();
				String time = st.nextToken();
				int wind_dir = 0;
				int wind_speed = 0;
				float visibility = 0.0f;
				if (degrees != null)
					wind_dir = Integer.parseInt(degrees);
				if (speed != null)
					wind_speed = Integer.parseInt(speed);
				if (visible != null)
					visibility = Float.parseFloat(visible);
				wind_dir = (int) ((wind_dir / 22.5) + .5);
				temp_c = Float.parseFloat(currConditions.get("temp_c"));
				temp_f = (9.0f / 5.0f * temp_c) + 32.0f;
				conditions = String
						.format("%-5s Temp %4.1fF (%4.1fC) Wind %-3s %3d kt Visibility %4.1f mi @ %-10s %-8sZ",
								station, temp_f, temp_c, dirs[(wind_dir % 16)],
								wind_speed, visibility, date, time);
				//
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return conditions;
	}

}
