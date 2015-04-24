package ca.ubc.ece.salt.sdjsb.batch;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;
import ca.ubc.ece.salt.sdjsb.SDJSB;
import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeAnalysis;
import fr.labri.gumtree.client.DiffOptions;

public class GitProjectAnalysis {
	
	private Git git;
	private Repository repository;
	
	private ProjectAnalysisResult analysisResult;
	
	GitProjectAnalysis(Git git, Repository repository, String name) {
		this.git = git;
		this.repository = repository;
		
		this.analysisResult = new ProjectAnalysisResult(name);
	}
	
	/**
	 * @return The results of the analysis.
	 */
	public ProjectAnalysisResult getAnalysisResult() {
		return this.analysisResult;
	}
	
	/**
	 * Analyze the repository (extract repairs).
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public void analyze() throws GitAPIException, IOException, Exception {
		
		/* Get the list of bug fixing commits from version history. */
		List<Pair<String, String>> bugFixingCommits = this.getBugFixingCommitPairs();
		
		/* Analyze the changes made in each bug fixing commit. */
		for(Pair<String, String> bugFixingCommit : bugFixingCommits) {

			this.analyzeDiff(bugFixingCommit.getLeft(), bugFixingCommit.getRight());
			
		}

	}

	/**
	 * Extract the source files from Git and run SDJSB on them.
	 * @param git The project git instance.
	 * @param repository The project git repository.
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
                String oldFile = this.fetchBlob(buggyRevision, diff.getOldPath());
                String newFile = this.fetchBlob(bugFixingRevision, diff.getOldPath());
                
                try {
                	List<Alert> alertsFromAnalysis = GitProjectAnalysis.runSDJSB(oldFile, newFile);
                	for(Alert alertFromAnalysis : alertsFromAnalysis) {
                		this.analysisResult.insert(new BatchAlert(alertFromAnalysis, bugFixingRevision, buggyRevision, diff.getOldPath(), diff.getNewPath()));
                	}
                }
                catch(Exception ignore) { 
                	System.err.println("Ignoring exception in ProjectAnalysis.runSDJSB.\nBuggy Revision: " + buggyRevision + "\nOld File: " + diff.getOldPath() + "\nBug Fixing Revision: " + bugFixingRevision + "\nNew File:" + diff.getNewPath());
                	System.out.println(oldFile);
                	System.out.println(newFile);
                	throw ignore;
                }
                catch(Error e) {
                	System.err.println("Ignoring exception in ProjectAnalysis.runSDJSB.\nBuggy Revision: " + buggyRevision + "\nOld File: " + diff.getOldPath() + "\nBug Fixing Revision: " + bugFixingRevision + "\nNew File:" + diff.getNewPath());
                	System.out.println(oldFile);
                	System.out.println(newFile);
                	throw e;
                }
			}
		}
		
	}

	/**
	 * Extracts revision identifier pairs from bug fixing commits. The pair
	 * includes the bug fixing commit and the previous (buggy) commit.
	 * @param git The project git instance.
	 * @param repository The project git repository.
	 * @param buggyRevision The hash that identifies the buggy revision.
	 * @param bugFixingRevision The hash that identifies the fixed revision.
	 * @throws IOException
	 * @throws GitAPIException
	 */
	private List<Pair<String, String>> getBugFixingCommitPairs() throws IOException, GitAPIException {

		List<Pair<String, String>> bugFixingCommits = new LinkedList<Pair<String, String>>();
		ObjectId head = this.repository.resolve(Constants.HEAD);
		Iterable<RevCommit> commits = this.git.log().add(head).all().call();
		String bugFixingCommit = null;
		int bfcCnt = 0, cCnt = 0;

		/* Starts with the most recent commit and goes back in time. */
		for(RevCommit commit : commits) {
			
			if(bugFixingCommit != null) {
				bugFixingCommits.add(Pair.of(commit.name(), bugFixingCommit));
				bugFixingCommit = null;
				bfcCnt++;
			}

			String message = commit.getFullMessage();
			Pattern p = Pattern.compile("fix|repair", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(message);
			cCnt++;
			
			/* If the commit message contains one of our fix keywords, store it. */
			if(m.find()) {
				bugFixingCommit = commit.name();
			}

		}
		
		/* Keep track of the number of commits for metrics reporting. */
		this.analysisResult.setBugFixingCommits(bfcCnt);
		this.analysisResult.setTotalCommits(cCnt);
		
		return bugFixingCommits;
	}

	/**
	 * Runs SDJSB and prints out the alerts it generates.
	 * @param oldFile The buggy source code.
	 * @param newFile The repaired source code.
	 */
	private static List<Alert> runSDJSB(String oldFile, String newFile) throws Exception {

        /* Analyze the files using SDJSB. */
        DiffOptions options = new DiffOptions();
        CmdLineParser parser = new CmdLineParser(options);

        try {
            parser.parseArgument(new String[] { oldFile, newFile });
        } catch (CmdLineException e) {
            System.err.println("Usage:\nSDJSB /path/to/src /path/to/dst");
            e.printStackTrace();
            return null;
        }
        
        /* Control flow difference the files. */
        ControlFlowDifferencing cfd = null;
        try {
            cfd = new ControlFlowDifferencing(new String[] {"", ""}, oldFile, newFile);
        }
        catch(Exception e) {
        	throw e;
//        	System.err.println("Error while differencing the files.");
//        	System.err.println(e.getMessage());
//        	return null;
        }
        
        /* Run the analysis. */ 
        List<Alert> alerts;
        try {
            alerts = cfd.analyze(new SpecialTypeAnalysis());
        }
        catch(Exception e) {
        	throw e;
//        	System.err.println("Error while analyzing the files.");
//        	System.err.println(e.getMessage());
//        	return null;
        }
        
        return alerts;
        //return SDJSB.analyze(options, oldFile, newFile);
	}
	
	/**
	 * Creates a new GitProjectAnalysis instance from a git project directory.
	 * @param directory The base directory for the project.
	 * @return An instance of GitProjectAnalysis.
	 * @throws GitProjectAnalysisException
	 */
	public static GitProjectAnalysis fromDirectory(String directory, String name) throws GitProjectAnalysisException {
        Git git;
        Repository repository;

		try {
			repository = new RepositoryBuilder().findGitDir(new File(directory)).build();
            git = Git.wrap(repository);
		} catch (IOException e) {
			throw new GitProjectAnalysisException("The git project was not found in the directory " + directory + ".");
		}

        return new GitProjectAnalysis(git, repository, getGitProjectName(repository.getConfig().getString("remote", "origin", "url")));
	}
	
	/**
	 * Creates a new GitProjectAnalysis instance from a URI.
	 * @param uri The remote .git address.
	 * @param directory The directory that stores the cloned repositories.
	 * @return An instance of GitProjectAnalysis.
	 * @throws GitAPIException 
	 * @throws TransportException 
	 * @throws InvalidRemoteException 
	 */
	public static GitProjectAnalysis fromURI(String uri, String directory) throws GitProjectAnalysisException, InvalidRemoteException, TransportException, GitAPIException {
		
		Git git;
		Repository repository;
		File gitDirectory = GitProjectAnalysis.getGitDirectory(uri, directory);

        /* If the directory exists, all we need to do is pull changes. */
        if(gitDirectory.exists()) {

            try {
                repository = new RepositoryBuilder().findGitDir(gitDirectory).build();
                git = Git.wrap(repository);
            } catch (IOException e) {
                throw new GitProjectAnalysisException("The git project was not found in the directory " + directory + ".");
            }
            
            /* Check that the remote repository is the same as the one we were given. */
            StoredConfig config = repository.getConfig();
            if(!config.getString("remote", "origin", "url").equals(uri)) {
                throw new GitProjectAnalysisException("The directory " + gitDirectory + " is being used by a different remote repository.");
            }

            /* Pull changes. */
            PullCommand pullCommand = git.pull();
            PullResult pullResult = pullCommand.call();

            if(!pullResult.isSuccessful()) {
                throw new GitProjectAnalysisException("Pull was not succesfull for " + gitDirectory);
            }
        }
        /* The directory does not exist, so clone the repository. */
        else {
            CloneCommand cloneCommand = Git.cloneRepository().setURI(uri).setDirectory(gitDirectory);
            git = cloneCommand.call();
            repository = git.getRepository();
        }
        
        return new GitProjectAnalysis(git, repository, getGitProjectName(uri));
	}
	
	/**
	 * Creates the directory for the repository given the URI and the base
	 * directory to store all repositories.
	 * @param uri The uri (e.g., https://github.com/karma-runner/karma.git)
	 * @param directory The directory for the repositories.
	 * @return The folder to clone the project into.
	 * @throws GitProjectAnalysisException
	 */
	private static File getGitDirectory(String uri, String directory) throws GitProjectAnalysisException {
        return new File(directory, getGitProjectName(uri));
	}
	
	/**
	 * Extracts the git project name from the URI.
	 * @param uri The uri (e.g., https://github.com/karma-runner/karma.git)
	 * @return The project name.
	 */
	private static String getGitProjectName(String uri) throws GitProjectAnalysisException {
        /* Get the name of the project. */
        Pattern namePattern = Pattern.compile("([^/]+)\\.git");
        Matcher matcher = namePattern.matcher(uri);

        if(!matcher.find()) {
        	throw new GitProjectAnalysisException("Could not find the .git name in the URI.");
        }
        
        return matcher.group(1);
	}

	/**
	 * Fetches the string contents of a file from a specific revision.
	 * 	from http://stackoverflow.com/questions/1685228/how-to-cat-a-file-in-jgit
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
	
}
