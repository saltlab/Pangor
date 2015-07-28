package ca.ubc.ece.salt.sdjsb.git;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;

import ca.ubc.ece.salt.sdjsb.batch.GitProjectAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.GitProjectAnalysisException;

public class GitProject {
	/** The Git instance. **/
	protected Git git;

	/** The repository instance. **/
	protected Repository repository;

	/** The repository name. **/
	protected String projectID;

	/** The number of bug fixing commits analyzed. **/
	protected int bugFixingCommits;

	/** The total number of commits inspected. **/
	protected int totalCommits;

	protected GitProject(Git git, Repository repository, String name) {
		this.git = git;
		this.repository = repository;
		this.projectID = name;
	}

	protected GitProject(GitProject project) {
		this(project.git, project.repository, project.projectID);
	}

	/**
	 * Extracts revision identifier pairs from bug fixing commits. The pair
	 * includes the bug fixing commit and the previous (buggy) commit.
	 *
	 * @param git The project git instance.
	 * @param repository The project git repository.
	 * @param buggyRevision The hash that identifies the buggy revision.
	 * @param bugFixingRevision The hash that identifies the fixed revision.
	 * @throws IOException
	 * @throws GitAPIException
	 */
	protected List<Pair<String, String>> getBugFixingCommitPairs() throws IOException, GitAPIException {

		List<Pair<String, String>> bugFixingCommits = new LinkedList<Pair<String, String>>();
		Iterable<RevCommit> commits = git.log().call();
		System.out.println(repository.getBranch());

		String bugFixingCommit = null;
		int bfcCnt = 0, cCnt = 0;

		/* Starts with the most recent commit and goes back in time. */
		for (RevCommit commit : commits) {

			if (bugFixingCommit != null) {
				bugFixingCommits.add(Pair.of(commit.name(), bugFixingCommit));
				bugFixingCommit = null;
				bfcCnt++;
			}

			String message = commit.getFullMessage();
			Pattern p = Pattern.compile("fix|repair", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(message);
			cCnt++;

			/*
			 * If the commit message contains one of our fix keywords, store it.
			 */
			if (m.find()) {
				bugFixingCommit = commit.name();
			}

		}

		/* Keep track of the number of commits for metrics reporting. */
		this.bugFixingCommits = bfcCnt;
		this.totalCommits = cCnt;

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

		return new GitProject(git, repository,
				getGitProjectName(repository.getConfig().getString("remote", "origin", "url")));
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

		GitProject gitProject = new GitProject(git, repository, getGitProjectName(uri));

		return gitProject;
	}
}
