package org.service.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * The <code>FileUtil</code> class is read a file as string/ write a string to a
 * file.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */
public class FileUtil {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * Private constructor
	 */
	private FileUtil() {
		// Utility class.
	}

	/**
	 * Method to create all directory structure.
	 * 
	 * @param directoryString
	 *            - holds directory path as string.
	 */
	public static void createDirectories(String directoryString) {

		if (directoryString == null) {
			throw new IllegalArgumentException();
		}

		File directory = new File(directoryString);
		if (!directory.exists() && !directory.mkdirs()) {
			throw new RuntimeException(String.format("Couldn't create directory, %s ", directory));
		}

		LOGGER.debug("Directory '{}' created/already exist.", directory.getAbsolutePath());

	}

	/**
	 * Method to read a file as string.
	 * 
	 * @param file
	 *            - holds file reference to read.
	 * @return String - return file content as String reference.
	 */
	public static String readString(File file) {

		String string = StringUtils.EMPTY;
		if (file == null || file.isFile()) {
			try {
				string = FileUtils.readFileToString(file);
			} catch (IOException exception) {
				throw new RuntimeException("IOException" + exception);
			}
		} else {
			throw new IllegalArgumentException();
		}

		LOGGER.debug("File '{}' content read successfully.", file.getAbsolutePath());
		return string;
	}

	/**
	 * Method to write string to a file.
	 * 
	 * @param file
	 *            - holds file reference to write.
	 * @param content
	 *            -
	 */
	public static void writeString(File file, String content) {

		if (file == null || content == null) {
			throw new IllegalArgumentException();
		}

		try {
			FileUtils.writeStringToFile(file, content);
		} catch (IOException exception) {
			throw new RuntimeException("IOException" + exception);
		}

		LOGGER.debug("File '{}' wrote with content successfully.", file.getAbsolutePath());
	}

	/**
	 * Method to copy given file to given directory.
	 * 
	 * @param srcFile
	 *            - holds source file reference.
	 * @param destDir
	 *            - holds destination directory reference.
	 */
	public static void copyFileToDirectory(File srcFile, File destDir) {

		if (srcFile == null || destDir == null) {
			throw new IllegalArgumentException();
		}

		try {
			FileUtils.copyFileToDirectory(srcFile, destDir);
		} catch (IOException exception) {
			throw new RuntimeException("IOException" + exception);
		}

		LOGGER.debug("File '{}' copied successfully to '{}' directory.", srcFile.getAbsolutePath(),
				destDir.getAbsolutePath());

	}
	
	/**
	 * Method to copy given file to given directory.
	 * 
	 * @param srcFile
	 *            - holds source file reference.
	 * @param destDir
	 *            - holds destination directory reference.
	 */
	public static void copyUrlToFile(URL srcURL, File destDir) {

		if (srcURL == null || destDir == null) {
			throw new IllegalArgumentException();
		}

		try {
			FileUtils.copyURLToFile(srcURL, destDir);
		} catch (IOException exception) {
			throw new RuntimeException("IOException" + exception);
		}

		LOGGER.debug("File '{}' copied successfully to '{}' directory.", srcURL.getHost(),
				destDir.getAbsolutePath());

	}

	/**
	 * Method to copy given file to given directory.
	 * 
	 * @param srcFile
	 *            - holds source file reference.
	 * @param destFile
	 *            - holds destination file reference.
	 */
	public static void copyFile(File srcFile, File destFile) {

		if (srcFile == null || destFile == null) {
			throw new IllegalArgumentException();
		}

		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException exception) {
			throw new RuntimeException("IOException" + exception);
		}

		LOGGER.debug("File '{}' copied successfully to '{}' directory.", srcFile.getAbsolutePath(),
				destFile.getAbsolutePath());

	}

	/**
	 * Method to delete directory.
	 * 
	 * @param directory
	 *            - holds directory reference.
	 */
	public static void deleteDirectory(File directory) {

		if (directory == null) {
			throw new IllegalArgumentException();
		}

		try {
			FileUtils.deleteDirectory(directory);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}

		LOGGER.debug("Directory '{}' deleted successfully.", directory.getAbsolutePath());
	}

	public static File getResourceFile(String path) {

		File file = null;
		try {
			file = new ClassPathResource(path).getFile();
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}
		return file;
	}

	public static InputStream getResourceStream(String path) {

		InputStream file = null;
		try {
			file = new ClassPathResource(path).getInputStream();
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}
		return file;
	}

	public static void replaceLines(Path filePath, String replace, String replaceWith) {

		List<String> fileContent;
		try {
			fileContent = new ArrayList<>(Files.readAllLines(filePath, StandardCharsets.UTF_8));

			for (int i = 0; i < fileContent.size(); i++) {
				if (fileContent.get(i).trim().equals(replace)) {
					LOGGER.debug("Found match to replace"+replaceWith);
					fileContent.set(i, replaceWith);
					break;
				}
			}

			Files.write(filePath, fileContent, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
