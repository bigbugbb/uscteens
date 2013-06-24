package edu.neu.android.mhealth.uscteensver1.support;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import android.content.Context;
import android.util.Log;
import edu.neu.android.wocketslib.support.DataStorage;

public class VersionChecker {
	private static final String TAG = "VersionChecker";
	
	public static boolean isNewUpdateAvailable(Context aContext)
	{
		int marketVersionCode = VersionChecker.getVersionCodeFromMarket();
		String thisVersion = DataStorage.getVersion(aContext, "0.0");
		int thisVersionCode = VersionChecker.getVersionCodeFromFancyVersionString(thisVersion);
		if ((thisVersionCode != 0) &&
			(marketVersionCode != 0) && 
			(thisVersionCode < marketVersionCode))
			return true;
		else
			return false;		
	}
		
	public static String getVersionFromMarket()
	{
		return getVersionFromMarket("edu.mit.android.cityver1");	
	}

	public static int getVersionCodeFromMarket()
	{
		String version = getVersionFromMarket();
		return getVersionCodeFromVersionString(version);
	}
	
	public static int getVersionCodeFromVersionString(String versionString)
	{
		String[] codes = versionString.split("[.]");
		if (codes.length == 2)
		{
			return Integer.parseInt(codes[1]); 
		}
		return 0; 		
	}

	public static int getVersionCodeFromFancyVersionString(String versionString)
	{
		String[] codes = versionString.split("[. ]");
		if (codes.length >= 2)
		{
			return Integer.parseInt(codes[1]); 
		}
		return 0; 		
	}
	
	private static String getVersionFromMarket(String packageName) {
		String urlString = "https://market.android.com/details?id=" + packageName;
		String versionFromMarket = getVersionFromMarketWebsite(urlString, packageName);
		int versionCodeFromMarket = getVersionCodeFromVersionString(versionFromMarket);
			
		urlString = "http://web.mit.edu/CITY/" + packageName + ".html";
		String versionFromCITYWebsite = getVersionFromCITYWebsite(urlString, packageName);
		int versionCodeFromCITYWebsite = getVersionCodeFromVersionString(versionFromCITYWebsite);

		return "1." + ((int) Math.max((int)versionCodeFromMarket, (int) versionCodeFromCITYWebsite));
	}
	
	private static String getVersionFromCITYWebsite(String urlString, String packageName) {
		String htmlSource = "";
		// using the https site since http doesn't seem to work
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	
			// get html as string
			InputStream is = new BufferedInputStream(urlConnection.getInputStream(),8000);
			BufferedReader r = new BufferedReader(new InputStreamReader(is),8000);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				sb.append(line);
			}
			htmlSource = sb.toString();

			urlConnection.disconnect();
		} catch (MalformedURLException e) {
			Log.e(TAG, "[getVersionFromMarket()] MalformedURLException for url: "
					+ urlString + ", " + e.getMessage());
			return "";
		} catch (IOException e) {
			Log.e(TAG, "[getVersionFromMarket()] IOException for url: " + urlString
					+ ", " + e.getMessage());
			return "";
		}

		// look for version in html
		Pattern p = Pattern
				.compile("Current Version\\:(.*?):");
		Matcher m = p.matcher(htmlSource);
		while (m.find()) {
			return m.group(1);
		}
		//Log.e("[getVersionFromMarket()] No regular expression match in market html for package: " + packageName);
		return "";
	}
	
	private static String getVersionFromMarketWebsite(String urlString, String packageName) {
		String htmlSource = "";
		// using the https site since http doesn't seem to work
		try {
			URL url = new URL(urlString);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.setHostnameVerifier(new HostnameVerifier() {
				// if this isn't included, will get a "Hostname not verified"
				// error
//				@Override
//				public boolean verify(String hostname, SSLSession session) {
//					return true;
//				}

				@Override
				public boolean verify(String hostname, SSLSession session) {
					// TODO Auto-generated method stub
					return true;
				}
			});

			// get html as string
			InputStream is = new BufferedInputStream(urlConnection.getInputStream(),8000);
			BufferedReader r = new BufferedReader(new InputStreamReader(is),8000);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				sb.append(line);
			}
			htmlSource = sb.toString();

			urlConnection.disconnect();
		} catch (MalformedURLException e) {
			Log.e(TAG, "[getVersionFromMarket()] MalformedURLException for url: "
					+ urlString + ", " + e.getMessage());
			return "";
		} catch (IOException e) {
			Log.e(TAG, "[getVersionFromMarket()] IOException for url: " + urlString
					+ ", " + e.getMessage());
			return "";
		}

		// look for version in html
		Pattern p = Pattern
				.compile("\\<dt\\>Current Version\\:\\<\\/dt\\>\\<dd\\>(.*?)\\<\\/dd\\>");
		Matcher m = p.matcher(htmlSource);
		while (m.find()) {
			return m.group(1);
		}
		//Log.e("[getVersionFromMarket()] No regular expression match in market html for package: " + packageName);
		return "";
	}	
}