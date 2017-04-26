package de.mallox.eclipse.templates.util.inputstream;

import java.util.Arrays;

/**
 * {@link TemporaryByteBuffer} ist eine Hilfsklasse, mit der Zeichen für das
 * nächste Lesen zurückgestellt werden können.
 */
public class TemporaryByteBuffer {

	/**
	 * Größe des genutzten Speichers.
	 */
	private int bufferLength;

	/**
	 * Speicher, in dem die Zeichen abgelegt werden.
	 */
	private byte[] buffer = new byte[0];

	/**
	 * Schreibt die angegebenen Länge ab dem angegebenen Offset vom angegebenen
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
	 *            Länge, des zu schreibenden Speichers.
	 */
	public void writeToBuffer(byte[] pBytes, int pOffset, int pLength) {
		/*
		 * Speicherbereich, wenn nötig erweitern:
		 */
		if (pLength > buffer.length - bufferLength) {
			buffer = Arrays.copyOf(buffer, bufferLength + pLength);
		}

		/*
		 * Neue Zeichen an die vorhandenen anhängen:
		 */
		for (int i = 0; i < pLength; i++) {
			buffer[bufferLength + i] = pBytes[pOffset + i];
		}

		/*
		 * Länge korrigieren:
		 */
		bufferLength += pLength;
	}

	/**
	 * Liest aus diesem {@link TemporaryByteBuffer} die angegebene Anzahl
	 * Zeichen und schreibt sie ab dem Offset in den übergebenen Speicher.
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
		 * Zeichen in den übergebenen Speicher-Bereich schreiben:
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
		 * Länge korrigieren:
		 */
		bufferLength -= pLength;

		if (bufferLength < 0) {
			bufferLength = 0;
		}
	}

	/**
	 * Liefert das nächste Zeichen aus diesem {@link TemporaryByteBuffer} oder
	 * <code>-1</code>, solle es kein Zeichen mehr geben.
	 * 
	 * @return Nächstes Zeichen aus diesem {@link TemporaryByteBuffer} oder
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
			 * Länge korrigieren:
			 */
			bufferLength--;

			return tByte;
		}

		return -1;
	}

	/**
	 * Liefert die Länge dieses {@link TemporaryByteBuffer}s zurück.
	 * 
	 * @return Länge dieses {@link TemporaryByteBuffer}s.
	 */
	public int getLength() {
		return bufferLength;
	}
}
