import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JProgressBar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author Blake Skaja
 * Class used for computing Fantasy Football calculations
 */
public class FantasyFootball {
	
	Map<String, Player> quarterbackList;
	Map<String, Player> runningbackList;
	Map<String, Player> recieverList;
	Map<String, Player> tightendList;
	Map<String, Player> flexList;
	Map<String, Player> defenseList;
	JSONParser parser;
	
	/** 
	 * 
	 * @param week
	 * 	The week that the original projections will be based off of
	 * @throws IOException
	 * @throws ParseException
	 */
	public FantasyFootball(String week) throws IOException, ParseException {
		parser = new JSONParser();
		fillProjectionsList(week);
		assignSalariesToPlayers(week);
	}

	/** 
	 * 
	 * @param urlString
	 * 	The URL for the FantasyFootballNerdsAPI query
	 * @return
	 * 	A new JSONObject containing the data for the query to FantasyFootballNerdsAPI
	 * @throws IOException
	 * @throws ParseException
	 */
	private JSONObject runQuery(String urlString) throws IOException, ParseException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		// Not sure why this is needed. It fixes the issues. Found it online.
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
		if (conn.getResponseCode() != 200) {
			    throw new IOException(conn.getResponseMessage());
		}
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
	
		rd.close();

		conn.disconnect();
		
		JSONObject object = (JSONObject) parser.parse(sb.toString());
		return object;
	}
	
	/**
	 * Gets all the NFL teams
	 * @return
	 *  A JSONArray containing all of the NFL teams
	 * @throws IOException
	 * @throws ParseException
	 */
	public JSONArray getAllNFLTeams() throws IOException, ParseException {
		String url = "http://www.fantasyfootballnerd.com/service/nfl-teams/json/6s2562qvn9mg/";
		JSONArray object = (JSONArray) runQuery(url).get("NFLTeams");
		return object;
	}
	
	/**
	 * Gets the matchups for the current NFL season
	 * @return
	 * 	An ArrayList of GameSchedule objects. Each game is in this array.
	 * @throws IOException
	 * @throws ParseException
	 */
	public ArrayList<GameSchedule> getMatchups() throws IOException, ParseException {
		String url = "http://www.fantasyfootballnerd.com/service/schedule/json/6s2562qvn9mg/";
		JSONArray allMatchups = (JSONArray) runQuery(url).get("Schedule");
		ArrayList<GameSchedule> gameSchedule = new ArrayList<GameSchedule>();

		for(Object object : allMatchups.toArray()) {
			JSONObject o = (JSONObject) object;
			gameSchedule.add(new GameSchedule(o));
		}
		return gameSchedule;
	}
	
	/**
	 * Build the projections lists for the current week
	 * @param week
	 * 	The week that the projections should be run for
	 * @throws IOException
	 * @throws ParseException
	 */
	public void fillProjectionsList(String week) throws IOException, ParseException {
		quarterbackList = getProjectionsForPosition("QB" , week);
		runningbackList = getProjectionsForPosition("RB" , week);
		recieverList = getProjectionsForPosition("WR" , week);
		tightendList = getProjectionsForPosition("TE" , week);
		flexList = new HashMap<String, Player>();
		flexList.putAll(runningbackList);
		flexList.putAll(recieverList);
		flexList.putAll(tightendList);
		defenseList = new HashMap<String, Player>();
		defenseList = getProjectionsForPosition("DEF" , week);
		defenseList = parseDefensiveList(defenseList);
	}

	/**
	 * Gets the projections for all players of the position and week
	 * @param position
	 * 	The position to get the projections for
	 * @param week
	 * 	The week to get the projections for
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public Map<String, Player> getProjectionsForPosition(String position, String week) throws IOException, ParseException {
		String url = "http://www.fantasyfootballnerd.com/service/weekly-projections/json/6s2562qvn9mg/" + position + "/" + week + "/";
		JSONArray allProjections = (JSONArray) runQuery(url).get("Projections");
		Map<String, Player> list = new HashMap<String, Player>();
		for(Object object : allProjections.toArray()) {
			JSONObject o = (JSONObject) object;
			list.put(o.get("displayName").toString(), new Player(o));
		}
		return list;
	}
	
	/**
	 * Maps the current DraftKings salaries to the players
	 * @param week
	 * 	The week of the DraftKings salaries to used. Must be saved in the project folder as a .csv file with the format DKSalaries$week$.csv.
	 */
	private void assignSalariesToPlayers(String week) {
		File file = new File("DKSalaries" + week + ".csv");
		Scanner reader;
		try {
			reader = new Scanner(file);
		} catch (FileNotFoundException e) {
			return;
		}
		
		reader.nextLine();
		while(reader.hasNextLine()) {
			String line = reader.nextLine();
			String[] parts = line.split(",");
			String position = parts[0].replace('"', ' ');
			position  = position.trim();
			String name = parts[1];
			name = parts[1].replace('"', ' ');
			name = name.trim();
			String value = parts[2];
			value = parts[2].replace('"', ' ');
			value = value.trim();
			
			switch (position) {
				case "QB" :
					if (quarterbackList.containsKey(name)) {
						quarterbackList.get(name).setSalary(Integer.parseInt(value));
					}
					break;
				case "RB" :
					if (runningbackList.containsKey(name)) {
						runningbackList.get(name).setSalary(Integer.parseInt(value));
					}
					break;
				case "WR" :
					if (recieverList.containsKey(name)) {
						recieverList.get(name).setSalary(Integer.parseInt(value));
					}
					break;
				case "TE" :
					if (tightendList.containsKey(name)) {
						tightendList.get(name).setSalary(Integer.parseInt(value));
					}
					break;
				case "DST" :
				if (defenseList.containsKey(name)) {
						defenseList.get(name).setSalary(Integer.parseInt(value));
					}
					break;
			}
			
		}
	}
	
	/**
	 * Parses the defensive list so the team names are the same for the projections and DraftKings
	 * @param defenseList
	 * 	The list to parse
	 * @return
	 * 	The parsed defensive list
	 */
	private Map<String, Player> parseDefensiveList(Map<String, Player> defenseList) {
		Set<String> keys = defenseList.keySet();
		Map<String, Player> list = new HashMap<String, Player>();
		
		for (String k : keys) {
			String[] l = k.split(" ");
			String x = l[l.length - 1].trim();
			list.put(x, defenseList.get(k));
		}
		
		return list;
	}

	/**
	 * Sorts the players list from highest to lowest salary
	 * @param list
	 * 	The list to sort
	 * @return
	 * 	The sorted list
	 */
	public ArrayList<Player> sortBySalary(Map<String, Player> list) {
		ArrayList<Player> playerList = new ArrayList<Player>(list.values());
		playerList.sort(new SalaryComparator());
		return playerList;
	}
	
	/**
	 * Sorts the players list from highest to lowest projected points
	 * @param list
	 * 	The list to sort
	 * @return
	 * 	The sorted list
	 */
	public ArrayList<Player> sortByProjectedPoints(Map<String, Player> list) {
		ArrayList<Player> playerList = new ArrayList<Player>(list.values());
		playerList.sort(new ProjectedPointsComparator());
		return playerList;
	}
	
	/**
	 * Sorts the players list from highest to lowest points per dollar value
	 * @param list
	 * 	The list to sort
	 * @return
	 * 	The sorted list
	 */
	public ArrayList<Player> sortByPointsPerDollar(Map<String, Player> list) {
		ArrayList<Player> playerList = new ArrayList<Player>(list.values());
		playerList.sort(new PointsPerDollarComparator());
		return playerList;
	}
	
	/**
	 * Generates the highest scoring possible line-up
	 * @param selections
	 * The players that you forsure want on your team
	 * @return
	 * The ideal line-up
	 */
	public ArrayList<Player> getIdealLineup(ArrayList<Player> selections) {
		ArrayList<Player> fullQBList = sortByPointsPerDollar(quarterbackList);
		List<Player> partQBList = fullQBList.subList(0, 14);
		
		ArrayList<Player> fullRBList = sortByPointsPerDollar(runningbackList);
		List<Player> partRBList = fullRBList.subList(0, 25);
		
		ArrayList<Player> fullWRList = sortByPointsPerDollar(recieverList);
		List<Player> partWRList = fullWRList.subList(0, 25);
		
		ArrayList<Player> fullTEList = sortByPointsPerDollar(tightendList);
		List<Player> partTEList = fullTEList.subList(0, 10);
		
		ArrayList<Player> fullFlexList = sortByPointsPerDollar(flexList);
		List<Player> partFlexList =  fullFlexList.subList(0, 25);
		
		List<Player> dstList = sortByProjectedPoints(defenseList);
		
		GetIdealLineupThread thread = new GetIdealLineupThread(partQBList, partRBList, partWRList, partTEList, partFlexList, dstList, selections);
		thread.run();
		
		return thread.idealLineup;
	}

	/**
	 * Updates the projections and salaries to the new week
	 * @param week
	 * The new week to update too
	 * @throws IOException
	 * @throws ParseException
	 */
	public void updateWeek(String week) throws IOException, ParseException {
		fillProjectionsList(week);
		assignSalariesToPlayers(week);
	}

}

/**
 * Simple comparator for comparing Player objects by salary
 * @author Blake Skaja
 *
 */
class SalaryComparator implements Comparator<Player> {
	@Override
	public int compare(Player o1, Player o2) {
		if (o1.getSalary() > o2.getSalary()) {
			return -1;
		} else if (o1.getSalary() < o2.getSalary()) {
			return 1;
		} else {
			return 0;
		}
	}
}

/**
 * Simple comparator for comparing Player objects by projected points
 * @author Blake Skaja
 *
 */
class ProjectedPointsComparator implements Comparator<Player> {
	@Override
	public int compare(Player o1, Player o2) {
		if (o1.getProjectedScore() > o2.getProjectedScore()) {
			return -1;
		} else if (o1.getProjectedScore() < o2.getProjectedScore()) {
			return 1;
		} else {
			return 0;
		}
	}
}

/**
 * Simple comparator for comparing Player objects by points per dollar
 * @author Blake Skaja
 *
 */
class PointsPerDollarComparator implements Comparator<Player> {
	@Override
	public int compare(Player o1, Player o2) {
		if (o1.getPointsPerDollar() > o2.getPointsPerDollar()) {
			return -1;
		} else if (o1.getPointsPerDollar() < o2.getPointsPerDollar()) {
			return 1;
		} else {
			return 0;
		}
	}
}

/**
 * Allows us to run the generate ideal lineup in a new thread.
 * @author Blake Skaja
 *
 */
class GetIdealLineupThread implements Runnable {
	List<Player> partQBList;
	List<Player> partRB1List;
	List<Player> partRB2List;
	List<Player> partWR1List;
	List<Player> partWR2List;
	List<Player> partWR3List;
	List<Player> partTEList;
	List<Player> partFlexList;
	List<Player> partDstList;
	ArrayList<Player> idealLineup = new ArrayList<Player>();
	
	public GetIdealLineupThread (List<Player> qbList, List<Player> rbList, 
			List<Player> wrList, List<Player> teList, List<Player> flexList, List<Player> dstList, List<Player> selections) {

		if (selections != null && !selections.get(0).getPlayerName().equals("null")) {
			partQBList = new ArrayList<Player>();
			partQBList.add(selections.get(0));
		} else {
			partQBList = qbList;
		}
		
		if (selections != null && !selections.get(1).getPlayerName().equals("null")) {
			partRB1List = new ArrayList<Player>();
			partRB1List.add(selections.get(1));
		} else {
			partRB1List = new ArrayList<Player>();
			partRB1List.addAll(rbList);
		}
		
		if (selections != null && !selections.get(2).getPlayerName().equals("null")) {
			partRB2List = new ArrayList<Player>();
			partRB2List.add(selections.get(2));
		} else {
			partRB2List = new ArrayList<Player>();
			partRB2List.addAll(rbList);
		}
		
		if (selections != null && !selections.get(3).getPlayerName().equals("null")) {
			partWR1List = new ArrayList<Player>();
			partWR1List.add(selections.get(3));
		} else {
			partWR1List = new ArrayList<Player>();
			partWR1List.addAll(wrList);
		}
		
		if (selections != null && !selections.get(4).getPlayerName().equals("null")) {
			partWR2List = new ArrayList<Player>();
			partWR2List.add(selections.get(4));
		} else {
			partWR2List = new ArrayList<Player>();
			partWR2List.addAll(wrList);
		}
		
		if (selections != null && !selections.get(5).getPlayerName().equals("null")) {
			partWR3List = new ArrayList<Player>();
			partWR3List.add(selections.get(5));
		} else {
			partWR3List = new ArrayList<Player>();
			partWR3List.addAll(wrList);
		}
		
		if (selections != null && !selections.get(6).getPlayerName().equals("null")) {
			partTEList = new ArrayList<Player>();
			partTEList.add(selections.get(6));
		} else {
			partTEList = teList;
		}
		
		if (selections != null && !selections.get(7).getPlayerName().equals("null")) {
			partFlexList = new ArrayList<Player>();
			partFlexList.add(selections.get(7));
		} else {
			partFlexList = flexList;
		}
		
		if (selections != null && !selections.get(8).getPlayerName().equals("null")) {
			partDstList = new ArrayList<Player>();
			partDstList.add(selections.get(8));
		} else {
			partDstList = dstList;
		}
	}
	
	@Override
	public void run() {
		double maxScore = 0;
		
		for (Player qb : partQBList) {
			for (Player rb1 : partRB1List) {
				for (Player rb2 : partRB2List) {
					for (Player wr1 : partWR1List) {
						for (Player wr2 : partWR2List) {
							for (Player wr3 : partWR3List) {
								for (Player te : partTEList) {
									for (Player flex : partFlexList) {
										if (!(rb1.equals(rb2) || rb1.equals(flex) || rb2.equals(flex) || wr1.equals(wr2) || 
										wr1.equals(flex) || wr1.equals(wr3) || wr2.equals(wr3) || wr2.equals(flex) || 
										wr3.equals(flex) || te.equals(flex))) {
											double score = qb.getProjectedScore() + rb1.getProjectedScore() + rb2.getProjectedScore() +
													wr1.getProjectedScore() + wr2.getProjectedScore() + wr3.getProjectedScore() + 
													te.getProjectedScore() + flex.getProjectedScore();
											
											double salary = qb.getSalary() + rb1.getSalary() + rb2.getSalary() +
													wr1.getSalary() + wr2.getSalary() + wr3.getSalary() + 
													te.getSalary() + flex.getSalary();
											
											if (score > maxScore && salary < 47000 ) {
												maxScore = score;
												idealLineup.clear();
												idealLineup.add(qb);
												idealLineup.add(rb1);
												idealLineup.add(rb2);
												idealLineup.add(wr1);
												idealLineup.add(wr2);
												idealLineup.add(wr3);
												idealLineup.add(te);
												idealLineup.add(flex);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		double score = 0;
		int salary = 0;
		for(Player p : idealLineup) {
			score += p.getProjectedScore();
			salary += p.getSalary();
		}
		
		for (Player d : partDstList) {
			double newScore = d.getProjectedScore() + score;
			if(newScore > maxScore && salary + d.getSalary() <= 50000 && d.getSalary() > 0) {
				maxScore = newScore;
				if (idealLineup.size() == 9) {
					idealLineup.remove(8);
				}
				
				idealLineup.add(d);
			}
		}
	}
	
}
