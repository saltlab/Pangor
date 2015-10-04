setwd("/home/brito/salt/JSRepairClass/input/applications/")
data <- read.csv("repositories_metrics.csv")
hist(data$TotalCommits, main = paste("Histogram for ", nrow(data), "node.js applications"), xlab = "Total number of commits")

setwd("/home/brito/salt/JSRepairClass/input/modules/")
data <- read.csv("repositories_metrics.csv")
hist(data$TotalCommits, main = paste("Histogram of", nrow(data), "top rated node.js modules (from npmjs.com)"), xlab = "Total number of commits")
