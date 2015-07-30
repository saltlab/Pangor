package ca.ubc.ece.salt.sdjsb.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import ca.ubc.ece.salt.sdjsb.batch.GitProjectAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.GitProjectAnalysisException;

/**
 * Represents a Git project and some metrics
 */
public class GitProject {
	/** The Git instance. **/
	protected Git git;

	/** The repository instance. **/
	protected Repository repository;

	/** The repository name. **/
	protected String projectID;

	/** The URI **/
	protected String URI;

	/** The number of bug fixing commits analyzed. **/
	protected Integer bugFixingCommits;

	/** The total number of commits inspected. **/
	protected Integer totalCommits;

	/** The number of commit authors (uniquely identified by their emails) */
	protected Integer numberAuthors;

	/** The number of javascript files */
	protected Integer numberOfFiles;

	/** The number of javascript lines of code */
	protected Integer numberOfLines;

	/** The number of downloads over the last month */
	protected Integer downloadsLastMonth = -1;

	/** The number of stargazers on GitHub */
	protected Integer stargazers = -1;

	/** Dates of last (most recent) and first commit */
	protected Date lastCommitDate, firstCommitDate;

	/**
	 * Constructor that is used by our static factory methods.
	 */
	protected GitProject(Git git, Repository repository, String URI) {
		this.git = git;
		this.repository = repository;
		this.URI = URI;

		try {
			this.projectID = getGitProjectName(URI);
		} catch (GitProjectAnalysisException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor that clones another Git Project. Particularly useful for
	 * subclasses
	 */
	protected GitProject(GitProject project) {
		this(project.git, project.repository, project.URI);
	}

	/*
	 * Getters for the metrics
	 */
	public String getName() {
		return this.projectID;
	}

	public String getURI() {
		return this.URI;
	}

	public Integer getTotalCommits() {
		if (this.totalCommits == null)
			getBugFixingCommitPairs();

		return this.totalCommits;
	}

	public Integer getBugFixingCommits() {
		if (this.bugFixingCommits == null)
			getBugFixingCommitPairs();

		return this.bugFixingCommits;
	}

	public Integer getNumberAuthors() {
		if (this.numberAuthors == null)
			getBugFixingCommitPairs();

		return this.numberAuthors;
	}

	public Date getLastCommitDate() {
		if (this.lastCommitDate == null)
			getBugFixingCommitPairs();

		return this.lastCommitDate;
	}

	public Date getFirstCommitDate() {
		if (this.firstCommitDate == null)
			getBugFixingCommitPairs();

		return this.firstCommitDate;
	}


	public Integer getNumberOfFiles() {
		if (this.numberOfFiles == null)
			getFilesMetrics();

		return this.numberOfFiles;
	}

	public Integer getNumberOfLines() {
		if (this.numberOfFiles == null)
			getFilesMetrics();

		return numberOfLines;
	}

	public Integer getDownloadsLastMonth() {
		return this.downloadsLastMonth;
	}

	public void setDownloadsLastMonth(Integer downloadsLastMonth) {
		this.downloadsLastMonth = downloadsLastMonth;
	}

	public Integer getStargazers() {
		// Not a github project
		if (!this.URI.contains("github.com"))
			return -1;

		// Value not cached
		if (this.stargazers == -1) {

			try {
				GitHub github = GitHub.connectAnonymously();

				// Really dirty way of getting username/reponame from URI
				GHRepository repository = github.getRepository(this.URI.split("github\\.com/")[1].split("\\.git")[0]);
				this.stargazers = repository.getWatchers();
			} catch (IOException e) {
				System.err.println("Error while accessing GitHub API: " + e.getMessage());
				return -1;
			}
		}

		return this.stargazers;
	}

	/**
	 * Uses the command line tool "ohcount" to get the number of javascript
	 * files and the number of javascript lines of code on the repository
	 */
	protected void getFilesMetrics() {
		Runtime runtime = Runtime.getRuntime();
		Process process;

		String[] command = { "/bin/sh", "-c", "ohcount " + repository.getDirectory().getParent().toString()
				+ " | grep javascript | tr -s ' ' | cut -d ' ' -f 2,3" };

		try {
			/* Check if ohcount is available */
			process = runtime.exec("which ohcount");
			process.waitFor();
			if (process.exitValue() != 0)
				throw new RuntimeException("Could not find ohcount command tool. Perphaphs not installed?");


			/* Run command */
			process = runtime.exec(command);
			process.waitFor();

			/* Get the output */
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String[] output = bufferedReader.readLine().split(" ");

			this.numberOfFiles = Integer.parseInt(output[0]);
			this.numberOfLines = Integer.parseInt(output[1]);

			bufferedReader.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();

			this.numberOfFiles = 0;
			this.numberOfLines = 0;
		}
	}

	/**
	 * Extracts revision identifier pairs from bug fixing commits. The pair
	 * includes the bug fixing commit and the previous (buggy) commit.
	 *
	 * TODO:
	 * This method is also the main method for calculating metrics. Ideally we
	 * would have different methods for metrics and for returning the bug fixing
	 * commit pairs, but we would have to iterate over all commits twice, so for
	 * now, everything is done on this method.
	 *
	 * @param git The project git instance.
	 * @param repository The project git repository.
	 * @param buggyRevision The hash that identifies the buggy revision.
	 * @param bugFixingRevision The hash that identifies the fixed revision.
	 * @throws IOException
	 * @throws GitAPIException
	 */
	protected List<Pair<String, String>> getBugFixingCommitPairs() {
		List<Pair<String, String>> bugFixingCommits = new LinkedList<Pair<String, String>>();
		String bugFixingCommit = null;
		int bugFixingCommitCounter = 0, commitCounter = 0;

		Set<String> authorsEmails = new HashSet<>();

		Date lastCommitDate = null;
		Date firstCommitDate = null;

		/*
		 * Call git log command
		 */
		Iterable<RevCommit> commits;
		try {
			commits = git.log().call();
		} catch (GitAPIException e) {
			e.printStackTrace();
			return bugFixingCommits;
		}

		/* Starts with the most recent commit and goes back in time. */
		for (RevCommit commit : commits) {
			/*
			 * Add author to authors list
			 */
			PersonIdent authorIdent = commit.getAuthorIdent();
			authorsEmails.add(authorIdent.getEmailAddress());

			/*
			 * If last commit was a bug fixing one, add it and current to list
			 */
			if (bugFixingCommit != null) {
				bugFixingCommits.add(Pair.of(commit.name(), bugFixingCommit));
				bugFixingCommit = null;
				bugFixingCommitCounter++;
			}

			String message = commit.getFullMessage();
			Pattern p = Pattern.compile("fix|repair", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(message);
			commitCounter++;

			/*
			 * If the commit message contains one of our fix keywords, store it.
			 */
			if (m.find()) {
				bugFixingCommit = commit.name();
			}

			/*
			 * First commit on iteration is most recent one (what we call "last")
			 */
			if (commitCounter == 1)
				lastCommitDate = authorIdent.getWhen();

			/*
			 * Store the date of this commit. When iteration is over, we have
			 * the date for first one
			 */
			firstCommitDate = authorIdent.getWhen();

		}

		/* Keep track of the number of commits and other metrics for reporting. */
		this.bugFixingCommits = bugFixingCommitCounter;
		this.totalCommits = commitCounter;
		this.numberAuthors = authorsEmails.size();
		this.lastCommitDate = lastCommitDate;
		this.firstCommitDate = firstCommitDate;

		return bugFixingCommits;
	}


	/**
	 * Extracts the git project name from the URI.
	 *
	 * @param uri The uri (e.g., https://github.com/karma-runner/karma.git)
	 * @return The project name.
	 */
	protected static String getGitProjectName(String uri) throws GitProjectAnalysisException {
		/* Get the name of the project. */
		Pattern namePattern = Pattern.compile("([^/]+)\\.git");
		Matcher matcher = namePattern.matcher(uri);

		if (!matcher.find()) {
			throw new GitProjectAnalysisException("Could not find the .git name in the URI.");
		}

		return matcher.group(1);
	}

	/**
	 * Creates the directory for the repository given the URI and the base
	 * directory to store all repositories.
	 *
	 * @param uri The uri (e.g., https://github.com/karma-runner/karma.git)
	 * @param directory The directory for the repositories.
	 * @return The folder to clone the project into.
	 * @throws GitProjectAnalysisException
	 */
	protected static File getGitDirectory(String uri, String directory) throws GitProjectAnalysisException {
		return new File(directory, getGitProjectName(uri));
	}


	/*
	 * Static factory methods
	 */

	/**
	 * Creates a new GitProject instance from a git project directory.
	 *
	 * @param directory The base directory for the project.
	 * @return An instance of GitProjectAnalysis.
	 * @throws GitProjectAnalysisException
	 */
	public static GitProject fromDirectory(String directory, String name) throws GitProjectAnalysisException {
		Git git;
		Repository repository;

		try {
			repository = new RepositoryBuilder().findGitDir(new File(directory)).build();
			git = Git.wrap(repository);
		} catch (IOException e) {
			throw new GitProjectAnalysisException("The git project was not found in the directory " + directory + ".");
		}

		return new GitProject(git, repository, repository.getConfig().getString("remote", "origin", "url"));
	}

	/**
	 * Creates a new GitProject instance from a URI.
	 *
	 * @param uri The remote .git address.
	 * @param directory The directory that stores the cloned repositories.
	 * @return An instance of GitProjectAnalysis.
	 * @throws GitAPIException
	 * @throws TransportException
	 * @throws InvalidRemoteException
	 */
	public static GitProject fromURI(String uri, String directory)
			throws GitProjectAnalysisException, InvalidRemoteException, TransportException, GitAPIException {
		Git git;
		Repository repository;
		File gitDirectory = GitProjectAnalysis.getGitDirectory(uri, directory);

		/* If the directory exists, all we need to do is pull changes. */
		if (gitDirectory.exists()) {
			try {
				repository = new RepositoryBuilder().findGitDir(gitDirectory).build();
				git = Git.wrap(repository);
			} catch (IOException e) {
				throw new GitProjectAnalysisException(
						"The git project was not found in the directory " + directory + ".");
			}

			/*
			 * Check that the remote repository is the same as the one we were
			 * given.
			 */
			StoredConfig config = repository.getConfig();
			if (!config.getString("remote", "origin", "url").equals(uri)) {
				throw new GitProjectAnalysisException(
						"The directory " + gitDirectory + " is being used by a different remote repository.");
			}

			/* Pull changes. */
			PullCommand pullCommand = git.pull();
			PullResult pullResult = pullCommand.call();

			if (!pullResult.isSuccessful()) {
				throw new GitProjectAnalysisException("Pull was not succesfull for " + gitDirectory);
			}
		}
		/* The directory does not exist, so clone the repository. */
		else {
			CloneCommand cloneCommand = Git.cloneRepository().setURI(uri).setDirectory(gitDirectory);
			git = cloneCommand.call();
			repository = git.getRepository();
		}

		GitProject gitProject = new GitProject(git, repository, uri);

		return gitProject;
	}
}
