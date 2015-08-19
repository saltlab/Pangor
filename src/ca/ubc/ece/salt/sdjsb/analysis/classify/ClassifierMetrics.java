package ca.ubc.ece.salt.sdjsb.analysis.classify;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Stores a set of metrics for a classifier data set.
 */
public class ClassifierMetrics {

	/** Stores the set of TypeCounts in an accessible manner. **/
	public Map<String, TypeCount> typeMap;

	/** Classifier alert type (e.g., STH, EH, IC, etc.). **/
	public Set<TypeCount> typeCount;

	/** Classifier alert subtype (e.g., STH_TYPE_ERROR_FALSEY, IC_NO_VALUE_TO_UNDEFINED, etc.). **/
	public Set<TypeCount> subtypeCount;

	public ClassifierMetrics() {

		/* All counts are sorted by the number of rows they occur in. */
		Comparator<TypeCount> comparator = new Comparator<TypeCount>() {

			@Override
			public int compare(TypeCount o1, TypeCount o2) {
				if(o1.count == o2.count) return 0;
				else if(o1.count > o2.count) return -1;
				else return 1;
			}

		};

		this.typeMap = new TreeMap<String, TypeCount>();
		this.typeCount = new TreeSet<TypeCount>(comparator);
		this.subtypeCount = new TreeSet<TypeCount>(comparator);

	}

	/**
	 * Adds a type count to the ordered set.
	 * @param type The classifier alert type.
	 * @param count The number of rows the classifier alert appeared in the data set.
	 */
	public void addTypeCount(String typeName, int count) {
		TypeCount typeCount = new TypeCount(typeName, count);
		this.typeMap.put(typeName, typeCount);
		this.typeCount.add(typeCount);
	}

	/**
	 * Adds a subtype count to the ordered set.
	 * @param typeName The classifier alert type.
	 * @param count The number of rows the classifier alert appeared in the data set.
	 */
	public void addSubTypeCount(String subTypeName, int count) {
		String typeName = subTypeName.split("_")[0];
		TypeCount typeCount = this.typeMap.get(typeName);
		typeCount.addSubTypeCount(subTypeName.substring(typeName.length() + 1, subTypeName.length()), count);
	}

	/**
	 * Prints the metrics as a latex table.
	 */
	public String printMetricsAsLatexTable() {
		String output = "";

        output += "\\begin{table*}\n";
        output += "\t\\centering\n";
        output += "\t\\caption{Classifier Evaluation and Results}\n";
        output += "\t\\begin{tabular}{ | c | l | r | r | r | r | r | }\n";
        output += "\t\t\\hline\n";
        output += "\t\t\\textbf{Type} & \\textbf{SubType} & \\textbf{Count} & \\textbf{Sample Size} & \\textbf{TP} & \\textbf{FP} & \\textbf{Precision} \\\\ \\hline\n";

		for(TypeCount typeCount : this.typeCount) {

			output += "\t\t\\multirow{" + (typeCount.subTypes.size() + 1) + "}{*}{" + typeCount.typeName + "}\n";

			for(SubTypeCount subTypeCount : typeCount.subTypes) {

				output += "\t\t& " + subTypeCount.subTypeName
						+ " & " + subTypeCount.count
						+ " & "
						+ " & "
						+ " & "
						+ " & "
						+ " \\\\ \n";

			}

			output += "\t\t& \\textbf{Total}"
					+ " & \\textbf{" + typeCount.count + "}"
					+ " & "
					+ " & "
					+ " & "
					+ " & "
					+ " \\\\ \n";

			output += "\t\t\\hline\n";
		}

        output += "\t\\end{tabular}\n";
        output += "\t\\label{tbl:classifierEvaluation}\n";
        output += "\\end{table*}\n";

		return output.replaceAll("_", "\\\\_");
	}

	/**
	 * Keeps track of the number of rows in which the type appears.
	 */
	public class TypeCount {
		public String typeName;
		public Set<SubTypeCount> subTypes;
		public int count;

		public TypeCount(String typeName, int count) {

			/* All counts are sorted by the number of rows they occur in. */
			Comparator<SubTypeCount> comparator = new Comparator<SubTypeCount>() {

				@Override
				public int compare(SubTypeCount o1, SubTypeCount o2) {
					if(o1.count == o2.count) return 0;
					else if(o1.count > o2.count) return -1;
					else return 1;
				}

			};

			this.typeName = typeName;
			this.subTypes = new TreeSet<SubTypeCount>(comparator);
			this.count = count;
		}

		/**
		 * Add a sub type count to the type count.
		 * @param subTypeName
		 * @param count
		 */
		public void addSubTypeCount(String subTypeName, int count) {
			this.subTypes.add(new SubTypeCount(subTypeName, count));
		}

		@Override
		public String toString() {
			return this.typeName;
		}
	}

	/**
	 * Keeps track of the number of rows in which the subtype appears.
	 */
	public class SubTypeCount {
		public String subTypeName;
		public int count;

		public SubTypeCount(String subTypeName, int count) {
			this.subTypeName = subTypeName;
			this.count = count;
		}

		@Override
		public String toString() {
			return this.subTypeName;
		}
	}

}
