package se.acoder.androidcsv.test.file;

import java.util.Arrays;

import se.acoder.androidcsv.file.Row;

import android.test.AndroidTestCase;

public class RowTest extends AndroidTestCase {
	//General-purpose Test-data
	private String csvString = "elem1;elem2;elem3;";
	private String[] valueArr = {"elem1", "elem2", "elem3"};
	
	public void testConstruct(){
		Row csvRow = new Row(valueArr);
		assertEquals("Constructed csv-row at unexpected format", 
				csvString, csvRow.toString());
	}
	
	public void testEqual(){
		Row r1 = new Row(valueArr);
		Row r2 = new Row(valueArr.clone());
		assertEquals("Object should be same as itself", r1, r1);
		assertEquals("Same string-values shoud render in equality", r1, r2);
		
		String[] differentArr = {"object1", "object2"};
		Row differentRow = new Row(differentArr);
		assertFalse("Two unique different datasets should not render in equality", 
				r1.equals(differentRow));
	}
	
	public void testRebuild(){
		Row r1 = new Row(valueArr);
		assertTrue("Rebuild should render original data-array", Arrays.equals(valueArr, r1.rebuild()));
	}
}
