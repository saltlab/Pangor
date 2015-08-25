package ca.ubc.ece.salt.sdjsb.learning;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordUse;

public class ClusterMetrics {

	/** The keyword which produced these clusters. **/
	public KeywordUse keyword;

	/** Stores the clusters. **/
	public List<Cluster> clusters;

	/** The total number of instances that were clustered. **/
	public int totalInstances;

	/** The average number of instances in a cluster. **/
	public double avgInstances;

	/** The median number of instances in a cluster. **/
	public int mdnInstances;

	public ClusterMetrics(KeywordUse keyword) {
		this.keyword = keyword;
		this.clusters = new LinkedList<Cluster>();
		this.totalInstances = 0;
		this.avgInstances = 0;
		this.mdnInstances = 0;
	}

	/**
	 * Add the cluster and re-compute the metrics.
	 * @param cluster The cluster to add.
	 */
	public void addCluster(Cluster cluster) {
		this.clusters.add(cluster);

		this.totalInstances = 0;
		for(Cluster c : this.clusters) {
			this.totalInstances += c.instances;
		}

		this.avgInstances = this.totalInstances / this.clusters.size();
		this.mdnInstances = this.clusters.get(this.clusters.size()/2).instances;
	}

	/**
	 * Prints a LaTex table from an ordered set of {@code ClusterMetrics}.
	 * @param metrics
	 * @return
	 */
	public static String getLatexTable(Set<ClusterMetrics> metrics) {
		String table = "\\begin{table*}\n";
		table += "\t\\centering\n";
		table += "\t\\caption{Clustering and Inspection Results}\n";
		table += "\t\\label{tbl:clusteringResults}\n";
		table += "{\\scriptsize\n";
		table += "\t\\begin{tabular}{ | l | r | r | r | r | r | r | }\n";
		table += "\t\t\\hline\n";
		table += "\t\t\\textbf{Keyword} & \\textbf{TotC} & \\textbf{Clusters} & \\textbf{AvgI} & \\textbf{MdnI. Size} & \\textbf{BG} & \\textbf{RG} \\\\ \\hline\n";

		for(ClusterMetrics metric : metrics) {
			table += "\t\t" + metric.keyword + " & " + metric.totalInstances + " & " + metric.clusters.size() + " & " + Math.round(metric.avgInstances) + " & " + metric.mdnInstances + " & & \\\\\n";
		}

		table += "\t\t\\hline\n";
		table += "\t\\end{tabular}\n";
		table += "}\n";
		table += "\\end{table*}\n";
		return table.replace("_", "\\_");
	}

	@Override
	public String toString() {
		return keyword.toString() + ": C = " + clusters + ", I = " + totalInstances + ", AVGI = " + avgInstances + ", MDNI = " + mdnInstances;
	}

}
