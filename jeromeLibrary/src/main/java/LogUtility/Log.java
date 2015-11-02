package LogUtility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;


public class Log 
{
	public enum LogLevel 
	{
		ERROR(0),
		DEBUG(1),
		INFO(2);
		
		public int type;
		LogLevel(int p) 
		{
			type = p;
		}
	}
	//private String filePath;
	private static File _LogFile;
	private static Writer _Writer;
	private static boolean _IsRecord = false;
	private static String _FileName = "log";
	private static String _FolderPath = "JeromeLibrary";
	private static Date _Date = new Date();
	private static SimpleDateFormat _SimpleDateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static LogLevel _LogLevel = LogLevel.DEBUG;
	public static void SetLogFolderName(String folderName)
	{
		_FolderPath = folderName;
	}
	public static void SetLogLevel(LogLevel level)
	{
		_LogLevel = level;
	}
	public static boolean WriteLog(String className,String methodName, String msg)
	{
		return write(_IsRecord, className, methodName, msg + "\n", _LogLevel);	
	}
	
	public static boolean WriteLog(String className,String methodName, String msg, boolean enforceRecord, LogLevel logLevel)
	{
		return write(enforceRecord, className, methodName,  msg, logLevel);	
	}
	public static boolean WriteLog(String className,String methodName, byte[] msg, LogLevel logLevel)
	{
		return write(_IsRecord, className, methodName,  msg, logLevel);	
	}
	public static boolean WriteLog(String className,String methodName, String msg, LogLevel logLevel)
	{
		return write(_IsRecord, className, methodName,  msg, logLevel);	
	}
	public static void enableRecord(boolean flag)
	{
		_IsRecord = flag;
	}
	private static boolean write(boolean bRecord, String className,String methodName, byte[] msg, LogLevel logLevel)
	{
		if (bRecord)
		{
		    File root = Environment.getExternalStorageDirectory();
		    File outDir = new File(root.getAbsolutePath() + File.separator + _FolderPath);
		    if (!outDir.isDirectory()) 
		    {
		      outDir.mkdir();
		    }
			String fileName = _FileName;
			if (logLevel == LogLevel.ERROR)
				fileName = fileName +".error";
			if (logLevel == LogLevel.DEBUG)
				fileName = fileName +".debug";
			if (logLevel == LogLevel.INFO)
				fileName = fileName +".info";
		    _LogFile = new File(outDir, fileName);

			try 
			{
				_Writer = new BufferedWriter(new FileWriter(_LogFile, true));
				_Date = new Date();
				String retStrFormatNowDate = _SimpleDateFormatter.format(_Date);
				StringBuilder strMsg = new StringBuilder();
				for(int i = 0; i < msg.length - 1;i++)
				{
					strMsg.append(String.format("%d,", msg[i]));
				}
				_Writer.write("[" + retStrFormatNowDate +"-> " + className + "->" + methodName + "]: "+ strMsg +"\n");
				_Writer.flush();
				_Writer.close();
				return true;
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;		
		
	}
	
	private static boolean write(boolean bRecord, String className,String methodName, String msg, LogLevel logLevel)
	{
		if (bRecord)
		{
		    File root = Environment.getExternalStorageDirectory();
		    File outDir = new File(root.getAbsolutePath() + File.separator + _FolderPath);
		    if (!outDir.isDirectory()) 
		    {
		      outDir.mkdir();
		    }
			String fileName = _FileName;
			if (logLevel == LogLevel.ERROR)
				fileName = fileName +".error";
			if (logLevel == LogLevel.DEBUG)
				fileName = fileName +".debug";
			if (logLevel == LogLevel.INFO)
				fileName = fileName +".info";
		    _LogFile = new File(outDir, fileName);

			try 
			{
				_Writer = new BufferedWriter(new FileWriter(_LogFile, true));
				_Date = new Date();
				String retStrFormatNowDate = _SimpleDateFormatter.format(_Date);
					
				_Writer.write("[" + retStrFormatNowDate +"-> " + className + "->" + methodName + "]: "+ msg +"\n");
				_Writer.flush();
				_Writer.close();
				return true;
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;		
		
	}
}
