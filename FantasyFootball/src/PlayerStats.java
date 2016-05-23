import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.Font;

/**
 * Used for displaying the players stats.
 * @author Blake Skaja
 *
 */
public class PlayerStats extends JFrame {

	private JPanel contentPane;
	
	/**
	 * Creates the new Frame and populates it with a players data
	 * @param player
	 * 	The players stats to display
	 * @param gameSchedule
	 * 	The current matchup to display
	 */
	public PlayerStats(Player player, ArrayList<GameSchedule> gameSchedule) {
		setBounds(100, 100, 721, 483);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		ArrayList<String> display = new ArrayList<String>();
		display.add(player.getPlayerName() + " - " + player.getPlayerPosition());
		display.add("Salary: " + player.getSalary());
		display.add("Projected Score: " + player.getProjectedScore());
		display.add("Points per Dollar: " + player.getPointsPerDollar());
		display.add("--------------------------------------------------------------------------------------------");
		display.add(getAwayTeam(gameSchedule, player));
		display.add(getGameTime(gameSchedule, player));
		display.add("--------------------------------------------------------------------------------------------");
		
		switch (player.getPlayerPosition()) {
			case "QB" : 
				display.add("Passing Attempts: " + player.getPassAttemptsProjection());
				display.add("Passing Completions: " + player.getPassingCompletionsProjection());
				display.add("Passing Yards: " + player.getPassingYardsProjection());
				display.add("Passing Touchdowns: " + player.getPassingTouchdownProjection());
				display.add("Passing Interceptions: " + player.getPassingInterceptionProjection());
				display.add("--------------------------------------------------------------------------------------------");
				display.add("Rushing Attempts: " + player.getRushAttemptsProjection());
				display.add("Rushing Yards: " + player.getRushingYardsProjection());
				display.add("Rushing Touchdowns: " + player.getRushingTouchdownProjection());
				break;
			case "RB" : 
				display.add("Rushing Attempts: " + player.getRushAttemptsProjection());
				display.add("Rushing Yards: " + player.getRushingYardsProjection());
				display.add("Rushing Touchdowns: " + player.getRushingTouchdownProjection());
				display.add("--------------------------------------------------------------------------------------------");
				display.add("Receptions: " + player.getReceptionsProjection());
				display.add("Receiving Yards: " + player.getReceivingYardsProjection());
				display.add("Receiving Touchdowns: " + player.getReceivingTouchdownProjection());
				break;	
			case "WR" : 
				display.add("Receptions: " + player.getReceptionsProjection());
				display.add("Receiving Yards: " + player.getReceivingYardsProjection());
				display.add("Receiving Touchdowns: " + player.getReceivingTouchdownProjection());
				display.add("--------------------------------------------------------------------------------------------");
				display.add("Rushing Attempts: " + player.getRushAttemptsProjection());
				display.add("Rushing Yards: " + player.getRushingYardsProjection());
				display.add("Rushing Touchdowns: " + player.getRushingTouchdownProjection());
				break;
			case "TE" : 
				display.add("Receptions: " + player.getReceptionsProjection());
				display.add("Receiving Yards: " + player.getReceivingYardsProjection());
				display.add("Receiving Touchdowns: " + player.getReceivingTouchdownProjection());
				break;
		}
			
		JList list = new JList(display.toArray());
		contentPane.add(list, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel lblProjections = new JLabel("Projections");
		lblProjections.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel.add(lblProjections);
	}
	
	public String getAwayTeam(ArrayList<GameSchedule> gameScheduleList, Player player) {
		for (GameSchedule g : gameScheduleList) {
			if (player.getTeamName().equals(g.awayTeam)) {
				return "Away against: " + g.homeTeam;
			}
			
			if (player.getTeamName().equals(g.homeTeam)) {
				return "Home against: " + g.awayTeam;
			}
		}
		return "No game";
	}
	
	public String getGameTime(ArrayList<GameSchedule> gameScheduleList, Player player) {
		for (GameSchedule g : gameScheduleList) {
			if (player.getTeamName().equals(g.awayTeam)) {
				return "Game time: " + g.gameTime;
			}
			
			if (player.getTeamName().equals(g.homeTeam)) {
				return "Game time: " + g.gameTime;
			}
		}
		
		return "No game time";

	}

}
