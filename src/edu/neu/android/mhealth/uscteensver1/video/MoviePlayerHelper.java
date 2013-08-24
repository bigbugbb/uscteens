package edu.neu.android.mhealth.uscteensver1.video;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import edu.neu.android.mhealth.uscteensver1.support.TeenException;

public class MoviePlayerHelper {
    /**
     * save movie link and index into csv file
     */
    private static final String TAG = "MoviePlayerHelper";
    public static String movieListFilePath = ".city/tutorials/";
    public static String movieListFileName = "MovieList.csv";
    public static String movieListFile = movieListFilePath + movieListFileName;
    public static String tableID_MovieLink = "Youtube Link ID";
    public static String tableID_MovieIndex = "Index";
    public static String movieDesc = "Movie Entry information";

    public static void saveMovieList(String youtubeLink) throws TeenException {
//		edu.neu.android.wocketslib.utils.FileHelper.createDir(movieListFilePath);
//		StringWriter buffer = new StringWriter();
//		if (!edu.neu.android.wocketslib.utils.FileHelper.isFileExists(movieListFile)){
//			buffer.append(tableID_MovieLink+","+tableID_MovieIndex+"\n");
//			buffer.append(youtubeLink+",1\n");
//			//FileHelper.appendToFile(buffer.toString(), movieListFile, movieDesc);
//		}
//		else{
//			InputStream is = FileHelper.openFileForRead(movieListFile, movieDesc, true);
//			int index = 0;
//			try {
//				ArrayList<String[]> entries = new ArrayList<String[]>();
//				CSVReader reader = new CSVReader(new InputStreamReader(is));
//				while (true) {
//					String[] columns = reader.readNext();
//					if (columns == null || columns.length == 0)
//						break;
//					if(!columns[0].equalsIgnoreCase(tableID_MovieLink))
//						entries.add(columns);
//				}
//				for (String[] strings : entries) {
//					if(strings[0].equalsIgnoreCase(youtubeLink)){
//
//						Log.i(TAG, "Movie Exists!");
//
//						return;
//					}
//				}
//				index = entries.size()+1;
//				buffer.append(youtubeLink+","+index+"\n");
//				FileHelper.appendToFile(buffer.toString(), movieListFile, movieDesc);
//			} catch (IOException ex) {
//			} finally {
//				if (is != null)
//					try {
//						is.close();
//					} catch (IOException e) {
//					}
//			}
//
//		}
    }

    /**
     * save played movie info into csv file
     */
    public static String playedFilepath = ".city/tutorials/";
    public static String playedFileName = "MoviePlayed.csv";
    public static String playedFile = playedFilepath + playedFileName;
    public static String tableID_MoviePercentage = "PercentageViewed";
    public static String tableID_MovieViewDate = "ViewDate";
    public static String Date_format = "yyyy-MM-dd HH:mm:ss.SSSZ";

    public static void savePlayedMovie(String youtubeLink, String percentage) throws TeenException {
//		FileHelper.createDir(playedFilepath);
//		StringWriter buffer = new StringWriter();
//		Date now = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat(Date_format);
//		String currentTime = sdf.format(now);
//		if (!FileHelper.fileExists(playedFile)){
//			buffer.append(tableID_MovieIndex+","+tableID_MovieLink+","+
//					tableID_MoviePercentage+","+tableID_MovieViewDate+"\n");
//			buffer.append("1,"+youtubeLink+","+percentage+","+currentTime+"\n");
//			FileHelper.appendToFile(buffer.toString(), playedFile, movieDesc);
//		}
//		else{
//			InputStream is = FileHelper.openFileForRead(playedFile, movieDesc, true);
//			try {
//				ArrayList<String[]> entries = new ArrayList<String[]>();
//				CSVReader reader = new CSVReader(new InputStreamReader(is));
//				while (true) {
//					String[] columns = reader.readNext();
//					if (columns == null || columns.length == 0)
//						break;
//					if(!columns[1].equalsIgnoreCase(tableID_MovieLink)&&!columns[1].equalsIgnoreCase(youtubeLink))
//						entries.add(columns);
//				}
//				buffer.append(tableID_MovieIndex+","+tableID_MovieLink+","+
//						tableID_MoviePercentage+","+tableID_MovieViewDate+"\n");
//				CSVWriter writer = new CSVWriter(buffer);
//				writer.writeAll(entries);
//				buffer.append(MoviePlayerHelper.getIndex(youtubeLink)+","+youtubeLink+","+percentage+","+currentTime+"\n");
//				FileHelper.overwriteFile(buffer.toString(), playedFile, movieDesc);
//			} catch (IOException ex) {
//			} finally {
//				if (is != null)
//					try {
//						is.close();
//					} catch (IOException e) {
//					}
//			}
//		}
    }

    /**
     * find movie index by url
     *
     * @throws CITYException
     */
    public static String getIndex(String youtubeID) throws TeenException {
//		FileHelper.createDir(movieListFilePath);
//		if (!FileHelper.fileExists(movieListFile)){
//			return null;
//		}
//		else{
//			InputStream is = FileHelper.openFileForRead(movieListFile, movieDesc, true);
//			try {
//				CSVReader reader = new CSVReader(new InputStreamReader(is));
//				while (true) {
//					String[] columns = reader.readNext();
//					if (columns == null || columns.length == 0)
//						break;
//					if(columns[0].equalsIgnoreCase(youtubeID))
//						return columns[1];
//				}
//				return null;
//			} catch (IOException ex) {
//			} finally {
//				if (is != null)
//					try {
//						is.close();
//					} catch (IOException e) {
//					}
//			}
//		}
        return null;
    }

    /**
     * find movie url by index
     *
     * @throws CITYException
     */
    public static String getUrl(String index) throws TeenException {
//		index=Integer.toString(Math.min(10, Integer.parseInt(index)));
//		FileHelper.createDir(movieListFilePath);
//		if (!FileHelper.fileExists(movieListFile)){
//			return null;
//		}
//		else{
//			InputStream is = FileHelper.openFileForRead(movieListFile, movieDesc, true);
//			try {
//				CSVReader reader = new CSVReader(new InputStreamReader(is));
//				while (true) {
//					String[] columns = reader.readNext();
//					if (columns == null || columns.length == 0)
//						break;
//					if(columns[1].equalsIgnoreCase(index))
//						return columns[0];
//				}
//				return null;
//			} catch (IOException ex) {
//			} finally {
//				if (is != null)
//					try {
//						is.close();
//					} catch (IOException e) {
//					}
//			}
//		}
        return null;
    }

    /**
     * find percentage by index
     *
     * @throws CITYException
     */
    public static String getPercentage(String index) throws TeenException {
//		FileHelper.createDir(playedFilepath);
//		if (!FileHelper.fileExists(playedFile)){
//			return null;
//		}
//		else{
//			InputStream is = FileHelper.openFileForRead(playedFile, movieDesc, true);
//			try {
//				CSVReader reader = new CSVReader(new InputStreamReader(is));
//				while (true) {
//					String[] columns = reader.readNext();
//					if (columns == null || columns.length == 0)
//						break;
//					if(columns[0].equalsIgnoreCase(index))
//						return columns[2];
//				}
//				return null;
//			} catch (IOException ex) {
//			} finally {
//				if (is != null)
//					try {
//						is.close();
//					} catch (IOException e) {
//					}
//			}
//		}
        return null;
    }

    /**
     * find view history by percentage
     *
     * @throws CITYException
     */
    public static boolean getisViewed(String p) throws TeenException {
        double percentage = 0;
        if (p != null)
            percentage = Double.parseDouble(p);
        return percentage > 0.8;
    }

    /**
     * get youtube video info from server
     */
    public static String[] getMovieNameLengthIcon(String url) {
        String requestStr = "https://gdata.youtube.com/feeds/api/videos/" + url + "?v=2";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(requestStr);
            HttpResponse response = client.execute(getRequest);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseStr = EntityUtils.toString(entity);
                    String title = responseStr.substring(responseStr.indexOf("<media:title"), responseStr.indexOf("media:title>"));
                    title = title.substring(title.indexOf(">"), title.indexOf("</"));
                    title = title.substring(1);
                    String duration = responseStr.substring(responseStr.indexOf("<yt:duration"));
                    duration = duration.substring(duration.indexOf("seconds='"), duration.indexOf("'/>"));
                    duration = duration.substring(9);
                    String iconLink = responseStr.substring(responseStr.indexOf("<media:thumbnail"));
                    iconLink = iconLink.substring(iconLink.indexOf("url='"), iconLink.indexOf(".jpg'"));
                    iconLink = iconLink.substring(5) + ".jpg";
                    String[] movieInfo = new String[]{title, duration, iconLink};
                    return movieInfo;
                } else {
                    return null;
                }
            } else {
                Log.i(TAG, "No response from server");
                return null;
            }
        } catch (ClientProtocolException e) {
            Log.i(TAG, "Json ClientProtocolException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, "IOException");
            e.printStackTrace();
        }
        return null;
    }
}
