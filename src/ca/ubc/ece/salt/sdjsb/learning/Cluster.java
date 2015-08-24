package ca.ubc.ece.salt.sdjsb.learning;

import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordUse;

public class Cluster {

	public KeywordUse keyword;
	public int cluster;
	public int instances;

	public Cluster(KeywordUse keyword, int cluster, int instances) {
		this.keyword = keyword;
		this.cluster = cluster;
		this.instances = instances;
	}

	@Override
	public String toString() {
		return keyword.toString() + ": C = " + cluster + ", I = " + instances;
	}

}
