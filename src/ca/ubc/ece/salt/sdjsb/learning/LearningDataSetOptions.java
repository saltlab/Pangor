package ca.ubc.ece.salt.sdjsb.learning;

import org.kohsuke.args4j.Option;

public class LearningDataSetOptions {

	@Option(name="-h", aliases={"--help"}, usage="Display the help file.")
	private boolean help = false;
	
	@Option(name="-ds", aliases={"--dataset"}, usage="The data set file to read.")
	private String dataSetPath = null;
	
	@Option(name="-f", aliases={"--filtered"}, usage="The file to write the filtered data set to.")
	private String filteredPath = null;
	
	public boolean getHelp() {
		return this.help;
	}
	
	public String getDataSetPath() {
		return this.dataSetPath;
	}
	
	public String getFilteredPath() {
		return this.filteredPath;
	}

}