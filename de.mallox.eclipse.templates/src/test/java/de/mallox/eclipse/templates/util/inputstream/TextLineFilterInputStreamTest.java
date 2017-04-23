package de.mallox.eclipse.templates.util.inputstream;

import static junit.framework.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.junit.Test;

public class TextLineFilterInputStreamTest {

	private TextLineFilterInputStream classUnderTest;

	private static final class TestData {
		public TestData() {
		}

		String encoding = "Cp1252";
		String text = "Dies ist ein Test mit ${Ersetzung} \n Dies ist eine weitere Zeile";
		String regExp = "\\$\\{(.*)\\}";
		String replacement = "[$1]";
		String expectedResult = "[$1]";
	}

	private TestData testData = new TestData();

	@Test
	public void test() throws Exception {
		ByteArrayInputStream tInputStream = new ByteArrayInputStream(testData.text.getBytes(testData.encoding));

		classUnderTest = new TextLineFilterInputStream(tInputStream, testData.encoding, testData.regExp,
				testData.replacement);

		ByteArrayOutputStream tOutputStream = new ByteArrayOutputStream();
		byte[] tBuffer = new byte[256];

		int tRead;
		while ((tRead = classUnderTest.read(tBuffer)) != -1) {
			tOutputStream.write(tBuffer, 0, tRead);
		}

		CharBuffer tCharBuffer = Charset.forName(testData.encoding)
				.decode(ByteBuffer.wrap(tOutputStream.toByteArray()));
		String tResult = tCharBuffer.toString();

		System.out.println(testData.expectedResult);
		System.out.println(tResult);

		assertEquals(testData.expectedResult, tResult);
	}
}
