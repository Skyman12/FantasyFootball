import org.json.simple.JSONObject;
 /**
  * Holds the game schedule data
  * @author Blake Skaja
  *
  */
public class GameSchedule {
	JSONObject gameScheduleData;
	String week;
	String awayTeam;
	String homeTeam;
	String gameTime;
	
	public GameSchedule(JSONObject object) {
		gameScheduleData = object;
		fillGameSchedule();
	}
	
	public void fillGameSchedule() {
		week = gameScheduleData.get("gameWeek").toString();
		awayTeam = gameScheduleData.get("awayTeam").toString();
		homeTeam = gameScheduleData.get("homeTeam").toString();
		gameTime = gameScheduleData.get("gameTimeET").toString();
	}

}
