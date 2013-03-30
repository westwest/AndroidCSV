package se.acoder.androidcsv.file;

public class Row {
	private String rowString;
	
	public Row(String[] values){
		int length = values.length;
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i<length; i++){
			sb.append(values[i]).append(";");
		}
		rowString = sb.toString();
	}
	public Row(String csvRow){
		rowString = csvRow;
	}
	
	public String toString(){
		return rowString.toString();
	}
	
	public String[] rebuild(){
		return rowString.split(";");
	}
	
	/**
	 * If rowString is equal rows is considered to be equal.
	 */
	public boolean equals(Object other){
		if(other instanceof Row){
			Row foreign = (Row) other;
			return rowString.equals(foreign.rowString);
		}
		return false;
	}
}
