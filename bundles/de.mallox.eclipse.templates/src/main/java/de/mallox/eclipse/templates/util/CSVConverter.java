package de.mallox.eclipse.templates.util;

import java.io.IOException;
import java.io.StringReader;

import com.opencsv.CSVReader;

public class CSVConverter {
	public static String[] parseStringArray(String pStringArrayValue) {
		if (pStringArrayValue == null || pStringArrayValue.length() == 0 || pStringArrayValue.equals("[]")) {
			return new String[0];
		}
		int tStartIndex = pStringArrayValue.indexOf('[') + 1;
		int tEndIndex = pStringArrayValue.lastIndexOf(']');
		if (tStartIndex > -1 && tEndIndex > -1 && tStartIndex < tEndIndex & tEndIndex < pStringArrayValue.length()) {
			String tStringArrayValue = pStringArrayValue.substring(tStartIndex, tEndIndex);

			try (CSVReader tCsvReader = new CSVReader(new StringReader(tStringArrayValue))) {
				String[] tArray = tCsvReader.readNext();
				/*
				 * Trim all values:
				 */
				for (int i = 0; i < tArray.length; i++) {
					tArray[i] = tArray[i].trim();
				}
				return tArray;
			} catch (IOException e) {
				String tMessage = "could not parse stringarray '" + pStringArrayValue + "': " + e.getMessage();
				throw new RuntimeException(tMessage, e);
			}
		}
		String tMessage = "could not parse stringarray '" + pStringArrayValue + "'.";
		throw new RuntimeException(tMessage);
	}

	public static String convertToString(String[] pStringValues) {
		StringBuilder tCsvValue = new StringBuilder();
		if (pStringValues != null && pStringValues.length != 0) {
			for (String tValue : pStringValues) {
				if (tCsvValue.length() == 0) {
					tCsvValue.append("[");
				} else {
					tCsvValue.append(", ");
				}
				tCsvValue.append(tValue);
			}
			tCsvValue.append("]");
			tCsvValue.trimToSize();

		}
		return tCsvValue.toString();
	}
}
