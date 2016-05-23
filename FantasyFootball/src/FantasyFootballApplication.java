import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.simple.parser.ParseException;

import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JProgressBar;

public class FantasyFootballApplication extends JFrame {

	private JPanel contentPane;
	private String[] positionList = {"All", "QB", "RB", "WR", "TE", "FLEX", "DST"};
	private String[] sortByList = {"Projected Score", "Salary", "Points per Dollar"};
	private FantasyFootball football;
	
	private ArrayList<Player> idealList;
	private ArrayList<Player> choosenPlayers;
	private ArrayList<GameSchedule> weeklyGameSchedule;
	private ArrayList<GameSchedule> fullGameSchedule;
	
	private JTabbedPane tabbedPane;
	
	// The all players pane and its contents
	private JScrollPane allPlayersScrollPane;
	private JPanel allPlayersPanel;
	private JLabel allPlayersPositionLabel;
	private JLabel allPlayersSortByLabel;
	private JLabel allPlayersWeekButton;
	private JButton allPlayersAddButton;
	private JButton allPlayersEditButton;
	private JButton allPlayersViewButton;
	private JComboBox<String> allPlayersPositionComboBox;
	private JComboBox<String> allPlayersWeekComboBox;
	private JComboBox<String> allPlayersSortByComboBox;
	private JList allPlayersList;
	
	// The build a line-up pane and its contents
	private JScrollPane buildLineupScrollPane;
	private JPanel buildLineupPanel;
	private JLabel buildLineupLabel1;
	private JLabel buildLineupSalaryLabel;
	private JLabel buildLineupLabel2;
	private JLabel buildLineupLabel3;
	private JLabel buildLineupProjectedPointsLabel;
	private JButton buildlLineupRemoveAllButton;
	private JButton buildLineupRemovePlayerButton;
	private JButton buildLineupViewButton;
	private JButton buildLineupFinishButton;
	private JList buildLineupList;
	
	// The ideal line-up pane and its contents
	private JScrollPane idealLineupPane;
	private JPanel idealLineupPanel;
	private JLabel idealLineupLabel1;
	private JLabel idealLineupSalaryLabel;
	private JLabel idealLineupLabel2;
	private JLabel idealLineupLabel3;
	private JLabel idealLineupProjectedPointsLabel;
	private JButton idealLineupGenerateButton;
	private JButton idealLineupViewButton;
	private JList idealLineupList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FantasyFootballApplication frame = new FantasyFootballApplication();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public FantasyFootballApplication() throws IOException, ParseException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		allPlayersWeekComboBox = new JComboBox();
		allPlayersWeekComboBox.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17"}));
		allPlayersWeekComboBox.setSelectedIndex(0);
		
		football = new FantasyFootball(Integer.toString(allPlayersWeekComboBox.getSelectedIndex() + 1));
		weeklyGameSchedule = new ArrayList<GameSchedule>();
		fullGameSchedule = football.getMatchups();
		getScheduleForWeek("1");
		
		buildLineupLabel1 = new JLabel("Salary: ");
		buildLineupSalaryLabel = new JLabel("0");
		buildLineupLabel2 = new JLabel("/ 50,000");
		buildLineupLabel3 = new JLabel("   ---    Projected Points: ");
		buildLineupProjectedPointsLabel = new JLabel("0.0");
		
		idealLineupPane = new JScrollPane();
		
		idealLineupPanel = new JPanel();
		idealLineupPane.setColumnHeaderView(idealLineupPanel);
		
		idealList = new ArrayList<Player>();
		addDefaultPlayers(idealList);
		idealLineupList = new JList(idealList.toArray());
		
		idealLineupPane.setViewportView(idealLineupList);
		
		idealLineupLabel1 = new JLabel("Salary: ");
		idealLineupPanel.add(idealLineupLabel1);
		
		idealLineupSalaryLabel = new JLabel("0");
		idealLineupPanel.add(idealLineupSalaryLabel);
		
		idealLineupLabel2 = new JLabel("/ 50,000");
		idealLineupPanel.add(idealLineupLabel2);
		
		idealLineupLabel3 = new JLabel("   ---    Projected Points: ");
		idealLineupPanel.add(idealLineupLabel3);
		
		idealLineupProjectedPointsLabel = new JLabel("0.0");
		idealLineupPanel.add(idealLineupProjectedPointsLabel);
		
		choosenPlayers = new ArrayList<Player>();
		addDefaultPlayers(choosenPlayers);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		allPlayersScrollPane = new JScrollPane();
		tabbedPane.addTab("Players", null, allPlayersScrollPane, null);
		
		allPlayersPanel = new JPanel();
		allPlayersScrollPane.setColumnHeaderView(allPlayersPanel);
		
		allPlayersPositionLabel = new JLabel("Position");
		allPlayersPanel.add(allPlayersPositionLabel);
		
		allPlayersPositionComboBox = new JComboBox(positionList);
		allPlayersPanel.add(allPlayersPositionComboBox);
		
		allPlayersSortByLabel = new JLabel("Sort by");
		allPlayersPanel.add(allPlayersSortByLabel);
		
		allPlayersSortByComboBox = new JComboBox(sortByList);
		allPlayersPanel.add(allPlayersSortByComboBox);
		
		allPlayersList = new JList(updateListData());
		
		buildLineupList = new JList(choosenPlayers.toArray());
		
		allPlayersAddButton = new JButton("Add to Roster");
		allPlayersAddButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (allPlayersList.getSelectedIndex() == -1) {
					return;
				}
				Player player = (Player) allPlayersList.getSelectedValue();
				if (!choosenPlayers.contains(player)) {
					if (player.getPlayerPosition().equals("QB") && 
							choosenPlayers.get(0).getPlayerName().equals("null")) {
						choosenPlayers.remove(0);	
						choosenPlayers.add(0, player);	
					} else if (player.getPlayerPosition().equals("RB") && 
							choosenPlayers.get(1).getPlayerName().equals("null")) {
						choosenPlayers.remove(1);	
						choosenPlayers.add(1, player);	
					} else if (player.getPlayerPosition().equals("RB") && 
							choosenPlayers.get(2).getPlayerName().equals("null")) {
						choosenPlayers.remove(2);	
						choosenPlayers.add(2, player);	
					} else if (player.getPlayerPosition().equals("WR") && 
							choosenPlayers.get(3).getPlayerName().equals("null")) {
						choosenPlayers.remove(3);	
						choosenPlayers.add(3, player);	
					} else if (player.getPlayerPosition().equals("WR") && 
							choosenPlayers.get(4).getPlayerName().equals("null")) {
						choosenPlayers.remove(4);	
						choosenPlayers.add(4, player);	
					} else if (player.getPlayerPosition().equals("WR") && 
							choosenPlayers.get(5).getPlayerName().equals("null")) {
						choosenPlayers.remove(5);	
						choosenPlayers.add(5, player);	
					} else if (player.getPlayerPosition().equals("TE") && 
							choosenPlayers.get(6).getPlayerName().equals("null")) {
						choosenPlayers.remove(6);	
						choosenPlayers.add(6, player);	
					} else if ((player.getPlayerPosition().equals("RB") ||
							player.getPlayerPosition().equals("WR") ||
							player.getPlayerPosition().equals("TE")) && 
							choosenPlayers.get(7).getPlayerName().equals("null")) {
						choosenPlayers.remove(7);	
						choosenPlayers.add(7, player);	
					} else if (player.getPlayerPosition().equals("DST") && 
							choosenPlayers.get(8).getPlayerName().equals("null")) {
						choosenPlayers.remove(8);	
						choosenPlayers.add(8, player);	
					}
				
				}
				
				buildLineupList.setListData(choosenPlayers.toArray());
				updateLabels(buildLineupProjectedPointsLabel, buildLineupSalaryLabel, choosenPlayers);
			}
		});
		
		allPlayersEditButton = new JButton("Edit");
		allPlayersEditButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (allPlayersList.getSelectedIndex() == -1) {
					return;
				}
				Player player = (Player) allPlayersList.getSelectedValue();
				String newScore = JOptionPane.showInputDialog(contentPane, "Enter new projected score.");
				if (newScore != null) {
					if (allPlayersList.getSelectedIndex() != -1) { 
						player.setProjectedScore(Double.parseDouble(newScore));
					} 
				}
				
				allPlayersList.setListData(updateListData());
				buildLineupList.setListData(choosenPlayers.toArray());
				updateLabels(buildLineupProjectedPointsLabel, buildLineupSalaryLabel, choosenPlayers);
			}
		});
		allPlayersPanel.add(allPlayersEditButton);
		
		allPlayersViewButton = new JButton("View");
		allPlayersViewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (allPlayersList.getSelectedIndex() == -1) {
					return;
				}
				Player player = (Player) allPlayersList.getSelectedValue();
				PlayerStats stats = new PlayerStats(player, weeklyGameSchedule);
				stats.setVisible(true);
			}
		});
		allPlayersPanel.add(allPlayersViewButton);
		allPlayersPanel.add(allPlayersAddButton);
		
		allPlayersWeekButton = new JLabel("Week");
		allPlayersPanel.add(allPlayersWeekButton);
		
		allPlayersWeekComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
					football.updateWeek(Integer.toString(allPlayersWeekComboBox.getSelectedIndex() + 1));
					allPlayersList.setListData(updateListData());
					updateLabels(buildLineupProjectedPointsLabel, buildLineupSalaryLabel, choosenPlayers);
					addDefaultPlayers(choosenPlayers);
					buildLineupList.setListData(choosenPlayers.toArray());
					ArrayList<Player> idealList = new ArrayList<Player>();
					addDefaultPlayers(idealList);
					idealLineupList.setListData(idealList.toArray());
					idealLineupSalaryLabel.setText("0");
					idealLineupProjectedPointsLabel.setText("0.0");
					getScheduleForWeek(Integer.toString(allPlayersWeekComboBox.getSelectedIndex() + 1));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
		allPlayersPanel.add(allPlayersWeekComboBox);
		allPlayersScrollPane.setViewportView(allPlayersList);
		
		buildLineupScrollPane = new JScrollPane();
		tabbedPane.addTab("Build a Lineup", null, buildLineupScrollPane, null);
		
		buildLineupPanel = new JPanel();
		buildLineupScrollPane.setColumnHeaderView(buildLineupPanel);
		
		buildLineupScrollPane.setViewportView(buildLineupList);
		
		buildLineupPanel.add(buildLineupLabel1);
		buildLineupPanel.add(buildLineupSalaryLabel);
		buildLineupPanel.add(buildLineupLabel2);
		buildLineupPanel.add(buildLineupLabel3);
		buildLineupPanel.add(buildLineupProjectedPointsLabel);
		
		buildLineupRemovePlayerButton = new JButton("Remove");
		buildLineupRemovePlayerButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {	
				if (buildLineupList.getSelectedIndex() == -1) {
					return;
				}
				String position = choosenPlayers.get(buildLineupList.getSelectedIndex()).getPlayerPosition();
				choosenPlayers.remove(buildLineupList.getSelectedIndex());	
				choosenPlayers.add(buildLineupList.getSelectedIndex(), new Player(position));
				buildLineupList.setListData(choosenPlayers.toArray());
				updateLabels(buildLineupProjectedPointsLabel, buildLineupSalaryLabel, choosenPlayers);
			}
		});
		
		buildlLineupRemoveAllButton = new JButton("Remove All");
		buildlLineupRemoveAllButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				addDefaultPlayers(choosenPlayers);
				buildLineupList.setListData(choosenPlayers.toArray());
				updateLabels(buildLineupProjectedPointsLabel, buildLineupSalaryLabel, choosenPlayers);
			}
		});
		buildLineupPanel.add(buildlLineupRemoveAllButton);
		buildLineupPanel.add(buildLineupRemovePlayerButton);
		
		buildLineupFinishButton = new JButton("Finish");
		buildLineupFinishButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {	
				SwingWorker worker = new SwingWorker() {
					@Override
					protected Object doInBackground() throws Exception {
						buildLineupFinishButton.setText("Generating lineup...");
						ArrayList<Player> idealLineup = football.getIdealLineup(choosenPlayers);
						choosenPlayers.clear();
						choosenPlayers.addAll(idealLineup);
						buildLineupList.setListData(idealLineup.toArray());
						updateLabels(buildLineupProjectedPointsLabel, buildLineupSalaryLabel, idealLineup);
						buildLineupFinishButton.setText("Finish with Ideal Lineup");
						return null;
					}		
				};
				worker.execute();
				
			}
		});
		
		buildLineupViewButton = new JButton("View");
		buildLineupViewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (allPlayersList.getSelectedIndex() == -1) {
					return;
				}
				Player player = (Player) buildLineupList.getSelectedValue();
				if (player.getPlayerName().equals("null")) {
					return;
				}
				PlayerStats stats = new PlayerStats(player, weeklyGameSchedule);
				stats.setVisible(true);
			}
		});
		buildLineupPanel.add(buildLineupViewButton);
		buildLineupPanel.add(buildLineupFinishButton);
		
		tabbedPane.addTab("Ideal Lineup", null, idealLineupPane, null);
		
		idealLineupGenerateButton = new JButton("Generate Ideal Lineup");
		idealLineupGenerateButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				SwingWorker worker = new SwingWorker() {
					@Override
					protected Object doInBackground() throws Exception {
						idealLineupGenerateButton.setText("Generating lineup...");
						ArrayList<Player> idealLineup = football.getIdealLineup(null);
						idealLineupList.setListData(idealLineup.toArray());
						updateLabels(idealLineupProjectedPointsLabel, idealLineupSalaryLabel, idealLineup);
						idealLineupGenerateButton.setText("Generate Ideal Lineup");
						return null;
					}		
				};
				worker.execute();
			}
		});
		
		idealLineupViewButton = new JButton("View");
		idealLineupViewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (allPlayersList.getSelectedIndex() == -1) {
					return;
				}
				Player player = (Player) idealLineupList.getSelectedValue();
				
				if (player.getPlayerName().equals("null")) {
					return;
				}
				PlayerStats stats = new PlayerStats(player, weeklyGameSchedule);
				stats.setVisible(true);
			}
		});
		idealLineupPanel.add(idealLineupViewButton);
		
		idealLineupPanel.add(idealLineupGenerateButton);
		
		allPlayersPositionComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				allPlayersList.setListData(updateListData());
			}
		});
		
		allPlayersSortByComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				allPlayersList.setListData(updateListData());
			}
		});
	}
	
	private void getScheduleForWeek(String week) {
		weeklyGameSchedule.clear();
		for (GameSchedule g : fullGameSchedule) {
			if (g.week.equals(week)) {
				weeklyGameSchedule.add(g);
			}
		}
	}

	private void updateLabels(JLabel projectedPointsLabel, JLabel salaryLabel, ArrayList<Player> list) {
		double projectedPoints = 0.0;
		for (Player s : list) {
			projectedPoints += s.getProjectedScore();
		}
		
		int salary = 0;
		for (Player s : list) {
			salary += s.getSalary();
		}
		
		projectedPointsLabel.setText((projectedPoints * 100) / 100 + "");
		salaryLabel.setText(salary + "");
	}
	
	private Object[] updateListData() {
		Map<String, Player> theList = new HashMap<String, Player>();
		
		switch (allPlayersPositionComboBox.getSelectedIndex()) {
			case 0:
				theList.putAll(football.quarterbackList);
				theList.putAll(football.runningbackList);
				theList.putAll(football.recieverList);
				theList.putAll(football.tightendList);
				//theList.putAll(football.defenseList);
				break;
			case 1:
				theList = football.quarterbackList;
				break;
			case 2:
				theList = football.runningbackList;
				break;
			case 3:
				theList = football.recieverList;
				break;
			case 4:
				theList = football.tightendList;
				break;
			case 5:
				theList = football.flexList;
				break;
			case 6:
				theList = football.defenseList;
				break;
			default: 
				theList = football.quarterbackList;
				break;
		}
		
		switch (allPlayersSortByComboBox.getSelectedIndex()) {
			case 0:
				return football.sortByProjectedPoints(theList).toArray();
			case 1:
				return football.sortBySalary(theList).toArray();
			case 2:
				return football.sortByPointsPerDollar(theList).toArray();
		}
		
		return football.sortByPointsPerDollar(theList).toArray();
	}
	
	public void addDefaultPlayers(ArrayList<Player> choosenPlayers) {
		choosenPlayers.clear();
		choosenPlayers.add(new Player("QB"));
		choosenPlayers.add(new Player("RB"));
		choosenPlayers.add(new Player("RB"));
		choosenPlayers.add(new Player("WR"));
		choosenPlayers.add(new Player("WR"));
		choosenPlayers.add(new Player("WR"));
		choosenPlayers.add(new Player("TE"));
		choosenPlayers.add(new Player("FLEX"));
		choosenPlayers.add(new Player("DST"));
	}

}
