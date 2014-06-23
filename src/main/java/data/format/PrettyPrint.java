package main.java.data.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class PrettyPrint {
	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		}
		finally {
			stream.close();
		}
	}

	/*	private static String readFile(String filePath) throws Exception {
		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[1024];
			int numRead=0;
			while((numRead=reader.read(buf)) != -1) fileData.append(String.valueOf(buf, 0, numRead));
		} catch(IOException e) {
			System.out.println( e.getMessage() );
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				//Ignoring this exception
			}			
		}
		return fileData.toString();
	}
	 */
	public static String getPrettyXml(String xmlStr) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 4);
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult result = new StreamResult(new StringWriter());
		StreamSource source = new StreamSource(new StringReader(xmlStr));
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();
		return xmlString;
	}

	public static String getPrettyJson(String jsonStr) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonReader jr = new JsonReader(new StringReader( jsonStr ));
		jr.setLenient(true);
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(jr);
		return gson.toJson(je);
/*		
		DataObject obj = new DataObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(jsonStr);
		return json;*/
	}

	public static void main(String[] args) throws TransformerException {
		String usage="Usage:  pp (-j | -x) <filepath>";
		String fileStr="";
		try {
			if (args.length < 2) throw new Exception( usage );

			if (args[0].equals( "-x" )) {
				fileStr = readFile( args[1] );
				System.out.println(getPrettyXml(fileStr));
			}
			else if (args[0].equals( "-j")) {
				fileStr = readFile( args[1] );
				System.out.println(getPrettyJson(fileStr));
			}
			else
				throw new Exception( usage );


		} catch ( IOException ioe ) {
			System.out.println( "Error reading " + args[0] + "\n" + ioe.getStackTrace());
		} 
		catch(TransformerException te) {
			System.out.println( "Error transforming xml file.  Ensure that file " + args[0] + " contains well formed xml.");
		}
		catch ( Exception e) {
			if (e!=null && e.getMessage()!=null)
				System.out.println( e.getMessage() );
		}
		//				"<root><child><sub>ddd</sub></child><child/></root>";
	}
}