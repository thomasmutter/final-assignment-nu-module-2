package fileConversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConversionHandler {

	public byte[] readFileToBytes(String path) throws FileNotFoundException {
		File file = new File(path);
		byte[] fileAsBytes = new byte[(int) file.length()];

		FileInputStream in = new FileInputStream(file);
		System.out.println("Reading file from " + path + " and converting to byte array");
		try {
			in.read(fileAsBytes);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File conversion: succes");

		return fileAsBytes;
	}

	public void writeBytesToFile(byte[] bytes, String path) {
		File file = new File(path);

		try {
			FileOutputStream out = new FileOutputStream(file);
			System.out.println("Writing bytes to file in directory: " + path);
			out.write(bytes);
			System.out.println("File conversion: succes");
			out.close();
		} catch (IOException e) {
			System.out.println("Directory does not exist");
			e.printStackTrace();
		}
	}
}
