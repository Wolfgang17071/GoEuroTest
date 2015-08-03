// GoEuroTest 
// Implement an API query and transform this data into a csv file 

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
 
public class GoEuroTest 
{
  private static String USER_AGENT = "Mozilla/5.0";
  static boolean fileExists = false;
  static String goEuroBase = "http://api.goeuro.com/api/v2/position/suggest/en/";
  public static void main(String[] args) 
  {
	if (args.length == 0)
	{
		usage();
		return;
	}
		
    for ( int i = 0; i<args.length; i++ ) 
	{
		try 
		{
			String location = queryGoEuro(args[i]);
			if (location.length() < 5)
				continue;
			String o = theJSONoutput(location);
			if (o.length() < 1)
				continue;
			writeFile(args[0], o);
		}

        catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Error: Input parameter Index " + i);
            System.out.println("Message is " + e.getMessage());
		}
	} //for
  } // main
  
  private static void usage()
  {
	System.out.println("Usage: java -jar GoEuroTest.jar location1 location2 ..." );
	System.out.println("A query with every location is performed, five parameters out of the reply are put in a line of the file \"<location1>.csv\"" );
  }
  
  private static String queryGoEuro(String userInput)
  {
	String goEuroLocation = goEuroBase + userInput;
	StringBuffer response =	new StringBuffer();
			
	try
	{
		URL mySite = new URL(goEuroLocation);
		HttpURLConnection con = (HttpURLConnection) mySite.openConnection();
		con.setRequestMethod ("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		BufferedReader in = new BufferedReader(
			new InputStreamReader(con.getInputStream()));
		String inputLine;
 
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();
		if (response.length() < 5)
		{
			System.out.println("No data for location " + userInput);
		}
	}
	catch (IOException e)
	{
		System.out.println("error reading " + goEuroLocation + "\nMessage: " + e.getMessage());
		e.printStackTrace();
	}
	finally
	{
		return (response.toString());
	}
		
  } // queryGoEuro
  
  private static String theJSONoutput (String jsonInput)
  {
	int index1, index2, index3;
	String outputString = "";
	
	final String[] ids = new String[] {"_id", "name", "type", "latitude", "longitude"};
	final int [] offset = new int[] {2, 3, 3, 2, 2};
	final int[] separator = new int[] {',', '"', '"', ',', '}'};
	final String outputSeparator = ",";
	//final String eol = "\n";
	
	for (int i=0; i<ids.length; i++)
	{
		index1 = jsonInput.indexOf (ids[i]);
		if (index1 < 0)
		{
			System.out.println("Error in location, key " + ids[i] + " not found!");
			return "";
		}
		index2 = index1 + ids[i].length() + offset[i];
		index3 = jsonInput.indexOf (separator[i], index2);
		if (index3 == -1 || index3 > jsonInput.length())
		{
			System.out.println("Error in location, parameter of key " + ids[i] + " missing!");
			return "";
		}
		String s = jsonInput.substring (index2, index3);
		outputString = outputString + s;
		if (i < ids.length -1)
			outputString = outputString + outputSeparator;
	} // for
	return outputString;
  } // theJSONoutput
  
  
  static void writeFile(String filename, String theOutput)
  {
	FileWriter writer;
	File file;
	final String filetype = ".csv";

    file = new File(filename + filetype);
	if (fileExists == false && file.exists())
	{
		try
		{
			file.delete();
			fileExists = true;
		}
		catch (SecurityException e) 
		{
			System.out.println("Can not delete file " + file.getName());
			e.printStackTrace();
		}
	}
	
    try 
	{
       writer = new FileWriter(file ,true);
       writer.write(theOutput);
       writer.write(System.getProperty("line.separator"));
	   writer.flush();
       writer.close();
    } 
	
	catch (IOException e) 
	{
		System.out.println("Error handling file " + file.getName());
		e.printStackTrace();
    }
	
  } // writeFile
 
} // class GoEuroTest 

