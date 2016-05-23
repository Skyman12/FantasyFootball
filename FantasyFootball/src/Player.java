import org.json.simple.JSONObject;

public class Player {
	private JSONObject projectionsData;
	private String name;
	private String teamName;
	private String position;
	private double projectedScore;
	private int salary;
	
	/**
	 * A player object for holding all the player related data
	 * @param projections
	 */
	public Player(JSONObject projections) {
		projectionsData = projections;
		name = projections.get("displayName").toString().trim();
		teamName = projectionsData.get("team").toString();
		if (projectionsData.get("position").toString().equals("DEF")) {
			position = "DST";
			String[] splitList = name.split(" ");
			name = splitList[splitList.length - 1].trim();
		} else {
			position = projectionsData.get("position").toString();
		}
		projectedScore = Math.floor(calculateProjectedScore() * 100) / 100;
	}
	
	public Player(String position) {
		this.position = position;
		this.name = "null";
		this.teamName = "";
		this.projectedScore = 0;
		this.salary = 0;
	}
	
	/**
	 * Using DraftKings scoring rules, generates the projected score for this player
	 * @return
	 * The calculated projected score
	 */
	public double calculateProjectedScore() {
		double score = 0;
		if (!position.equals("DST")) {
			
			// Passing Touchdowns
			score += 4 * getPassingTouchdownProjection();
			
			// Passing Yards
			score += .04 * getPassingYardsProjection();
			
			// Passing Yards bonus
			if (getPassingYardsProjection() >= 300) {
				score += 3;
			}
			
			// Passing Interception subtraction
			score -= getPassingInterceptionProjection();
			
			// Rushing Yards
			score += .1 * getRushingYardsProjection();
			
			// Rushing Touchdowns
			score += 6 * getRushingTouchdownProjection();
			
			// Rushing Yards bonus
			if (getRushingYardsProjection() >= 100) {
				score += 3;
			}
			
			// Receiving Yards
			score += .1 * getReceivingYardsProjection();
					
			// Receiving Touchdowns
			score += 6 * getReceivingTouchdownProjection();
					
			// Receiving Yards bonus
			if (getReceivingYardsProjection() >= 100) {
				score += 3;
			}
			
			// Per Reception
			score +=  getReceptionsProjection();
			
			// Fumble subtraction
			score -= getFumblesLostProjection();
		} 
		// Defensive Stuff 
		else {
			// Defensive sack
			score += getDefensiveSacksProjection();
			
			// Defensive interception
			score += 2 * getDefensiveInterceptionsProjection();
			
			score += 2 * getDefensiveFumbleRecoveryProjection();
			
			score += 6 * getDefensiveReturnTouchdownProjection();
			
			score += 6 * getDefensiveTouchdownProjection();
			
			score += 2 * getDefSafetyProjection();
		
			double pointsAgainst = Math.round(getDefensivePointsAgainstProjection());
			
			if (pointsAgainst == 0.0 ) {
				score += 10;
			}
			else if (pointsAgainst == 0.0 ) {
				score += 10;
			} else if (pointsAgainst < 6.0 ) {
				score += 7;
			} else if (pointsAgainst < 13.0 ) {
				score += 4;
			} else if (pointsAgainst < 20.0 ) {
				score += 1;
			} else if (pointsAgainst < 27.0 ) {
				score += 0;
			} else if (pointsAgainst < 34.0 ) {
				score += -1;
			} else {
				score += -4;
			}
		}

		return score;
	}
	
	/**
	 * The projected score
	 * @return
	 * The projected score
	 */
	public double getProjectedScore() {
		return projectedScore;
	}
	
	/**
	 * The salary
	 * @return
	 * The salary
	 */
	public int getSalary() {
		return salary;
	}
	
	public void setSalary(int s) {
		salary = s;
	}
	
	public void setProjectedScore(double score) {
		projectedScore = score;
	}
	
	public double getPointsPerDollar() {
		if (salary == 0.0) {
			return 0.0;
		}
		return Math.floor((projectedScore / salary) * 1000000) / 100;
	}
	
	public String getPlayerName() {
		return name;
	}
	
	public String getTeamName() {
		return teamName;
	}
	
	public String getPlayerPosition() {
		return position;
	}

	public void printData() {
		System.out.println(projectionsData.toString());
	}
	
	public String toString() {
		return position + " : " + name + 
				" --- Projected Score: " + projectedScore + 
				" --- Salary: " + salary + 
				" --- Points per Dollar: " + getPointsPerDollar();
	}
	
	public double getDefensiveReturnTouchdownProjection() {
		return Double.parseDouble(projectionsData.get("defRetTD").toString());
	}
	
	public double getFieldGoalMadeProjection() {
		return Double.parseDouble(projectionsData.get("fg").toString());
	}
	
	public double getDefSafetyProjection() {
		return Double.parseDouble(projectionsData.get("defSafety").toString());
	}
	
	public double getFumblesLostProjection() {
		return Double.parseDouble(projectionsData.get("fumblesLost").toString());
	}
	
	public double getDefensiveTouchdownProjection() {
		return Double.parseDouble(projectionsData.get("defTD").toString());
	}
	
	public double getDefensivePointsAgainstProjection() {
		return Double.parseDouble(projectionsData.get("defPA").toString());
	}
	
	public double getDefensiveInterceptionsProjection() {
		return Double.parseDouble(projectionsData.get("defInt").toString());
	}
	
	public double getDefensiveYardsAllowedProjection() {
		return Double.parseDouble(projectionsData.get("defYdsAllowed").toString());
	}
	
	public double getDefensiveSacksProjection() {
		return Double.parseDouble(projectionsData.get("defSack").toString());
	}
	
	public double getDefensiveFumbleRecoveryProjection() {
		return Double.parseDouble(projectionsData.get("defFR").toString());
	}
	
	public double getReceptionsProjection() {
		return Double.parseDouble(projectionsData.get("receptions").toString());
	}
	
	public double getPassingInterceptionProjection() {
		return Double.parseDouble(projectionsData.get("passInt").toString());
	}
	
	public double getPassAttemptsProjection() {
		return Double.parseDouble(projectionsData.get("passAtt").toString());
	}
	
	public double getPassingCompletionsProjection() {
		return Double.parseDouble(projectionsData.get("passCmp").toString());
	}
	
	public double getRushAttemptsProjection() {
		return Double.parseDouble(projectionsData.get("rushAtt").toString());
	}

	public double getReceivingYardsProjection() {
		return Double.parseDouble(projectionsData.get("recYds").toString());
	}
	
	public double getPassingYardsProjection() {
		return Double.parseDouble(projectionsData.get("passYds").toString());
	}
	
	public double getRushingYardsProjection() {
		return Double.parseDouble(projectionsData.get("rushYds").toString());
	}
	
	public double getReceivingTouchdownProjection() {
		return Double.parseDouble(projectionsData.get("recTD").toString());
	}
	
	public double getRushingTouchdownProjection() {
		return Double.parseDouble(projectionsData.get("rushTD").toString());
	}
	
	public double getPassingTouchdownProjection() {
		return Double.parseDouble(projectionsData.get("passTD").toString());
	}
	
	public boolean equals(Player p) {
		if(this.getPlayerName().equals(p.getPlayerName())) {
			return true;
		}
		
		return false;
		
	}

}
