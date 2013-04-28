package apiworld;

import java.net.URLEncoder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;

public final class UtilityFunctions {
	private UtilityFunctions(){
		// Hide Utility Class Constructor - Utility classes should not have a public or default constructor.
	}
	public static final String PARAM_START = "?";
	public static final String COMMAND_URL_SEPARATOR = "/";
	public static final String URL_SEPARATOR = "/";
	public static final String PARAM_SEPARATOR = "&";

	public static String dropTrailingSeparator(String urlParameterTokens,
			String paramSeparator) {
		int lastCharIndex = urlParameterTokens.length()
				- paramSeparator.length();
		if (lastCharIndex > 0) {
			String trailingString = urlParameterTokens.substring(lastCharIndex,
					lastCharIndex + paramSeparator.length());
			if (trailingString.equals(paramSeparator)) {
				return urlParameterTokens.substring(0, lastCharIndex);
			}
		}
		return urlParameterTokens;
	}

	public static boolean doesHaveSeparator(String urlString,
			String commandUrlSeparator) {
		int lastCharIndex = urlString.length() - commandUrlSeparator.length();
		if (lastCharIndex > 0) {
			String trailingString = urlString.substring(lastCharIndex,
					lastCharIndex + commandUrlSeparator.length());
			return trailingString.equals(commandUrlSeparator);
		}
		return false;
	}

	public static boolean doesNotHaveTrailingSeparator(String urlString,
			String commandUrlSeparator) {
		return !doesHaveSeparator(urlString, commandUrlSeparator);
	}

	@SuppressWarnings("deprecation")
	public static String encodeToken(String value) {
		if (value == null) {
			return null;
		}
		return URLEncoder.encode(value);
	}

	public static boolean isNotNull(Object value) {
		return value != null;
	}
	
	public static Document stringToXML(String string) {
		String localString = string.substring(1, string.length() - 1);
		localString = localString.replaceAll(">,", ">");
		return Jsoup.parse(localString);
	}
	
	public static Boolean validateJSON(String resultAsString) {
		Gson gson = new Gson();
		gson.toJson(resultAsString);
		return true;
	}
	
	public static Boolean validateXML(String result) {
		stringToXML(result);
		return true;
	}
}