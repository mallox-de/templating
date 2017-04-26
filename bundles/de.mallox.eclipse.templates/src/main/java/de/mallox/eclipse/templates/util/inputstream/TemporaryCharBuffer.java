package de.mallox.eclipse.templates.util.inputstream;

import java.util.Arrays;

/**
 * {@link TemporaryCharBuffer} ist eine Hilfsklasse, mit der Zeichen f�r das
 * n�chste Lesen zur�ckgestellt werden k�nnen.
 */
public class TemporaryCharBuffer {

	/**
	 * Gr��e, mit der dieser {@link TemporaryCharBuffer} wachsen soll.
	 */
	private static final int GROW_SIZE = 60;

	/**
	 * Gr��e des genutzten Speichers.
	 */
	private int bufferLength;

	/**
	 * Speicher, in dem die Zeichen abgelegt werden.
	 */
	private char[] buffer = new char[0];

	/**
	 * Schreibt die angegebenen L�nge ab dem angegebenen Offset vom angegebenen
	 * Speicher in diesen {@link TemporaryCharBuffer}.
	 * <p>
	 * Es werden die neuen Zeichen an das Ende der schon vorhandenen Zeichen im
	 * {@link TemporaryCharBuffer} geschrieben.
	 * 
	 * @param pChars
	 *            Array, aus dem in diesen {@link TemporaryCharBuffer}
	 *            geschrieben werden soll.
	 * @param pOffset
	 *            Offset, ab dem aus dem angegebenen Speicher gelesen werden
	 *            soll.
	 * @param pLength
	 *            L�nge, des zu schreibenden Speichers.
	 */
	public void writeToBuffer(char[] pChars, int pOffset, int pLength) {
		/*
		 * Speicherbereich, wenn n�tig erweitern:
		 */
		if (pLength > buffer.length - bufferLength) {
			buffer = Arrays.copyOf(buffer, bufferLength + pLength);
		}

		/*
		 * Neue Zeichen an die vorhandenen anh�ngen:
		 */
		for (int i = 0; i < pLength; i++) {
			buffer[bufferLength + i] = pChars[pOffset + i];
		}

		/*
		 * L�nge korrigieren:
		 */
		bufferLength += pLength;
	}

	/**
	 * Schreibt ein Zeichen in diesen {@link TemporaryCharBuffer}.
	 * <p>
	 * Es werden die neuen Zeichen an das Ende der schon vorhandenen Zeichen im
	 * {@link TemporaryCharBuffer} geschrieben.
	 * 
	 * @param pChar
	 *            Zeichen, das geschrieben werden soll.
	 */
	public void writeToBuffer(char pChar) {
		/*
		 * Speicherbereich, wenn n�tig erweitern:
		 */
		if (1 > buffer.length - bufferLength) {
			buffer = Arrays.copyOf(buffer, bufferLength + GROW_SIZE);
		}

		/*
		 * Neue Zeichen an die vorhandenen anh�ngen:
		 */
		buffer[bufferLength] = pChar;

		/*
		 * L�nge korrigieren:
		 */
		bufferLength += 1;
	}

	/**
	 * Liest aus diesem {@link TemporaryCharBuffer} die angegebene Anzahl
	 * Zeichen und schreibt sie ab dem Offset in den �bergebenen Speicher.
	 * <p>
	 * Die gelesenen Zeichen werden aus dem Speicher entfernt.
	 * 
	 * @param pChars
	 *            Array, in den geschrieben werden soll.
	 * @param pOffset
	 *            Offset, ab dem geschrieben werden soll.
	 * @param pLength
	 *            Anzahl Zeichen, die gelesen werden sollen.
	 */
	public void readFromBuffer(char[] pChars, int pOffset, int pLength) {
		/*
		 * Zeichen in den �bergebenen Speicher-Bereich schreiben:
		 */
		for (int i = 0; i < pLength && i < bufferLength; i++) {
			pChars[pOffset + i] = buffer[i];
		}

		/*
		 * Noch verbleibende Zeichen an den Anfang des Speicher-Bereichs
		 * schieben:
		 */
		for (int i = Math.min(pLength, bufferLength); i < bufferLength; i++) {
			buffer[i - pLength] = buffer[i];
		}

		/*
		 * L�nge korrigieren:
		 */
		bufferLength -= pLength;

		if (bufferLength < 0) {
			bufferLength = 0;
		}
	}

	/**
	 * Liefert das n�chste Zeichen aus diesem {@link TemporaryCharBuffer} oder
	 * <code>-1</code>, solle es kein Zeichen mehr geben.
	 * 
	 * @return N�chstes Zeichen aus diesem {@link TemporaryCharBuffer} oder
	 *         <code>-1</code>.
	 */
	public int readCharFromBuffer() {
		if (bufferLength > 0) {
			int tChar = buffer[bufferLength - 1];
			bufferLength--;
			return tChar;
		}

		return -1;
	}

	/**
	 * Liefert den Buffer als String zur�ck und leert dabei den Speicher.
	 * 
	 * @return
	 */
	public String readStringFromBuffer() {
		String tCompleteBuffer = String.valueOf(buffer, 0, bufferLength);

		bufferLength = 0;

		return tCompleteBuffer;
	}

	/**
	 * Liefert die L�nge dieses {@link TemporaryCharBuffer}s zur�ck.
	 * 
	 * @return L�nge dieses {@link TemporaryCharBuffer}s.
	 */
	public int getLength() {
		return bufferLength;
	}
}
