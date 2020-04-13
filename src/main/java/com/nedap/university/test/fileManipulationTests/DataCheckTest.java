package fileManipulationTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataIntegrity.DataCheck;
import fileConversion.ConversionHandler;

class DataCheckTest {

	private DataCheck checksum;
	private ConversionHandler handler;
	private static final String DIR = "src/main/java/com/nedap/university/test/fileManipulationTests";

	@BeforeEach
	void setUp() throws Exception {
		checksum = new DataCheck();
		handler = new ConversionHandler();
	}

	@Test
	void testCRC() {
		byte[] file = handler.readFileToBytes(DIR + File.separator + "tiny.pdf");
		int remainder = checksum.calculateRemainder(file);
		assertFalse(remainder == 0);
		byte[] fileWithCRC = checksum.appendCRC(file);
		int remainderWithCRC = checksum.calculateRemainder(fileWithCRC);
		assertTrue(remainderWithCRC == 0);
	}
}
