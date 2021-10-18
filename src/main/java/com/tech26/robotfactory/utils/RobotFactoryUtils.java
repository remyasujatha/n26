package com.tech26.robotfactory.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.util.ResourceUtils;

/**
 * @author Remya
 * 
 * Class providing utility functions for the application
 *
 */
public class RobotFactoryUtils {

	public static synchronized  boolean writeToFile(String fileName, String content) {
		  FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(fileName);
			 byte[] strToBytes = content.getBytes();
			    outputStream.write(strToBytes);

			    outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		   
		return true;
	}

}
