package ca.ubc.ece.salt.sdjsb.analysis.learning.ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

public class FeatureVector {

	/**
	 * JavaScript keywords to track including:
	 *  - Reserved words
	 *  - Objects
	 *  - Methods
	 *  - Properties
	 */
	public static final List<String> JAVASCRIPT_KEYWORDS  = Arrays.asList("arguments", "boolean", // Reserved words
			"byte", "char", "class", "debugger", "default", "delete", "double",
			"enum", "export", "false", "float", "instanceof", "int",
			"long", "native", "null", "short", "super", "this", "transient",
			"true", "typeof", "volatile",
			"Array", "Date", "Math", "NaN", "Number", "Object", "String", // Objects
			"undefined",
			"eval", "hasOwnProperty", "isFinite", "isNaN", "isPrototypeOf", // Methods
			"toString", "valueOf",
			"length", "name", "prototype", // Properties
			"zero", "blank", "empty_object", "empty_array", "error", // Custom
			"callback", "~getNextKey", "~hasNextKey", "~error~", "~falsey~");
	
	/**
	 * Keywords from the fs package.
	 */
	public static final List<String> FS_KEYWORDS = Arrays.asList("fs", "rename",
			"renameSync", "ftruncate", "ftruncateSync", "truncate", 
			"truncateSync", "chown", "chownSync", "lchown", "lchownSync",
			"chmod", "chmodSync", "fchmod", "fchmodSync", "lchmod", "lchmodSync",
			"stat", "lstat", "fstat", "statSync", "lstatSync", "fstatSync",
			"link", "linkSync", "symlink", "symlinkSync", "readlink", 
			"readlinkSync", "realpath", "realpathSync", "unlink", "unlinkSync",
			"rmdir", "rmdirSync", "mkdir", "mkdirSync", "readdir", "readdirSync",
			"close", "closeSync", "open", "openSync", "utimes", "utimesSync",
			"futimes", "futimesSync", "fsync", "fsyncSync", "write", "writeSync",
			"read", "readSync", "readFile", "readFileSync", "writeFile", 
			"writeFileSync", "appendFile", "appendFileSync", "watchFile",
			"unwatchFile", "watch", "exists", "existsSync", "access", "accessSync",
			"createReadStream", "createWriteStream", "ReadStream", "WriteStream",
			"FSWatcher");
	
	
	/**
	 * Keywords from the console package.
	 */
	public static final List<String> CONSOLE_KEYWORDS = Arrays.asList("console",
			"log", "info", "error", "warn", "dir", "time", "timeEnd", "trace",
			"assert");
	
	/**
	 * Keywords from the assert package.
	 */
	public static final List<String> ASSERT_KEYWORDS = Arrays.asList("assert",
			"fail", "equal", "notEqual", "deepEqual", "notDeepEqual", 
			"strictEqual", "notStrictEqual", "throws", "doesNotThrow",
			"ifError");

	/**
	 * Keywords from the Buffer class.
	 */
	public static final List<String> BUFFER_KEYWORDS = Arrays.asList("Buffer", "isEncoding",
			"isBuffer", "byteLength", "concat", "compare", "length", "write", 
			"writeUIntLE", "writeUIntBE", "writeIntLE", "writeIntBE", "readUIntLE",
			"readUIntBE", "readIntLE", "readIntBE", "toString", "toJSON", "equals",
			"compare", "copy", "slice", "readUInt8", "readUInt16LE", "readUInt16BE",
			"readUInt32LE", "readUInt32BE", "readInt8", "readInt16LE", "readInt16BE",
			"readInt32LE", "readInt32BE", "readFloatLE", "readFloatBE", 
			"readDoubleLE", "readDoubleBE", "writeUInt8", "writeUInt16LE",
			"writeUInt16BE", "writeUInt32LE", "writeUInt32BE", "writeInt8",
			"writeInt16LE", "writeInt16BE", "writeInt32LE", "writeInt32BE", 
			"writeFloatLE", "writeFloatBE", "writeDoubleLE", "writeDoubleBE",
			"fill", "INSPECT_MAX_BYTES", "SlowBuffer");
	
	/**
	 * Keywords from the ChildProcess class.
	 */
	public static final List<String> CHILD_PROCESS_KEYWORDS = Arrays.asList("ChildProcess",
			"error", "exit", "close", "disconnect", "message", "stdin", 
			"stdout", "stderr", "stdio", "pid", "connected", "kill", "send",
			"disconnect", "spawn", "stdio", "detached", "customFds", "exec", 
			"execFile", "fork", "spawnSync", "execFileSync", "execSync");
	
	/**
	 * Keywords form the cluster package.
	 */
	public static final List<String> CLUSTER_KEYWORDS = Arrays.asList("cluster",
			"schedulingPolicy", "settings", "isMaster", "isWorker", "fork",
			"online", "listening", "disconnect", "exit", "setup", "setupMaster",
			"fork", "disconnect", "worker", "workers", "Worker", "id", "process",
			"suicide", "send", "kill", "disconnect", "isDead", "isConnected", 
			"message", "online", "listening", "disconnect", "exit", "error");
	
	/**
	 * Keywords from the crypto package.
	 */
	public static final List<String> CRYPTO_KEYWORDS = Arrays.asList("crypto",
			"setEngine", "getCiphers", "getHashes", "createCredentials", 
			"createHash", "update", "digest", "createHmac", "update", "digest",
			"createCipher", "createCipheriv", "Cipher", "update", "final",
			"setAutoPadding", "getAuthTag", "setAAD", "createSign", "Sign",
			"update", "sign", "createVerify", "Verify", "update", "verify",
			"createDeffieHellman", "DeffieHellman", "verifyError", "generateKeys",
			"computeSecret", "getPrime", "getGenerator", "getPublicKey",
			"getPrivateKey", "setPublicKey", "setPrivateKey", "getDiffieHellman",
			"createECDH", "ECDH", "generateKeys", "computeSecret", 
			"getPublicKey", "getPrivateKey", "setPublicKey", "setPrivateKey",
			"pbkdf2", "pbkdf2Sync", "randomBytes", "pseudoRandomBytes",
			"Certificate", "verifySpkac", "exportChallenge", "exportPublicKey",
			"publicEncrypt", "privateDecrypt", "DEFAULT_ENCODING");
	
	/**
	 * Keywords from the DNS package.
	 */
	public static final List<String> DNS_KEYWORDS = Arrays.asList("dns",
			"lookup", "lookupService", "resolve", "resolve4", "resolve6",
			"resolveMx", "resolveTxt", "resolveSrv", "resolveSoa",
			"resolveNs", "resolveCname", "reverse", "getServers",
			"setServers", "NODATA", "FORMERR", "SERVFAIL", "NOTFOUND",
			"NOTIMP", "REFUSED", "BADQUERY", "BADNAME", "BADFAMILY",
			"BADRESP", "CONNREFUSED", "TIMEOUT", "EOF", "FILE", "NOMEM",
			"DESTRUCTION", "BADSTR", "BADFLAGS", "NONAME", "BADHINTS",
			"LOADIPHLPAPI", "ADDRGETNETWORKPARAMS", "CANCELLED");
	
	/**
	 * Keywords from the domain package.
	 */
	public static final List<String> DOMAIN_KEYWORDS = Arrays.asList("domain", 
			"run", "members", "add", "remove", "bind", "intercept", "enter", 
			"exit", "dispose");
	
	/**
	 * Keywords from the events package.
	 */
	public static final List<String> EVENTS_KEYWORDS = Arrays.asList("events",
			"EventEmitter", "addListener", "on", "once", "removeListener",
			"removeAllListeners", "setMaxListeners", "defaultMaxListeners", 
			"listeners", "emit", "listenerCount", "newListener", 
			"removeListener");
	
	/**
	 * Global keywords in Node.js
	 */
	public static final List<String> GLOBAL_KEYWORDS = Arrays.asList("global",
			"process", "console", "Buffer", "require", "resolve", "cache",
			"extensions", "__filename", "__dirname", "module", "exports", 
			"setTimeout", "clearTimeout", "setInterval", "clearInterval");
	
	/**
	 * Keywords from the HTTP package.
	 */
	public static final List<String> HTTP_KEYWORDS = Arrays.asList("http",
			"METHODS", "STATUS_CODES", "createServer", "createClient", "Server",
			"request", "connection", "close", "checkContinue", "connect", 
			"upgrade", "clientError", "listen", "close", "maxHeadersCount",
			"setTimeout", "timeout", "close", "finish", "writeContinue",
			"writeHead", "setTimeout", "statusCode", "statusMessage",
			"setHeader", "headersSent", "sendDate", "getHeader", "removeHeader", 
			"write", "addTrailers", "end", "request", "get", "Agent", 
			"maxSockets", "maxFreeSockets", "sockets", "freeSockets", "requests",
			"destroy", "getName", "globalAgent", "ClientRequest", "response",
			"socket", "connect", "upgrade", "continue", "flushHeaders", "write",
			"end", "abort", "setTimeout", "setNoDelay", "setSocketKeepAlive",
			"IncommingMessage", "close", "httpsVersion", "headers", "rawHeaders",
			"trailers", "rawTrailers", "setTimeout", "method", "url", 
			"statusCode", "statusMessage", "socket");
	
	/**
	 * Keywords form the HTTPS package.
	 */
	public static final List<String> HTTPS_KEYWORDS = Arrays.asList("https",
			"Server", "setTimeout", "timeout", "createServer", "listen",
			"close", "request", "get", "Agent", "globalAgent");
	
	/**
	 * Keywords from the modules package.
	 */
	public static final List<String> MODULE_KEYWORDS = Arrays.asList("module",
			"exports", "alias", "require", "id", "filename", "loaded", "parent",
			"children");
	
	/**
	 * Keywords from the net package.
	 */
	public static final List<String> NET_KEYWORDS = Arrays.asList("net",
			"createServer", "connect", "createConnection", "Server",
			"listen", "close", "address", "unref", "ref", "maxConnections",
			"connections", "getConnections", "listening", "connection",
			"close", "error", "Socket", "connect", "bufferSize", "setEncoding",
			"write", "end", "destroy", "pause", "resume", "setTimeout", 
			"setNoDelay", "setKeepAlive", "address", "unref", "ref", 
			"remoteAddress", "remoteFamily", "remotePort", "localAddress",
			"localPort", "bytesRead", "bytesWritten", "lookup", "connect",
			"data", "end", "timeout", "drain", "error", "close", "isIP", 
			"isIPv4", "isIPv6");
	
	/**
	 * Keywords from the OS package.
	 */
	public static final List<String> OS_KEYWORDS = Arrays.asList("os", 
			"tmpdir", "endianness", "hostname", "type", "platform", "arch",
			"release", "uptime", "loadavg", "totalmem", "freemem", "cpus",
			"networkInterfaces", "EOL");
	
	/**
	 * Keywords from the path package.
	 */
	public static final List<String> PATH_KEYWORDS = Arrays.asList("path");
			
			
	
	/** A counter to produce unique IDs for each feature vector. **/
	private static int idCounter;
	
	/** The unique ID for the feature vector. **/
	public int id;
	
	/** The ID for the commit where the bug is present. **/
	public String buggyCommitID;
	
	/** The ID for the commit where the bug is repaired. **/
	public String repairedCommitID;
	
	/** The file path from which this feature vector was constructed. **/
	public String path;
	
	/** The function from which this feature vector was constructed. **/
	public String functionName;
	
	/** The buggy code **/
	public String sourceCode;
	
	/** The repaired code. **/
	public String destinationCode;
	
	/** The statement types in each fragment. **/
	public Map<String, Integer> insertedStatementMap;
	public Map<String, Integer> removedStatementMap;
	public Map<String, Integer> updatedStatementMap;
	
	/** The keyword counts in each fragment. **/
	public Map<String, Integer> insertedKeywordMap;
	public Map<String, Integer> removedKeywordMap;
	public Map<String, Integer> updatedKeywordMap;
	public Map<String, Integer> unchangedKeywordMap;

	public FeatureVector() {
		this.id = FeatureVector.getNextID();
		this.insertedStatementMap = FeatureVector.buildStatementMap();
		this.removedStatementMap = FeatureVector.buildStatementMap();
		this.updatedStatementMap = FeatureVector.buildStatementMap();
		this.insertedKeywordMap = FeatureVector.buildKeywordMap();
		this.removedKeywordMap = FeatureVector.buildKeywordMap();
		this.updatedKeywordMap = FeatureVector.buildKeywordMap();
		this.unchangedKeywordMap = FeatureVector.buildKeywordMap();
	}
	
	/**
	 * Joins a source feature vector with this (the destination) feature vector.
	 * @param source The source feature vector.
	 */
	public void join(FeatureVector source) {
		this.sourceCode = source.sourceCode;
		this.removedStatementMap = source.removedStatementMap;
		this.removedKeywordMap = source.removedKeywordMap;
	}
	
	/**
	 * If the given token is a statement, that statement's count is incremented
	 * by one.
	 * @param token The string to check against the statement list.
	 */
	@SuppressWarnings("incomplete-switch")
	public void addStatement(String token, ChangeType changeType) {

		switch(changeType) {

		case INSERTED:
			if(this.insertedStatementMap.containsKey(token)) {
				this.insertedStatementMap.put(token,  this.insertedStatementMap.get(token) + 1);
			}
			break;

		case REMOVED:
			if(this.removedStatementMap.containsKey(token)) {
				this.removedStatementMap.put(token,  this.removedStatementMap.get(token) + 1);
			}
			break;

		case UPDATED:
			if(this.updatedStatementMap.containsKey(token)) {
				this.updatedStatementMap.put(token,  this.updatedStatementMap.get(token) + 1);
			}
			break;
			
		}
	}
	
	/**
	 * If the given token is a keyword, that keyword's count is incremented by
	 * one.
	 * @param token The string to check against the keyword list.
	 */
	@SuppressWarnings("incomplete-switch")
	public void addKeyword(String token, ChangeType changeType) {

		switch(changeType) {

		case INSERTED:
			if(this.insertedKeywordMap.containsKey(token)) {
				this.insertedKeywordMap.put(token,  this.insertedKeywordMap.get(token) + 1);
			}
			break;

		case REMOVED:
			if(this.removedKeywordMap.containsKey(token)) {
				this.removedKeywordMap.put(token,  this.removedKeywordMap.get(token) + 1);
			}
			break;

		case UPDATED:
			if(this.updatedKeywordMap.containsKey(token)) {
				this.updatedKeywordMap.put(token,  this.updatedKeywordMap.get(token) + 1);
			}
			break;

		case MOVED:
		case UNCHANGED:
			if(this.unchangedKeywordMap.containsKey(token)) {
				this.unchangedKeywordMap.put(token,  this.unchangedKeywordMap.get(token) + 1);
			}

		}
	}
	
	/**
	 * @return true if there are no keyword changes.
	 */
	public boolean isEmpty() {

		if(!isEmpty(this.insertedKeywordMap)) return false;
		if(!isEmpty(this.removedKeywordMap)) return false;
		if(!isEmpty(this.updatedKeywordMap)) return false;

		return true;

	}
	
	/**
	 * Checks if an integer in the map is greater than zero.
	 * @param map the map to check
	 * @return true if all values in the map are zero.
	 */
	private static boolean isEmpty(Map<String, Integer> map) {
		for(Integer value : map.values()) {
			if(value > 0) return false;
		}
		return true;
	}
	
	/**
	 * @return A map initialized with the statements we want to track.
	 */
	private static Map<String, Integer> buildStatementMap() {

		Map<String, Integer> statementsInFragments = new HashMap<String, Integer>();

		statementsInFragments.put("EmptyStatement", 0);
		statementsInFragments.put("ExpressionStatement", 0);
		statementsInFragments.put("VariableDeclaration", 0);
		statementsInFragments.put("FunctionNode", 0);
		statementsInFragments.put("ReturnStatement", 0);
		statementsInFragments.put("LabeledStatement", 0);
		statementsInFragments.put("ThrowStatement", 0);
		statementsInFragments.put("TryStatement", 0);
		statementsInFragments.put("WithStatement", 0);
		statementsInFragments.put("BreakStatement", 0);
		statementsInFragments.put("ContinueStatement", 0);
		statementsInFragments.put("SwitchStatement", 0);
		
		return statementsInFragments;
		
	}
	
	/**
	 * @return the CSV header.
	 */
	public static String getHeader() {

		String header = "ID\tURI\tSourceFile\tDestinationFile\tBuggyCommit\tRepairedCommit\tFunction";

		/* Statement types might not be effective predictors... or at least we 
		 * may need to handle more statement types before they are effective. */
//		for(String statementType : FeatureVector.buildStatementMap().keySet()) {
//			header += "\tInserted-" + statementType;
//			header += "\tRemoved-" + statementType;
//			header += "\tUpdated-" + statementType;
//		}
		
		for(String keyword : getKeywords()) {
			header += "\tInserted-" + keyword;
			header += "\tRemoved-" + keyword;
			header += "\tUpdated-" + keyword;
			header += "\tUnchanged-" + keyword;
		}

		return header;

	}
	
	/**
	 * @param project The project identifier.
	 * @param buggyCommit The ID for the commit where the bug is present.
	 * @param repairedCommit The ID for the commit where the bug is repaired.
	 * @return the CSV row (the feature vector) as a string.
	 */
	public String getFeatureVector(String project, String sourceFile, String destinationFile, String buggyCommit, String repairedCommit) {

		String vector = id + "\t" + project + "\t" + sourceFile + "\t" + destinationFile 
				+ "\t" + buggyCommit + "\t" + repairedCommit + "\t" + this.functionName;
		
		/* Statement types might not be effective predictors... or at least we 
		 * may need to handle more statement types before they are effective. */
//		for(String statementType : this.insertedStatementMap.keySet()) {
//			vector += "\t" + this.insertedStatementMap.get(statementType);
//			vector += "\t" + this.removedStatementMap.get(statementType);
//			vector += "\t" + this.updatedStatementMap.get(statementType);
//		}
		
		for(String keyword : getKeywords()) {
			vector += "\t" + this.insertedKeywordMap.get(keyword);
			vector += "\t" + this.removedKeywordMap.get(keyword);
			vector += "\t" + this.updatedKeywordMap.get(keyword);
			vector += "\t" + this.unchangedKeywordMap.get(keyword);
		}
		
		return vector;

	}
	
	/**
	 * @return The source code for the alert.
	 */
	public String getSource() {
		return this.sourceCode;
	}
	
	/**
	 * @return The destination code for the alert.
	 */
	public String getDestination() {
		return this.destinationCode;
	}

	/**
	 * @return The next unique ID for a feature vector alert.
	 */
	private static int getNextID() {
		idCounter++;
		return idCounter;
	}

	/**
	 * @return a map initialized with the keywords we want to track.
	 */
	private static Map<String, Integer> buildKeywordMap() {

		/* Initialize the keyword map. */
		Map<String, Integer> keywordMap = new HashMap<String, Integer>();
		for(String keyword : getKeywords()) { 
			keywordMap.put(keyword, 0);
		}
		
		return keywordMap;
		
	}
	
	/**
	 * Builds the list of keywords that we want to use in our feature vectors.
	 * This list will change with different experiments, so this function is
	 * useful to separate the keyword list declarations from the list that is
	 * used at runtime.
	 * @return The list of keywords to look for.
	 */
	private static List<String> getKeywords() {
		List<String> keywords = new LinkedList<String>();
		keywords.addAll(JAVASCRIPT_KEYWORDS);
		keywords.addAll(FS_KEYWORDS);
		return keywords;
	}
	
}