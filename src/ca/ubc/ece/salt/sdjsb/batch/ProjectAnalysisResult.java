package ca.ubc.ece.salt.sdjsb.batch;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import ca.ubc.ece.salt.sdjsb.alert.Alert;

/**
 * Maintains the set of alerts for a project analysis.
 * 
 * Since alerts are generated in any order, AlertSet maintains an ordered
 * list of alerts for a project.
 */
public class ProjectAnalysisResult {
	
	private String projectName;
	private int totalCommits;
	private int bugFixingCommits;
	
	private TreeMap<String, Set<String>> subtypes; // The map of a type to its subtypes.
	private TreeMap<String, List<Alert>> alerts; // The map of a subtype to its alerts.
	
	/* Keep alerts in alphabetical order according to type then subtype. */
	private Comparator<String> comparator = new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return a.compareTo(b);
			}
		};

	public ProjectAnalysisResult(String projectName) {
		this.projectName = projectName;
		this.totalCommits = 0;
		this.bugFixingCommits = 0;
		this.subtypes = new TreeMap<String, Set<String>>(this.comparator);
		this.alerts = new TreeMap<String, List<Alert>>(this.comparator);
	}
	
	
	/**
	 * @return The number of alerts with the given type.
	 */
	public int countAlertsForType(String type) {

		int ctr = 0;
		Set<String> subtypes = this.subtypes.get(type);
		
		if(subtypes == null) return 0;

		for(String subtype : subtypes) {
			 ctr += this.alerts.get(subtype).size();
		}
		
		return ctr;
		
	}
	
	/**
	 * @return The number of alerts with the given subtype.
	 */
	public int countAlertsForSubType(String subtype) {

		List<Alert> alerts = this.alerts.get(subtype);
		if(alerts == null) return 0;
		return alerts.size();
	}
	
	public Set<String> getTypes() {
		return this.subtypes.keySet();
	}
	
	/**
	 * @return The list of subtypes for a given type.
	 */
	public Set<String> getSubtypesOf(String type) {
		return this.subtypes.get(type);
	}
	
	/**
	 * @return The list of alerts for a given subtype.
	 */
	public List<Alert> getAlertsOfSubType(String subtype) {
		return this.alerts.get(subtype);
	}
	
	/**
	 * Inserts all alerts into the alert set.
	 * @param alert
	 */
	public void insertAll(List<Alert> alerts) {
		
		for(Alert alert : alerts) {
			this.insert(alert);
		}
		
	}
	
	/**
	 * Insert an alert into the alert set.
	 * @param alert
	 */
	public void insert(Alert alert) {
		String type = alert.getType();
		String subtype = alert.getType() + "_" + alert.getSubType();
		
		/* Add the subtype to the type if it isn't there yet. 
		 * 
		 * We could pre-compute this set using the checker types that 
		 * have been added to save time, but right now we don't know what
		 * subtypes are provided. Better to do it afterwards. */
		if(this.subtypes.containsKey(type)) {
			this.subtypes.get(type).add(subtype);
		}
		else {
			Set<String> subtypes = new TreeSet<String>(this.comparator);
			subtypes.add(subtype);
			this.subtypes.put(type, subtypes);
		}
		
		/* Add the alert to the set of alerts for the subtype. */
		if(this.alerts.containsKey(subtype)) {
			this.alerts.get(subtype).add(alert);
		}
		else {
			List<Alert> subtypes = new LinkedList<Alert>();
			subtypes.add(alert);
			this.alerts.put(subtype, subtypes);
		}
	}

	/**
	 * @return The project name.
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return The number of commits in the project.
	 */
	public int getTotalCommits() {
		return totalCommits;
	}
	
	/**
	 * @param totalCommits The number of commits in the project.
	 */
	public void setTotalCommits(int totalCommits) {
		this.totalCommits = totalCommits;
	}

	/**
	 * @return The number of bug fixing commits in the project.
	 */
	public int getBugFixingCommits() {
		return bugFixingCommits;
	}
	
	/**
	 * @param bugFixingCommits The number of bug fixing commits in the project.
	 */
	public void setBugFixingCommits(int bugFixingCommits) {
		this.bugFixingCommits = bugFixingCommits;
	}
	
}
