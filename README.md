=======
## README ##

Pangor is a static analyis framework for discovering and analyzing the pervasive bug patterns of JavaScript. For details on Pangor, see our method description and empirical study [Mining the Pervasive and Detectable Bug Patterns of JavaScript](http://salt.ece.ubc.ca/software/pangor/).

Pangor contains two analysis types: keyword change analysis and instance checker analysis.

## Keyword Change Analysis ##

The keyword change analysis component has two parts. The first builds a data set of keyword changes in bug fixing commits. The second creates a set of clusters from the data set. Clusters contain similar bug patterns.

### Data Set Construction ###

To run Pangor's keyword change analysis from the command line:
```bash
java ca.ubc.ece.salt.pangor.learning.LearningAnalysisMain -h

Usage: DataSetMain  [-cc (--complexity) N] [-d (--directory) VAL] [-ds (--dataset) VAL] [-h (--help)] [-r (--repositories) VAL] [-s (--supplement) VAL] [-tr (--threads) N] [-u (--uri) VAL]

 -cc (--complexity) N    : The maximum change complexity of a file to analyze.
 -d (--directory) VAL    : The git directory (e.g., /path/to/project/.git/).
 -ds (--dataset) VAL     : The data set file to read.
 -h (--help)             : Display the help file.
 -r (--repositories) VAL : The path to the file specifying the repositories to analyze.
 -s (--supplement) VAL   : The folder path to place any supplementary files.
 -tr (--threads) N       : The number of threads to be used.
 -u (--uri) VAL          : The uri of the public repository (e.g., https://github.com/qhanam/JSRepairClass.git).
```

Through Maven:
```bash
mvn exec:java -Dexec.mainClass="ca.ubc.ece.salt.pangor.ClassifyAnalysisMain" -Dexec.args="-h"
```

### Cluster Construction ###

To run Pangor's clustering and keyword metrics program:
```bash
java ca.ubc.ece.salt.pangor.learning.LearningDataSetMain -h

Usage: DataSetMain  [-a (--arff-path) VAL] [-c (--clusters)] [-ds (--dataset) VAL] [-f (--filtered) VAL] [-h (--help)] [-m (--metrics)]

 -a (--arff-path) VAL : Folder to write the ARFF files.
 -c (--clusters)      : Print the clusters from the data set.
 -ds (--dataset) VAL  : The data set file to read.
 -f (--filtered) VAL  : The file to write the filtered data set to.
 -h (--help)          : Display the help file.
 -m (--metrics)       : Print the metrics from the data set.
```

## Instance Checker Analysis ##

The instance checker analysis component also has two parts. The first builds a data set of bug pattern instance alerts. The second filters out duplicates from the data set and computes metrics (i.e., counts each type of alert).

### Analysis ###

To run Pangor's checker from the command line:
```bash
java ca.ubc.ece.salt.pangor.classify.ClassifyAnalysisMain -h

Usage: ClassifyMain  [-ds (--dataset) VAL] [-h (--help)] [-pp (--preprocess)] [-r (--repositories) VAL] [-s (--supplement) VAL] [-tr (--threads) N] [-u (--uri) VAL]

 -ds (--dataset) VAL     : The data set file to write to.
 -h (--help)             : Display the help file.
 -pp (--preprocess)      : Pre-process the AST before running GumTree. Expands ternary operators and short circuits.
 -r (--repositories) VAL : The path to the file specifying the list of repositories to analyze.
 -s (--supplement) VAL   : The folder path to place the supplementary files.
 -tr (--threads) N       : The number of threads to be used.
 -u (--uri) VAL          : The uri of the public repository (e.g., https://github.com/qhanam/JSRepairClass.git).
```

ClassifyAnalysisMain creates the following artifacts:

* `dataset.csv`: The data set containing the detected instances of the bug patterns.
* `supplementary/`: The buggy and repaired source code files for each instance.

### Duplicate Filtering and Metrics ###

To run Pangor's checker duplicate filtering and metrics program from the command line:
```bash
java ca.ubc.ece.salt.pangor.classify.ClassifyDataSetMain -h

Usage: DataSetMain  [-ds (--dataset) VAL] [-f (--filtered) VAL] [-h (--help)] [-m (--metrics)]

 -ds (--dataset) VAL : The data set file to read.
 -f (--filtered) VAL : The file to write the filtered data set to.
 -h (--help)         : Display the help file.
 -m (--metrics)      : Print the metrics from the data set.
```

ClassifyDataSetMain creates the following artifacts:

* `filtered.csv`: The data set with duplicate alerts removed.

### Installation ###

Pangor is a Maven project.

* Clone Pangor.
* Build and install the project (`mvn clean install`).

Optional: Create the Eclipse project files (`mvn eclipse:eclipse`).

### Directory Structure ###

* `src\`: The source code for the Pangor framework.
* `test\`: Testing files for the Pangor framework.
    * `src\`: The source code for the Pangor framwork tests.
    * `input\`: JavaScript files used in the Pangor framework tests.
* `tools\`: Scripts for inspecting bug pattern clusters.
* `input\`: Contains a repository list for input to LearningAnalysisMain and ClassifyAnalysisMain.
