package se.acoder.androidcsv.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * The class manages actual interaction with device storage through
 * static methods only exposed to the package.
 * @author Johannes Westlund
 *
 */
public class FileIO {
	private final static String TAG = FileIO.class.getSimpleName();
	
	protected static List<Row> readFile(FilePath fp){
		List<Row> rows = new ArrayList<Row>();
		Log.i(TAG, "Attempts to read file");
		try {
			BufferedReader br = new BufferedReader( 
					new InputStreamReader(fp.getContext()
							.openFileInput(fp.getName())));
			Log.i(TAG, "Buffer setup correctly");
			String rawRow;
			while( (rawRow = br.readLine()) != null){
				Log.d(TAG, rawRow);
				rows.add(new Row(rawRow));
			}
			br.close();
			return rows;
		} catch (FileNotFoundException e) {
			Log.d(TAG, "File '" + fp.getName() + "' not found");
		} catch (IOException ie){
			Log.d(TAG, "IOException when attempting to read '" + fp.getName()+"'");
		}
		return null;
	}
	
	protected static boolean removeRow(FilePath path, Row row){
		Log.i(TAG, "Attempting removeRow");
		if(fileExists(path)){
			try {
				File f = new File(path.getURI());
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				boolean found = false;
				String csvRow = row.toString();
				String line;
				long lastPos = 0;
				Log.i(TAG, "Tries to find row '"+csvRow+"'");
				while(!found && (line = raf.readLine()) != null){
					Log.d(TAG, "Current row: '"+line+"'");
					if(line.equals(csvRow)){
						Log.d(TAG, "Matching row was found");
						found = true;
						String restOfFile = "";
						String tempStr;
						while((tempStr = raf.readLine()) != null){
							restOfFile+=tempStr;
							restOfFile+="\n";
						}
						raf.seek(lastPos);
						raf.setLength(lastPos);
						raf.writeBytes(restOfFile);
						raf.writeBytes("");
						raf.close();
						return true;
					}
					lastPos = raf.getFilePointer(); 
				}
				raf.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG,"File not found.");
			} catch (IOException e) {
				Log.d(TAG, "IO Exception");
			}
		}
		return false;
	}
	
	public static synchronized boolean fileExists(FilePath path){
		Log.d(TAG, "Test existance of '" + path.getURI() + "'");
		//File f = new File(path.getURI());
		File f = path.getContext().getFileStreamPath(path.getName());
		return f.isFile();
	}
	protected static boolean appendFile(FilePath fp, List<Row> rows) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					fp.getContext().openFileOutput(fp.getName(), Context.MODE_APPEND)));
			for(Row row : rows){
				Log.d(TAG, row.toString());
				bw.write(row.toString());
				bw.newLine();
			}
			bw.close();
			return true;
		} catch (FileNotFoundException e) {
			Log.d(TAG, "File not found, '"+ fp.getName() +"'");
		} catch (IOException ioe){
			Log.d(TAG, "IOException when writing to '" + fp.getName() + "'");
		}
		return false;
	}
	protected static boolean deleteFile(FilePath fp){
		File f = new File(fp.getURI());
		return f.delete();
	}
	
	/**
	 * Exports given file to the public file-system to directory root/[packageName]
	 * @param fp
	 * @return
	 */
	public static boolean saveExternal(FilePath fp){
		String root = Environment.getExternalStorageDirectory().toString();
		File loggerDir = new File(root + "/" + fp.getContext().getPackageName()); 
		loggerDir.mkdirs();
		File dst = new File(loggerDir, fp.getName());
		File src = new File(fp.getURI());
		try {
			copy(src,dst);
			Log.i(TAG, "Saved to '" + dst +"'");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
}
