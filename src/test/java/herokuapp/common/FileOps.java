package herokuapp.common;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Utilities to assist in dealing with files.
 */
public final class FileOps {
	private FileOps() {
		throw new UnsupportedOperationException("This class isn't supposed to be initialised!");
	}

	/**
	 * Takes a file and returns it as a parsed JSON object.
	 *
	 * @param file The file to parse into JSON.
	 * @return The parsed file as a JSON object.
	 * @throws RuntimeException when an IOException occurs.
	 */
	public static JsonObject fileToJson(File file) {
		JsonObject json;
		try (InputStream configFile = new FileInputStream(file)) {
			json = (JsonObject) new JsonParser().parse(IOUtils.toString(configFile));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return json;
	}

	/**
	 * @param path The path to check for files.
	 * @return An array of files in the specified filepath.
	 * @throws RuntimeException when there are no files available in the specified folder.
	 */
	public static File[] getListOfFiles(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		assertThat(path + " is not a directory/an IO error occurred", path, is(notNullValue()));

		if (listOfFiles.length < 1) {
			throw new RuntimeException("No test files available in " + path);
		}
		return listOfFiles;
	}

	/**
	 * @param filePath The name of the file to turn into a string.
	 * @return The file specified as a string.
 	 * @throws RuntimeException when an IOException occurs.
	 */
	public static String loadResource(String filePath) {
		File file = new File(filePath);

		try (InputStream workingFile = new FileInputStream(file)) {
			return IOUtils.toString(workingFile, Charsets.UTF_8);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}
	}

	/**
	 * @param file Takes a file and converts it to a string.
	 * @return The contents of the file as a string.
	 * @throws RuntimeException When an IOException occurs.
	 */
	public static String fileToString(File file) {
		try {
			return IOUtils.toString(new FileInputStream(file), "UTF-8");
		} catch (FileNotFoundException fnfe) {
			throw new RuntimeException("File not found exception: " + file.getAbsolutePath());
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}
	}

	/**
	 * method to convert PDF to byte array
	 * @return
     */
	public static byte[] convertFileToByteArray(String testFileName) {

		InputStream inputStream = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			inputStream = new FileInputStream(testFileName);
			byte[] buffer = new byte[1024];
			baos = new ByteArrayOutputStream();

			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return baos.toByteArray();
	}
}