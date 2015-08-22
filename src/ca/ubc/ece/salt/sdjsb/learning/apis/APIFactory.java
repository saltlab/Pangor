package ca.ubc.ece.salt.sdjsb.learning.apis;

import java.util.Arrays;
import java.util.List;

/**
 * Builds the APIs. Right now APIs are hard coded into this class. In the
 * future we may want to automatically extract APIs from source code or
 * documentation.
 */
public class APIFactory {

	/**
	 * Builds the abstract representation of the JavaScript keywords and API.
	 * Also builds the abstract API representations of all the Node.js
	 * packages we want to include.
	 *
	 * Note: The APIs are currently hard coded. This is tedious, so if we want
	 * to include all the Node.js APIs, we should automate the building of
	 * abstract APIs.
	 *
	 * @return The root of the abstract API tree.
	 */
	public static TopLevelAPI buildTopLevelAPI() {

		List<String> keywords = Arrays.asList( "abstract",
				"arguments", "boolean", "break", "byte", "case", "catch",
				"char", "class", "const", "continue", "debugger", "default",
				"delete", "do", "double", "else", "enum", "eval", "export",
				"extends", "false", "final", "finally", "float", "for",
				"function", "goto", "if", "implements", "import", "in",
				"instanceof", "int", "interface", "let", "long", "native",
				"new", "null", "package", "private", "protected", "public",
				"return", "short", "static", "super", "switch", "synchronized",
				"this", "undefined", "throw", "throws", "transient", "true",
				"try", "typeof", "var", "void", "volatile", "while", "with",
				"yield", "callback", "error", "zero", "blank", "undefined",
				"falsey");

		List<PackageAPI> packages = buildTopLevelPackages();

		/* Methods of the Global class. */
		List<String> methods = Arrays.asList("eval", "hasOwnProperty", "isFinite",
				"isNaN", "isPrototypeOf", "toString", "valueOf", "decodeURI",
				"decodeURIComponent", "encodeURI", "encodeURIComponent", "Number",
				"parseFloat", "parseInt", "String", "unescape", "callback");

		/* Fields of the Global class. */
		List<String> fields = Arrays.asList("length", "name", "prototype",
				"constructor", "Infinity", "NaN");

		List<String> constants = Arrays.asList();

		List<String> events = Arrays.asList();

		List<String> exceptions = Arrays.asList("exception");

		List<ClassAPI> classes = buildTopLevelClasses();

		return new TopLevelAPI(keywords, packages, methods, fields, constants, events, exceptions, classes);

	}

	public static List<PackageAPI> buildTopLevelPackages() {

		PackageAPI fileSystem = buildFileSystemPackage();
		PackageAPI path = buildPathPackage();

		return Arrays.asList(/* fileSystem, path */);

	}

	public static List<ClassAPI> buildTopLevelClasses() {

//		ClassAPI template = new ClassAPI("template", /* Class Name */
//				Arrays.asList(), /* Methods */
//				Arrays.asList(), /* Fields */
//				Arrays.asList(), /* Constants */
//				Arrays.asList(), /* Events */
//				Arrays.asList()); /* Classes */

		ClassAPI json = new ClassAPI("JSON", /* Class Name */
				Arrays.asList("parse", "stringify"), /* Methods */
				Arrays.asList(), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList()); /* Classes */

		ClassAPI function = new ClassAPI("Function", /* Class Name */
				Arrays.asList("apply", "bind", "call"), /* Methods */
				Arrays.asList("length"), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList() /* Classes */);

		ClassAPI array = new ClassAPI("Array", /* Class Name */
				Arrays.asList("concat", "indexOf", "join", "indexOf", "lastIndexOf",
						"pop", "push", "reverse", "shift", "splice", "sort",
						"unshift", "forEach", "every", "some", "filter",
						"map", "reduce", "reduceRight"), /* Methods */
				Arrays.asList(), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList()); /* Classes */

		ClassAPI date = new ClassAPI("Date", /* Class Name */
				Arrays.asList("getDate", "getDay", "getFullYear", "getHours",
						"getMilliseconds", "getMinutes", "getMonth", "getSeconds",
						"getTime", "getTimezoneOffset", "getUTCDate", "getUTCDay",
						"getUTCFullyear", "getUTCHours", "getUTCMilliseconds",
						"getUTCMinutes", "getUTCMonth", "getUTCSeconds", "getYear",
						"parse", "setDate", "setFullYear", "setHours", "setMilliseconds",
						"setMinutes", "setMonth", "setSeconds", "setTime", "setUTCDate",
						"setUTCFullYear", "setUTCHours", "setUTCMilliseconds",
						"setUTCMinutes", "setUTCMonth", "setUTCSeconds", "setYear",
						"toDateString", "toGMTString", "toISOString", "toJSON",
						"toLocaleDateString", "toLocaleTimeString", "toLocaleString",
						"toTimeString", "toUTCString", "UTC"), /* Methods */
				Arrays.asList(), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList()); /* Classes */

		ClassAPI math = new ClassAPI("Math", /* Class Name */
				Arrays.asList("abs", "acos", "asin", "atan", "atan2", "ceil",
						"cos", "exp", "floor", "log", "max", "min", "pow",
						"random", "round", "sin", "sqrt", "tan"), /* Methods */
				Arrays.asList(), /* Fields */
				Arrays.asList("E", "LN2", "LN10", "LOG2E", "LOG10E", "PI",
						"SQRT1_2", "SQRT2"), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList()); /* Classes */

		/* Note: for Object, we simplify all Object.prototype.[field|method] to Object.[field|method]. */
		ClassAPI object = new ClassAPI("Object", /* Class Name */
				Arrays.asList("create", "defineProperty", "defineProperties", "freeze",
						"getOwnPropertyDescriptor", "getOwnPropertyNames", "getPrototypeOf", "is",
						"isExtensible", "isFrozen", "isSealed", "keys", "preventExtensions",
						"seal", "hasOwnProperty", "isPrototypeOf", "propertyIsEnumerable",
						"toLocalString", "toString"), /* Methods */
				Arrays.asList("length", "prototype", "constructor", "_noSuchMethod_"), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList()); /* Classes */

		/* TODO: There are also other default error constructors that we may want
		 * 		 to track: EvalError, RangeError, ReferenceError, SyntaxError,
		 * 		 TypeError and URIError.
		 */
		ClassAPI error = new ClassAPI("Error", /* Class Name */
				Arrays.asList(), /* Methods */
				Arrays.asList("message", "name"), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList()); /* Classes */

		ClassAPI number = new ClassAPI("Number", /* Class Name */
				Arrays.asList("isNaN", "isFinite", "isInteger", "isSafeInteger",
						"parseFloat", "parseInt", "toExponential", "toFixed",
						"toPrecision"), /* Methods */
				Arrays.asList("NaN"), /* Fields */
				Arrays.asList("MAX_VALUE", "MIN_VALUE", "NEGATIVE_INFINITY",
						"POSITIVE_INFINITY"), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList()); /* Classes */

		ClassAPI string = new ClassAPI("String", /* Class Name */
				Arrays.asList("fromCharCode", "charAt", "charCodeAt", "concat",
						"localeCompare", "match", "replace", "search", "slice",
						"split", "substr", "substring", "toLocaleLowerCase",
						"toLocaleUpperCase", "toLowerCase", "toUpperCase",
						"trim"), /* Methods */
				Arrays.asList("length"), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList()); /* Classes */

		ClassAPI regexp = new ClassAPI("RegExp", /* Class Name */
				Arrays.asList("compile", "exec", "test"), /* Methods */
				Arrays.asList("lastIndex", "global", "ignoreCase", "multiline",
						"source"), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList()); /* Classes */

		/* TODO: There are a lot of other classes that we don't have here. I.e.,
		 * 		 Intl, ArrayBuffer, DataView, JSON, [U]?Int[#]Array, etc. It would
		 * 		 be good to load all these, but we should automate importing the
		 * 		APIs first.
		 */

		return Arrays.asList(json, function, error, array, date, math, number, string, regexp, object);

	}

//	public static PackageAPI buildTemplatePackage() {
//
//		PackageAPI template = new PackageAPI("template", /* Package Name */
//				Arrays.asList(), /* Methods */
//				Arrays.asList(), /* Fields */
//				Arrays.asList(), /* Constants */
//				Arrays.asList(), /* Events */
//				Arrays.asList()); /* Classes */
//
//	}

	public static PackageAPI buildPathPackage() {

		PackageAPI path = new PackageAPI("path", /* Package Name */
				Arrays.asList("normalize", "join", "resolve", "isAbsolute",
						"relative", "dirname", "basename", "extname", "parse",
						"format"), /* Methods */
				Arrays.asList("sep", "delimiter", "posix", "win32"), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList()); /* Classes */

		return path;

	}

	public static  PackageAPI buildFileSystemPackage() {

		ClassAPI stats = new ClassAPI("Stats", /* Class Name */
				Arrays.asList("isFile", "isDirectory", "isBlockDevice",
						"isCharacterDevice", "isSymbolicLink", "isFIFO",
						"isSocket"), /* Methods */
				Arrays.asList("atime", "mtime", "ctime", "birthtime"), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList()); /* Classes */

		ClassAPI writeStream = new ClassAPI("WriteStream", /* Class Name */
				Arrays.asList(), /* Methods */
				Arrays.asList("bytesWritten"), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList("open"), /* Events */
				Arrays.asList()); /* Classes */

		ClassAPI readStream = new ClassAPI("ReadStream", /* Class Name */
				Arrays.asList(), /* Methods */
				Arrays.asList(), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList("open"), /* Events */
				Arrays.asList()); /* Classes */

		ClassAPI fsWatcher = new ClassAPI("FSWatcher",
				Arrays.asList("close"), /* Methods */
				Arrays.asList(), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList("change", "error"), /* Events */
				Arrays.asList()); /* Classes */

		return new PackageAPI("fs", /* Package Name */
				Arrays.asList("rename", "renameSync", "ftruncate", "ftruncateSync",
						"truncate", "truncateSync", "chown", "chownSync", "lchown",
						"lchownSync", "chmod", "chmodSync", "fchmod", "fchmodSync",
						"lchmod", "lchmodSync", "stat", "lstat", "fstat", "statSync",
						"lstatSync", "fstatSync", "link", "linkSync", "symlink",
						"symlinkSync", "readlink", "readlinkSync", "realpath",
						"realpathSync", "unlink", "unlinkSync", "rmdir", "rmdirSync",
						"mkdir", "mkdirSync", "readdir", "readdirSync", "close",
						"closeSync", "open", "openSync", "utimes", "utimesSync",
						"futimes", "futimesSync", "fsync", "fsyncSync", "write",
						"writeSync", "read", "readSync", "readFile", "readFileSync",
						"writeFile", "writeFileSync", "appendFile", "appendFileSync",
						"watchFile", "unwatchFile", "watch", "exists", "existsSync",
						"access", "accessSync", "createReadStream", "createWriteStream"), /* Methods */
				Arrays.asList(), /* Fields */
				Arrays.asList(), /* Constants */
				Arrays.asList(), /* Events */
				Arrays.asList(stats, writeStream, readStream, fsWatcher)); /* Classes */

	}

}