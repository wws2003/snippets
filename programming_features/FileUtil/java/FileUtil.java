package com.techburg.autospring.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class FileUtil {
	public String getStringFromFile(String fileName) throws Exception {
		InputStream inputStream = new BufferedInputStream(new FileInputStream(fileName));
		StringBuilder contentBuilder = new StringBuilder();
		try {
			getStringFromInputStream(inputStream, contentBuilder);
			return contentBuilder.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			inputStream.close();
		}
	}
	
	public void storeContentToFile(String content, String fileName) throws Exception {
		OutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(fileName));
			writeStringToOutputStream(content, outputStream);
			outputStream.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			outputStream.close();
		}
	}
	
	public void getStringFromInputStream(InputStream inputStream, StringBuilder outputBuilder) throws Exception {
		int c;
		byte[] b = new byte[1024];
		while ((c = inputStream.read(b)) != -1) {
			String chunk = new String(b, 0, c);
			outputBuilder.append(chunk);
		}
	}
	
	public void writeStringToOutputStream(String output, OutputStream outputStream) throws Exception {
		byte[] outputByteArray = output.getBytes(Charset.forName("UTF-8"));
		outputStream.write(outputByteArray);
	}
	
	public void createDirectoryIfNotExist(String directoryPath) {
		File file = new File(directoryPath);
		if(!file.exists()) {
			file.mkdir();
		}
	}
}
