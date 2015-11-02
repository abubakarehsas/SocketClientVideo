package LogUtility;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FileUtilities {
  private Writer _Writer;
  private String _AbsolutePath = "SuperGISRuntimeSDK";
  private final Context _Context;

  public FileUtilities(Context context) {
    super();
    this._Context = context;
  }
  public void SetFolderName(String folderName)
  {
	  _AbsolutePath = folderName;
  }
  public boolean write(String folderPath, String fileName, String data) 
  {
	  File outDir = new File(folderPath);
	
	  if (!outDir.isDirectory()) 
	  {
		  outDir.mkdir();
	  }
	  try 
	  {
		  if (!outDir.isDirectory()) 
		  {
			  return false;
			  
		  }
		  File outputFile = new File(outDir, fileName);
		  _Writer = new BufferedWriter(new FileWriter(outputFile));
		  _Writer.write(data);
		  if (_Context != null) 
		  {
			  Toast.makeText(
					  _Context.getApplicationContext(),
						"Report successfully saved to: "
								+ outputFile.getAbsolutePath(),
						Toast.LENGTH_LONG).show();
		   }
		  _Writer.close();
	  } 
	  catch (IOException e) 
	  {
		  Log.w("eztt", e.getMessage(), e);
		  if (_Context != null) 
		  {
			 Toast.makeText(
					_Context,
					e.getMessage()
							+ " Unable to write to external storage.",
					Toast.LENGTH_LONG).show();
		  }
		  return false;
	  }
	  return true;
  }
  public boolean write(String fileName, String data) 
  {
    File root = Environment.getExternalStorageDirectory();
    return write(root.getAbsolutePath() + File.separator + _AbsolutePath, fileName, data);


  }
  public void Baste64XMLWrite(String fileName, String base64data) {
	    File root = Environment.getExternalStorageDirectory();
	    File outDir = new File(root.getAbsolutePath() + File.separator + "SuperGISRuntimeSDK");
	    if (!outDir.isDirectory()) {
	      outDir.mkdir();
	    }
	    try 
	    {
	    	if (!outDir.isDirectory()) 
	    	{
	    		throw new IOException( "Unable to create directory EZ_time_tracker. Maybe the SD card is mounted?");
	    	}
	    	File outputFile = new File(outDir, fileName);
	    	_Writer = new BufferedWriter(new FileWriter(outputFile));
	    	_Writer.write(base64data);
	    	_Writer.close();
	    	//writer.close();
	    	
	    	InputStream inputStream = new BufferedInputStream(new FileInputStream(outDir+fileName));
	        if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();

	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	            	stringBuilder.append("\"");
	                stringBuilder.append(receiveString);
	                stringBuilder.append("\"+");
	                stringBuilder.append("\n");
	            }
	            stringBuilder.append(";");
	            inputStream.close();
	            outputFile = new File(outDir, fileName);
	        	_Writer = new BufferedWriter(new FileWriter(outputFile));
	            _Writer.write(stringBuilder.toString());
	            _Writer.close();
	        }
	       
	        
	    } 
	    catch (IOException e) 
	    {
	    	Log.w("eztt", e.getMessage(), e);
	    	if (_Context != null)
	    	{
	    		Toast.makeText(_Context, 
		    					e.getMessage() + " Unable to write to external storage.",
		    					Toast.LENGTH_LONG).show();
	    	}
	    }

	  }
  public Writer getWriter() {
    return _Writer;
  }

  public String getAbsolutePath() {
    return _AbsolutePath;
  }

}