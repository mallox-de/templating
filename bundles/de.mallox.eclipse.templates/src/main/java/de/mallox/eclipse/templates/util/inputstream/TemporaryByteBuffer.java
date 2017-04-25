package de.mallox.eclipse.templates.util.inputstream;

import java.util.Arrays;

/**
 * {@link TemporaryByteBuffer} ist eine Hilfsklasse, mit der Zeichen f�r das
 * n�chste Lesen zur�ckgestellt werden k�nnen.
 */
public class TemporaryByteBuffer {

	/**
	 * Gr��e des genutzten Speichers.
	 */
	private int bufferLength;

	/**
	 * Speicher, in dem die Zeichen abgelegt werden.
	 */
	private byte[] buffer = new byte[0];

	/**
	 * Schreibt die angegebenen L�nge ab dem angegebenen Offset vom angegebenen
	 * Speicher in diesen {@link TemporaryByteBuffer}.
	 * <p>
	 * Es werden die neuen Zeichen an das Ende der schon vorhandenen Bytes im
	 * {@link TemporaryByteBuffer} geschrieben.
	 * 
	 * @param pBytes
	 *            Array, aus dem in diesen {@link TemporaryByteBuffer}
	 *            geschrieben werden soll.
	 * @param pOffset
	 *            Offset, ab dem aus dem angegebenen Speicher gelesen werden
	 *            soll.
	 * @param pLength
	 *            L�nge, des zu schreibenden Speichers.
	 */
	public void writeToBuffer(byte[] pBytes, int pOffset, int pLength) {
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
			buffer[bufferLength + i] = pBytes[pOffset + i];
		}

		/*
		 * L�nge korrigieren:
		 */
		bufferLength += pLength;
	}

	/**
	 * Liest aus diesem {@link TemporaryByteBuffer} die angegebene Anzahl
	 * Zeichen und schreibt sie ab dem Offset in den �bergebenen Speicher.
	 * <p>
	 * Die gelesenen Zeichen werden aus dem Speicher entfernt.
	 * 
	 * @param pBytes
	 *            Array, in den geschrieben werden soll.
	 * @param pOffset
	 *            Offset, ab dem geschrieben werden soll.
	 * @param pLength
	 *            Anzahl Zeichen, die gelesen werden sollen.
	 */
	public void readFromBuffer(byte[] pBytes, int pOffset, int pLength) {
		/*
		 * Zeichen in den �bergebenen Speicher-Bereich schreiben:
		 */
		for (int i = 0; i < pLength && i < bufferLength; i++) {
			pBytes[pOffset + i] = buffer[i];
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
	 * Liefert das n�chste Zeichen aus diesem {@link TemporaryByteBuffer} oder
	 * <code>-1</code>, solle es kein Zeichen mehr geben.
	 * 
	 * @return N�chstes Zeichen aus diesem {@link TemporaryByteBuffer} oder
	 *         <code>-1</code>.
	 */
	public int readByteFromBuffer() {
		if (bufferLength > 0) {
			int tByte = buffer[0];

			/*
			 * Noch verbleibende Zeichen an den Anfang des Speicher-Bereichs
			 * schieben:
			 */
			for (int i = 1; i < bufferLength; i++) {
				buffer[i - 1] = buffer[i];
			}

			/*
			 * L�nge korrigieren:
			 */
			bufferLength--;

			return tByte;
		}

		return -1;
	}

	/**
	 * Liefert die L�nge dieses {@link TemporaryByteBuffer}s zur�ck.
	 * 
	 * @return L�nge dieses {@link TemporaryByteBuffer}s.
	 */
	public int getLength() {
		return bufferLength;
	}
}
