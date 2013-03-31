package se.acoder.androidcsv.file;

import android.content.Context;
import android.util.Log;

public class FilePath {
	private final static String TAG = FilePath.class.getSimpleName();
	private Context context;
	private String fileName;
	private String extention = ".txt";
	
	public FilePath(String fileName, Context context){
		this.context = context;
		this.fileName = fileName;
	}
	public FilePath(String fileName, Context context, Extention extention){
		this(fileName,context);
		this.extention = extention.getExtention();
	}
	
	public String getName(){
		return fileName+extention;
	}
	public Context getContext(){
		return context;
	}
	public String getSimpleName(){
		return fileName;
	}
	public String getExtention(){
		return extention;
	}
	public String getURI(){
		return context.getFileStreamPath(getName()).getAbsolutePath();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof FilePath){
			FilePath foreign = (FilePath) o;
			Log.d(TAG, getName() + " == " + foreign.getName());
			Log.d(TAG, "Outcome: " + (getName().equals(foreign.getName())));
			if(getName().equals(foreign.getName()))
				return true;
		}
		return false;
	}
}
