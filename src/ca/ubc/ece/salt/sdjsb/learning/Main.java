package ca.ubc.ece.salt.sdjsb.learning;

import java.io.FileWriter;
import java.io.PrintWriter;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.clusterers.SimpleKMeans;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		/* Establish a connection to the data file. */
		DataSource source = new DataSource("./output/run1.arff");
		Instances data = source.getDataSet();
		
		/* Filter out the columns we don't want. */
		String[] removeOptions = new String[2];
		removeOptions[0] = "-R";
		removeOptions[1] = "1,3,4";
		Remove remove = new Remove();
		remove.setOptions(removeOptions);
		remove.setInputFormat(data);
		Instances filteredData = Filter.useFilter(data, remove);
		
		/* Cluster the filteredData and store the cluster for each row. */
		String[] options = new String[5];
		options[0] = "-O";	// Preserve order of instances
		options[1] = "-S";	// Seed
		options[2] = "1";
		options[3] = "-N"; 	// Number of clusters
		options[4] = "5";
		SimpleKMeans clusterer = new SimpleKMeans();
		clusterer.setOptions(options);
		clusterer.buildClusterer(filteredData);
		
		/* Print the results. */
		PrintWriter pw = new PrintWriter(new FileWriter("./output/clusters.csv"));
		int[] assignments = clusterer.getAssignments();
		for(int i = 0; i < filteredData.size(); i++) {
			pw.print(data.instance(i).stringValue(1));
			pw.print("," + data.instance(i).stringValue(2));
			pw.print("," + data.instance(i).stringValue(3));
			pw.print("," + assignments[i]);
			pw.print("\n");
		}
		pw.close();
	}

}
