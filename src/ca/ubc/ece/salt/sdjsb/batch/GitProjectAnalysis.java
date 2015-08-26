package ca.ubc.ece.salt.sdjsb.batch;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import ca.ubc.ece.salt.sdjsb.git.GitProject;

/**
 * Performs analysis on a Git project using an AnalysisRunner
 */
public class GitProjectAnalysis extends GitProject {

	/** Runs an analysis on a source file. **/
	private AnalysisRunner runner;

	/**
	 * Constructor that is used by our static factory methods.
	 */
	protected GitProjectAnalysis(GitProject gitProject, AnalysisRunner runner) {
		super(gitProject);
		this.runner = runner;
	}

	/**
	 * Analyze the repository (extract repairs).
	 *
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public void analyze() throws GitAPIException, IOException, Exception {
		long startTime = System.currentTimeMillis();
		logger.info("[START ANALYSIS] {}", this.getURI());

		/* Get the list of bug fixing commits from version history. */
		List<Pair<String, String>> bugFixingCommits = this.getBugFixingCommitPairs();

		logger.info(" [ANALYZING] {} bug fixing commits", bugFixingCommits.size());

		/* Analyze the changes made in each bug fixing commit. */
		for(Pair<String, String> bugFixingCommit : bugFixingCommits) {

			this.analyzeDiff(bugFixingCommit.getLeft(), bugFixingCommit.getRight());
		}

		long endTime = System.currentTimeMillis();
		logger.info("[END ANALYSIS] {}. Time (in seconds): {} ", this.getURI(), (endTime - startTime) / 1000.0);
	}

	/**
	 * Extract the source files from Git and analyze them with the analysis
	 * runner.
	 *
	 * @param buggyRevision The hash that identifies the buggy revision.
	 * @param bugFixingRevision The hash that identifies the fixed revision.
	 * @throws IOException
	 * @throws GitAPIException
	 */
	private void analyzeDiff(String buggyRevision, String bugFixingRevision) throws IOException, GitAPIException, Exception {

		ObjectId buggy = this.repository.resolve(buggyRevision + "^{tree}");
		ObjectId repaired = this.repository.resolve(bugFixingRevision + "^{tree}");

		ObjectReader reader = this.repository.newObjectReader();

		CanonicalTreeParser buggyTreeIter = new CanonicalTreeParser();
		buggyTreeIter.reset(reader, buggy);

		CanonicalTreeParser repairedTreeIter = new CanonicalTreeParser();
		repairedTreeIter.reset(reader, repaired);

		DiffCommand diffCommand = this.git.diff().setShowNameAndStatusOnly(true).setOldTree(buggyTreeIter).setNewTree(repairedTreeIter);

		List<DiffEntry> diffs = diffCommand.call();

		for(DiffEntry diff : diffs) {


			if(diff.getOldPath().matches("^.*\\.js$") && diff.getNewPath().matches("^.*\\.js$")){

				/* Skip jquery files. */
				if (diff.getOldPath().matches("^.*jquery.*$") || diff.getNewPath().matches("^.*jquery.*$")) {
					logger.info("[SKIP_FILE] jquery file: " + diff.getOldPath());
					continue;
				}

				/* Skip minified files. */
				if (diff.getOldPath().endsWith(".min.js") || diff.getNewPath().endsWith(".min.js")) {
					logger.info("[SKIP_FILE] Skipping minifed file: " + diff.getOldPath());
					return;
				}

				logger.debug("Exploring diff \n {} \n {} - {} \n {} - {}", getURI(), buggyRevision, diff.getOldPath(),
						bugFixingRevision, diff.getNewPath());

                String oldFile = this.fetchBlob(buggyRevision, diff.getOldPath());
                String newFile = this.fetchBlob(bugFixingRevision, diff.getNewPath());

                try {
                	AnalysisMetaInformation ami = new AnalysisMetaInformation(
                			this.totalCommits, this.bugFixingCommits,
                			this.projectID,
                			this.projectHomepage,
                			diff.getOldPath(), diff.getNewPath(),
                			buggyRevision, bugFixingRevision,
                			oldFile, newFile);
                	runner.analyzeFile(ami);
                }
                catch(Exception ignore) {
                	System.err.println("Ignoring exception in ProjectAnalysis.runSDJSB.\nBuggy Revision: " + buggyRevision + "\nOld File: " + diff.getOldPath() + "\nBug Fixing Revision: " + bugFixingRevision + "\nNew File:" + diff.getNewPath());
                	System.out.println(oldFile);
                	System.out.println(newFile);
                	throw ignore;
                }
                catch(Error e) {
                	System.err.println("Ignoring error in ProjectAnalysis.runSDJSB.\nBuggy Revision: " + buggyRevision + "\nOld File: " + diff.getOldPath() + "\nBug Fixing Revision: " + bugFixingRevision + "\nNew File:" + diff.getNewPath());
                	System.out.println(oldFile);
                	System.out.println(newFile);
                	throw e;
                }
			}
		}

	}

	/**
	 * Fetches the string contents of a file from a specific revision. from
	 * http://stackoverflow.com/questions/1685228/how-to-cat-a-file-in-jgit
	 *
	 * @param repo The repository to fetch the file from.
	 * @param revSpec The commit id.
	 * @param path The path to the file.
	 * @return The contents of the text file.
	 * @throws MissingObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws IOException
	 */
	private String fetchBlob(String revSpec, String path) throws MissingObjectException, IncorrectObjectTypeException, IOException {

		// Resolve the revision specification
	    final ObjectId id = this.repository.resolve(revSpec);

        // Makes it simpler to release the allocated resources in one go
        ObjectReader reader = this.repository.newObjectReader();

        try {
            // Get the commit object for that revision
            RevWalk walk = new RevWalk(reader);
            RevCommit commit = walk.parseCommit(id);

            // Get the revision's file tree
            RevTree tree = commit.getTree();
            // .. and narrow it down to the single file's path
            TreeWalk treewalk = TreeWalk.forPath(reader, path, tree);

            if (treewalk != null) {
                // use the blob id to read the file's data
                byte[] data = reader.open(treewalk.getObjectId(0)).getBytes();
                return new String(data, "utf-8");
            } else {
                return "";
            }
        } finally {
            reader.release();
        }
    }

	/*
	 * Static factory methods
	 */

	/**
	 * Creates a new GitProjectAnalysis instance from a git project directory.
	 *
	 * @param directory The base directory for the project.
	 * @return An instance of GitProjectAnalysis.
	 * @throws GitProjectAnalysisException
	 */
	public static GitProjectAnalysis fromDirectory(String directory, String name, AnalysisRunner runner)
			throws GitProjectAnalysisException {
		GitProject gitProject = GitProject.fromDirectory(directory, name);

		return new GitProjectAnalysis(gitProject, runner);
	}

	/**
	 * Creates a new GitProjectAnalysis instance from a URI.
	 *
	 * @param uri The remote .git address.
	 * @param directory The directory that stores the cloned repositories.
	 * @return An instance of GitProjectAnalysis.
	 * @throws GitAPIException
	 * @throws TransportException
	 * @throws InvalidRemoteException
	 */
	public static GitProjectAnalysis fromURI(String uri, String directory, AnalysisRunner runner)
			throws GitProjectAnalysisException, InvalidRemoteException, TransportException, GitAPIException {
		GitProject gitProject = GitProject.fromURI(uri, directory);

		return new GitProjectAnalysis(gitProject, runner);
	}

}
