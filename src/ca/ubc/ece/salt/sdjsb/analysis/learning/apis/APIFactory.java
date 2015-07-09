package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

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
		
		List<String> keywords = Arrays.asList("arguments", "boolean", "byte", 
			"char", "class", "debugger", "default", "delete", "double", "enum", 
			"export", "false", "float", "instanceof", "int", "long", "native", 
			"null", "short", "super", "this", "transient", "true", "typeof", 
			"volatile");

		List<PackageAPI> packages = buildTopLevelPackages();
		
		List<String> methods = Arrays.asList("eval", "hasOwnProperty", "isFinite", "isNaN", 
				"isPrototypeOf", "toString", "valueOf");
		
		List<String> fields = Arrays.asList("length", "name", "prototype", "constructor");
		
		List<String> constants = Arrays.asList();
		
		List<String> events = Arrays.asList();
		
		List<ClassAPI> classes = buildTopLevelClasses();
		
		return new TopLevelAPI(keywords, packages, methods, fields, constants, events, classes);

	}
	
	public static List<PackageAPI> buildTopLevelPackages() {
		return null;
	}

	public static List<ClassAPI> buildTopLevelClasses() {

//		ClassAPI template = new ClassAPI("template", /* Class Name */
//				Arrays.asList(), /* Methods */
//				Arrays.asList(), /* Fields */
//				Arrays.asList(), /* Constants */
//				Arrays.asList(), /* Events */
//				Arrays.asList()); /* Classes */
		
		ClassAPI array = new ClassAPI("Array", /* Class Name */
				Arrays.asList("concat", "indexOf", "join", "lastIndexOf",
						"pop", "push", "reverse", "shift", "slice", "sort", 
						"splice", "toString", "unshift", "valueOf"), /* Methods */
				Arrays.asList("length"), /* Fields */
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
		
		// TODO: "Array", "Date", "Math", "NaN", "Number", "Object", "String";
		
		return Arrays.asList(array, date);

	}
	
}