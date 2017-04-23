package de.mallox.eclipse.templates.util.inputstream;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.regex.Pattern;

public class TextLineFilterInputStream extends FilterInputStream {

	private final Pattern regexPattern;
	private final String replacement;

	private TemporaryCharBuffer lineBuffer = new TemporaryCharBuffer();
	private TemporaryByteBuffer outputBuffer = new TemporaryByteBuffer();
	private String charsetName;

	public TextLineFilterInputStream(InputStream pInputStream, String pCharsetName, String pRegex,
			String pReplacement) {
		super(pInputStream);
		charsetName = pCharsetName;
		regexPattern = Pattern.compile(pRegex);
		replacement = pReplacement;
	}

	private String readLine() throws IOException {
		int tCurrentChar;
		while ((tCurrentChar = in.read()) != -1) {
			if (tCurrentChar == '\n') {// Got NL, outa here.
				lineBuffer.writeToBuffer('\n');
				break;
			} else if (tCurrentChar == '\r') {
				// Got CR, is the next char NL ?
				int c2 = in.read();
				if (c2 == '\r') // discard extraneous CR
					c2 = in.read();
				if (c2 != '\n') {
					// If not NL, push it back
					if (!(in instanceof PushbackInputStream))
						in = this.in = new PushbackInputStream(in);
					((PushbackInputStream) in).unread(c2);
				}
				lineBuffer.writeToBuffer('\n');
				break; // outa here.
			}

			lineBuffer.writeToBuffer((char) tCurrentChar);
		}

		if (tCurrentChar == -1 && lineBuffer.getLength() == 0)
			return null;

		return lineBuffer.readStringFromBuffer();
	}

	private void fillOutputBufferWithReplacedNewLine() throws IOException {
		final String tLine = readLine();
		if (tLine != null) {
			final String tReplacedLine = regexPattern.matcher(tLine).replaceAll(replacement);
			byte[] tBytes = tReplacedLine.getBytes(charsetName);
			outputBuffer.writeToBuffer(tBytes, 0, tBytes.length);
		}
	}

	@Override
	public int read() throws IOException {
		if (outputBuffer.getLength() == 0) {
			fillOutputBufferWithReplacedNewLine();
		}

		return outputBuffer.readByteFromBuffer();
	}

	@Override
	public int read(byte[] pBytes, int pOffset, int pLength) throws IOException {
		if (outputBuffer.getLength() < pLength) {
			fillOutputBufferWithReplacedNewLine();
		}

		int tAvailableLength = outputBuffer.getLength();
		if (tAvailableLength == 0) {
			return -1;
		}

		outputBuffer.readFromBuffer(pBytes, pOffset, pLength);

		return tAvailableLength;
	}

	@Override
	public int read(byte[] pBytes) throws IOException {
		return read(pBytes, 0, pBytes.length);
	}

	@Override
	public int available() throws IOException {
		return outputBuffer.getLength();
	}

	@Override
	public void close() throws IOException {
		super.close();
	}

	@Override
	public long skip(long pN) throws IOException {
		throw new IllegalArgumentException("skip ist not supported");
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new IllegalArgumentException("mark/reset ist not supported");
	}

	@Override
	public synchronized void mark(int pReadlimit) {
		throw new IllegalArgumentException("mark ist not supported");
	}
}
