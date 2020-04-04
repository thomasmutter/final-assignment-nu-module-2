package fileManipulationTests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fileConversion.ConversionHandler;

class ConversionHandlerTest {
	private ConversionHandler handler;
	private static final String DIR = "src/main/java/com/nedap/university/test/fileManipulationTests";

	@BeforeEach
	void setUp() throws Exception {
		handler = new ConversionHandler();
	}

	@Test
	void testFileToBytesConversion() {
		String fileContents = "This file is used for t3s+!nG.";
		byte[] contentBytes = fileContents.getBytes();
		byte[] fileBytes = handler.readFileToBytes(DIR + File.separator + "testFile");

		assertArrayEquals(contentBytes, fileBytes);
	}

}
