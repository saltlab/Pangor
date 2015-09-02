=======
## README ##

Pangor is a static analyis framework for discovering and analyzing the pervasive bug patterns of JavaScript. It is used as part of our study [Mining the Pervasive and Detectable Bug Patterns of JavaScript](htttp://salt.ece.ubc.ca/software/pangor/).

Pangor contains two analysis types: keyword change analysis and flow analysis.

## Keyword Change Analysis ##

The keyword change analysis component has two parts. The first builds a data set of keyword changes in bug fixing commits. The second creates a set of clusters from the data set. Clusters contain similar bug patterns.

### Data Set Construction ###

* Analysis Main: ca.ubc.ece.salt.pangor.learning.LearningAnalysisMain

### Cluster Construction ###

* Cluster Creation & Metrics: ca.ubc.ece.salt.pangor.learning.LearningDataSetMain

## Static Analysis ##

The static analysis component detects instances of bug patterns in source code by using AST or flow analysis to inspect the repaired file.

### Analysis ###

To run Pangor's Checker from the command line:
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

Through Maven:
```bash
mvn exec:java -Dexec.mainClass="ca.ubc.ece.salt.pangor.ClassifyAnalysisMain" -Dexec.args="-h"
```

ClassifyAnalysisMain creates the following artifacts:

* `dataset.csv`: The data set containing the detected instances of the bug patterns.
* `supplementary/`: The buggy and repaired source code files for each instance.

### Duplicate Filtering and Metrics ###

```bash
java ca.ubc.ece.salt.pangor.ClassifyDataSetMain -h
```

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
