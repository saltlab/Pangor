package ca.ubc.ece.salt.pangor.batch;

public class GitProjectAnalysisException extends Exception { 

	private static final long serialVersionUID = 1L;

	private String message;

    public GitProjectAnalysisException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}