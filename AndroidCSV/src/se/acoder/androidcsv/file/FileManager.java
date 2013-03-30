package se.acoder.androidcsv.file;

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
		
		manager_settings = new FilePath("barsys_filemanager", c);
		if(FileIO.fileExists(manager_settings)){
			List<Row> rawFilePaths = FileIO.readFile(manager_settings);
			Log.i(TAG, "Loads managed file-paths");
			for(Row rawFilePath : rawFilePaths ){
				managedFiles.add(new FilePath(rawFilePath.rebuild()[0], c));
			}
		}
	}
	
	public synchronized boolean deleteFile(FilePath fp){
		managedFiles.remove(fp);
		if(FileIO.fileExists(fp)){
			Log.d(TAG,"Deletes file '"+fp.getName()+"'");
			FileIO.deleteFile(fp);
			return true;
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
			if(!FileIsManaged(fp))
				managedFiles.add(fp);
			return true;
		}
		return false;
	}
	
	/**
	 * Access content of a file. 
	 * @param fp
	 * @return 
	 */
	public List<Row> readFile(FilePath fp){
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
	 * Method should be called as part of shutdown procedure in order to ensure
	 * that state-data is saved correctly.
	 */
	public void controlledShutdown(){
		//appendFile(managedFiles, )
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
		return managedFiles.contains(fp);
	}
}
