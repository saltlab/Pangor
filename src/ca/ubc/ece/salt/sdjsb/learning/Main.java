package ca.ubc.ece.salt.sdjsb.learning;

import java.io.FileWriter;
import java.io.PrintWriter;

import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.converters.ConverterUtils.DataSource;
import weka.clusterers.Clusterer;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		/* Establish a connection to the data file. */
		DataSource source = new DataSource("./output/bulk1.arff");
		Instances data = source.getDataSet();
		
		/* Filter out the columns we don't want. */
		String[] removeOptions = new String[2];
		removeOptions[0] = "-R";
		removeOptions[1] = "1-7";
		Remove remove = new Remove();
		remove.setOptions(removeOptions);
		remove.setInputFormat(data);
		Instances filteredData = Filter.useFilter(data, remove);
		
		/* Cluster the filteredData and store the cluster for each row. */
		
		/* K-Means Clusterer. We probably want to use Manhattan Distance as the
		 * distance function. This computes the closeness by computing the
		 * difference between points in their individual dimensions, then
		 * summing the distance. This is important because if we use Euclidean
		 * distance, we'll get clusters of unrelated keywords. */
		
		ManhattanDistance distanceFunction = new ManhattanDistance();
		String[] distanceFunctionOptions = "-R first-last".split("\\s");
		distanceFunction.setOptions(distanceFunctionOptions);
				
		SimpleKMeans kMeansClusterer = new SimpleKMeans();
		String[] kMeansClustererOptions = "-init 0 -max-candidates 100 -periodic-pruning 10000 -min-density 2.0 -t1 -1.25 -t2 -1.0 -N 50 -I 500 -O -num-slots 1 -S 10".split("\\s");
		kMeansClusterer.setOptions(kMeansClustererOptions);
		kMeansClusterer.setDistanceFunction(distanceFunction);
		kMeansClusterer.buildClusterer(filteredData);
		
		/* EM Clusterer. */
//		EM emClusterer = new EM();
//		String[] emClustererOptions = "-I 100 -N 50 -X 10 -max -1 -ll-cv 1.0E-6 -ll-iter 1.0E-6 -M 1.0E-6 -K 10 -num-slots 1 -S 100".split("\\s");
//		emClusterer.setOptions(emClustererOptions);
//		emClusterer.buildClusterer(filteredData);
		
		/* Which clusterer do you want to use? */
		SimpleKMeans clusterer = kMeansClusterer;
		
		/* Print the results. */
		PrintWriter pw = new PrintWriter(new FileWriter("./output/clusters.csv"));
		
		double[] clusterSizes = clusterer.getClusterSizes();
		for(int i = 0; i < clusterSizes.length; i++) {
			pw.println("Cluster " + i + " has " + clusterSizes[i] + " instances.");
		}
		
		int[] assignments = clusterer.getAssignments();
		for(int i = 0; i < filteredData.size(); i++) {
			pw.print(data.instance(i).toString(0));
			pw.print("," + data.instance(i).stringValue(1));
			pw.print("," + data.instance(i).stringValue(2));
			pw.print("," + data.instance(i).stringValue(3));
			pw.print("," + data.instance(i).toString(4));
			pw.print("," + data.instance(i).toString(5));
			pw.print("," + data.instance(i).toString(6));
			pw.print("," + assignments[i]);
			pw.print("\n");
		}
		pw.close();
	}

}
