package ca.ubc.ece.salt.pangor.learning.pointsto;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * A PriorityQueue to store all the PredictionResult. It sorts them on the
 * reverse order, so the prediction on the top is the most likely to be right.
 */
public class PredictionResults extends PriorityQueue<PredictionResult> {
	public PredictionResults() {
		super(new ReversePredictionResultComparator());
	}
}

/**
 * A custom comparator to sort our PredictionResults in reverse order
 */
class ReversePredictionResultComparator implements Comparator<PredictionResult> {
	@Override
	public int compare(PredictionResult o1, PredictionResult o2) {
		if (o2.likelihood > o1.likelihood)
			return 1;
		else if (o2.likelihood < o1.likelihood)
			return -1;
		else
			return 0;
	}

}