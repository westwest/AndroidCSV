package se.acoder.androidcsv.test.file;

import java.util.ArrayList;
import java.util.List;

import se.acoder.androidcsv.file.FileIO;
import se.acoder.androidcsv.file.FileManager;
import se.acoder.androidcsv.file.FilePath;
import se.acoder.androidcsv.file.Row;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import android.test.mock.MockContentResolver;

public class FileTest extends AndroidTestCase {
	private Context mockContext;
	private FileManager fm;
	private FilePath fp;
	private List<Row> generalTestData = new ArrayList<Row>();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mockContext = new IsolatedContext(new MockContentResolver(), getContext());
		fm = FileManager.getInstance(mockContext);
		fp = new FilePath("test", mockContext);
		
		//Generate testdata matrix
		for(int i = 0; i<5; i++){
			String[] cols = new String[7];
			for(int j = 0; j<7; j++)
				cols[j] = "test_"+i+"."+j;
			generalTestData.add(new Row(cols));		
		}
	}
	
	public void testFileManagerSingleton(){
		FileManager fm1 = FileManager.getInstance(mockContext);
		FileManager fm2 = FileManager.getInstance(mockContext);
		assertEquals("Singleton should ensure that pointers" +
				" references same instance", fm1, fm2);
	}
	
	public void testFileExistsAndDelete(){
		assertTrue("File not created successfully", fm.appendFile(fp, generalTestData));
		assertTrue("File does not exist", FileIO.fileExists(fp));
		assertTrue("File was not deleted", fm.deleteFile(fp));
		assertFalse("Deleted file cannot exist", FileIO.fileExists(fp));
		assertFalse("Deleted file cannot be managed", FileManager.FileIsManaged(fp));
	}
	
	public void testFileRead(){
		assertTrue("File not created successfully", fm.appendFile(fp, generalTestData));
		List<Row> rowsFromFile = fm.readFile(fp);
		assertTrue("Read rows should not be null", rowsFromFile != null);
		assertEquals("Read rows should equals saved rows", generalTestData, rowsFromFile);
	}
}
