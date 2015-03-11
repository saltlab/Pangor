package ca.ubc.ece.salt.sdjsb.batch;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ca.ubc.ece.salt.sdjsb.SDJSB;
import ca.ubc.ece.salt.sdjsb.checker.Alert;
import fr.labri.gumtree.client.DiffOptions;

public class ProjectAnalysis {

	/**
	 * Inspects bug fixing commits to classify repairs.
	 * @param args Usage: ProjectAnalysis /path/to/project/.git
	 * @throws IOException
	 * @throws GitAPIException
	 */
	public static void main(String[] args) throws IOException, GitAPIException {
		
		if(args.length < 1) {
			System.out.println("Usage: ProjectAnalysis /path/to/project/.git");
			return;
		}
		
		List<Alert> alerts = new LinkedList<Alert>();

		File gitDirectory = new File(args[0]);

		Git git = Git.open(gitDirectory);

		Repository repository = git.getRepository();
		
		List<Pair<String, String>> bugFixingCommits = ProjectAnalysis.getBugFixingCommits(git, repository);
		
		for(Pair<String, String> bugFixingCommit : bugFixingCommits) {

            alerts.addAll(ProjectAnalysis.analyzeDiff(git, repository, bugFixingCommit.getLeft(), bugFixingCommit.getRight()));
			
		}
		
		System.out.println("Alerts (" + alerts.size() + "):");
		for(Alert alert : alerts) {
			System.out.println("\t" + alert.getShortDescription());
		}
		
		//ProjectAnalysis.analyzeDiff(git, repository, "2eac02ccadb4d09277c1e6b8b018b2d0394ed2cb", "bf2bf8a64673e3ccd0f5d1a103e11a067c1f9ac5");
		
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
	private static List<Pair<String, String>> getBugFixingCommits(Git git, Repository repository) throws IOException, GitAPIException {

		List<Pair<String, String>> bugFixingCommits = new LinkedList<Pair<String, String>>();
		ObjectId head = repository.resolve(Constants.HEAD);
		Iterable<RevCommit> commits = git.log().add(head).all().call();
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
				//System.out.println("Bug Fixing Commit: " + commit.name());

                Date date = new Date(commit.getCommitTime() * 1000L);
                //System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
			}

		}
		
		System.out.println("There were " + bfcCnt + " bug fixing commits found out of " + cCnt + " total commits.");
		
		return bugFixingCommits;
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
	private static List<Alert> analyzeDiff(Git git, Repository repository, String buggyRevision, String bugFixingRevision) throws IOException, GitAPIException {
		
		List<Alert> alerts = new LinkedList<Alert>();

		ObjectId buggy = repository.resolve(buggyRevision + "^{tree}");
		ObjectId repaired = repository.resolve(bugFixingRevision + "^{tree}");
		
		ObjectReader reader = repository.newObjectReader();
		
		CanonicalTreeParser buggyTreeIter = new CanonicalTreeParser();
		buggyTreeIter.reset(reader, buggy);

		CanonicalTreeParser repairedTreeIter = new CanonicalTreeParser();
		repairedTreeIter.reset(reader, repaired);
		
		DiffCommand diffCommand = git.diff().setShowNameAndStatusOnly(true).setOldTree(buggyTreeIter).setNewTree(repairedTreeIter);
		
		List<DiffEntry> diffs = diffCommand.call();
		
		for(DiffEntry diff : diffs) {
			if(diff.getOldPath().matches("^.*\\.js$") && diff.getNewPath().matches("^.*\\.js$")){
                String oldFile = ProjectAnalysis.fetchBlob(repository, buggyRevision, diff.getOldPath());
                String newFile = ProjectAnalysis.fetchBlob(repository, bugFixingRevision, diff.getOldPath());
                
                try {
                	alerts.addAll(ProjectAnalysis.runSDJSB(oldFile, newFile));
                }
                catch(Exception ignore) { 
                	System.err.println("Ignoring exception in ProjectAnalysis.runSDJSB.");
                }
			}
		}
		
		return alerts;
	}
	
	/**
	 * Runs SDJSB and prints out the alerts it generates.
	 * @param oldFile The buggy source code.
	 * @param newFile The repaired source code.
	 */
	private static List<Alert> runSDJSB(String oldFile, String newFile) {
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

        return SDJSB.analyze(options, oldFile, newFile);
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
	private static String fetchBlob(Repository repo, String revSpec, String path) throws MissingObjectException, IncorrectObjectTypeException, IOException {

		// Resolve the revision specification
	    final ObjectId id = repo.resolve(revSpec);
		
        // Makes it simpler to release the allocated resources in one go
        ObjectReader reader = repo.newObjectReader();

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
