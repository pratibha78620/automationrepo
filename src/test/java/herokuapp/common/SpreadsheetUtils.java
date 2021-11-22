package herokuapp.common;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilities for dealing with CSV files.
 */
public class SpreadsheetUtils {
	/**
	 * Returns a CSV reader for the specified file.
	 *
	 * @param fileName The name of the file to get a CSV reader for. Must not be null.
	 * @return A CSV reader
	 */
	public static CSVReader getCsvReader(String fileName) {
		checkNotNull(fileName);

		FileReader reader;
		try {
			reader = new FileReader(fileName);
		} catch (FileNotFoundException fnfe) {
			throw new RuntimeException(fileName + " not found!");
		}
		return new CSVReader(reader);
	}

	/**
	 * Takes the name of a CSV file and returns it as a two-dimensional array of objects.
	 * Ignores the first row (useful in cases such as the first row being the column titles
	 *
	 * @param fileName The name of the CSV file. Must not be null.
	 * @return A two-dimensional array of Objects representing the CSV file data.
	 */
	public static Object[][] getCsvTestDataIgnoringFirstRow(String fileName) {
		checkNotNull(fileName);

		Object[][] data = null;
		try (CSVReader reader = getCsvReader(fileName)) {
			data = getCsvTestDataIgnoringFirstRow(reader);
		} catch (IOException e) {
			throw new RuntimeException("Error reading file " + fileName + ": " + e.getMessage());
		}
		return data;
	}

	/**
	 * Takes a CSV reader and returns the data as a two-dimensional Object array. Converts the nulls to empty strings,
	 * and ignores the first row (typically done as column titles). The width of each row will be the same width
	 * as the first row. Nulls in the CSV data will be converted to empty strings.
	 *
	 * @param csvReader The reader to get the data from. Must not be null.
	 * @return
	 */
	public static Object[][] getCsvTestDataIgnoringFirstRow(CSVReader csvReader) {
		checkNotNull(csvReader);

		List<String[]> lines;
		try {
			lines = csvReader.readAll();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}

        /* We only have lines.size() - 1 entries as the first line is the column title */
		int numRows = lines.size() - 1;
		int numColumns = lines.get(0).length;

		Object[][] data = new Object[numRows][numColumns];
		for (int i = 1; i <= numRows; i++) {
			String[] entry = lines.get(i);
			for (int j = 0; j < numColumns; j++) {
				Object o = entry[j];
				if (o == null) {
					data[i - 1][j] = "";
				} else {
					data[i - 1][j] = o.toString();
				}
			}
		}
		return data;
	}
}
