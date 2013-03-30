package se.acoder.androidcsv.file;

import android.content.Context;

public class FilePath {
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
}
