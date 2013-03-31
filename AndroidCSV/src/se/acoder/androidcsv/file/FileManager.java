package se.acoder.androidcsv.file;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import android.util.Log;

/**
 * Class provides interface to the file-io functionality and keeps track of
 * files the app manages. Designed to manage several simultaneous threads.
 * @author Johannes Westlund
 *
 */
public class FileManager {
	private final static String TAG = FileManager.class.getSimpleName();
	private static FilePath manager_settings;
	private static LinkedList<FilePath> managedFiles;
	private static FileManager instance;
	
	/**
	 * Default way of access singleton class
	 * @param c Context of caller app.
	 * @return The instance of FileManager.
	 */
	public static FileManager getInstance(Context c){
		if(instance == null){
			synchronized ( FileManager .class){
				if(instance == null){
					instance = new FileManager(c);
				}
			}
		}
		return instance;
	}
	
	/**
	 * Private constructor
	 */
	private FileManager(Context c){
		managedFiles = new LinkedList<FilePath>();
		
		manager_settings = new FilePath("filemanager", c);
		if(FileIO.fileExists(manager_settings)){
			List<Row> rawFilePaths = FileIO.readFile(manager_settings);
			Log.i(TAG, "Loads managed file-paths");
			for(Row rawFilePath : rawFilePaths ){
				String[] values = rawFilePath.rebuild();
				managedFiles.add(new FilePath(values[0], c, Extention.createExtention(values[1])));
			}
		}
	}
	
	private Row filePathToRow(FilePath fp){
		String[] fpSaveFormat = {fp.getSimpleName(), fp.getExtention().replace(".", "")};
		return new Row(fpSaveFormat);
	}
	
	public synchronized boolean deleteFile(FilePath fp){
		if(managedFiles.contains(fp)){
			managedFiles.remove(fp);
			FileIO.removeRow(manager_settings, filePathToRow(fp));
			if(FileIO.fileExists(fp)){
				Log.d(TAG,"Deletes file '"+fp.getName()+"'");
				FileIO.deleteFile(fp);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Creates a new file if file not present. Appends to end without overwriting
	 * if file is present.
	 * @param fp File-path
	 * @return Success or fail.
	 */
	public synchronized boolean appendFile(FilePath fp, List<Row> rows){
		if(FileIO.appendFile(fp, rows)){
			if(!FileIsManaged(fp)){
				managedFiles.add(fp);
				appendFile(manager_settings, filePathToRow(fp));
			}
			return true;
		}
		return false;
	}
	public synchronized boolean appendFile(FilePath fp, Row row){
		List<Row> list = new ArrayList<Row>();
		list.add(row);
		return appendFile(fp, list);
	}
	
	/**
	 * Mostly implemented for test reasons. Normally it is inefficient to just
	 * fiddle around with individual rows. Better to write a temp-file and overwrite
	 * or merge.
	 * @param fp
	 * @param row
	 * @return
	 */
	public synchronized boolean removeRow(FilePath fp, Row row){
		if(FileIsManaged(fp))
			return FileIO.removeRow(fp, row);
		return false;
	}
	
	/**
	 * Access content of a file. 
	 * @param fp
	 * @return 
	 */
	public List<Row> readFile(FilePath fp){
		Log.d(TAG, "File is managed: " +FileIsManaged(fp));
		Log.d(TAG, "File exists: " +FileIO.fileExists(fp));
		if(FileIsManaged(fp) && FileIO.fileExists(fp)){
			return FileIO.readFile(fp);
		}
		return null;
	}
	
	public boolean copyToExt(FilePath fp){
		if(FileIsManaged(fp) && FileIO.fileExists(fp))
			return FileIO.saveExternal(fp);
		return false;
	}
	
	/**
	 * Accessor for the full list of managed files.
	 * @return A list of all files the app manages.
	 */
	public List<FilePath> getManagedFiles(){
		List<FilePath> ls = new LinkedList<FilePath>();
		for(FilePath path : managedFiles){
			ls.add(path);
		}
		return ls;
	}
	
	/**
	 * Checks if given filepath is known by the FileManager.
	 * @param fp Filepath to check
	 * @return True if known.
	 */
	public static boolean FileIsManaged(FilePath fp){
		if(fp.equals(manager_settings))
			return true;
		return managedFiles.contains(fp);
	}
}
