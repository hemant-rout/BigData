
package com.spark.fixedlength;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


public class FixedWidthRecordReader implements
		RecordReader<LongWritable, Text> {

	private static final int DEFAULT_BUFFER_SIZE = 64 * 1024;
	String charsetName = "UTF-8";
	String quote;
	StringBuilder stringBuilder;
	long start;
	long end;
	long pos;
	FileSystem fs;
	FSDataInputStream fileIn;
	private String[] lengthsAndDelimiters;
	final Path file;
	InputStreamReader inputStreamReader;
	char[] singleChar, multipleChars;
	boolean isQuotePresent = false;

	public FixedWidthRecordReader(JobConf conf, FileSplit split)
			throws IOException {
		lengthsAndDelimiters = FixedWidthHelper
				.modifyIdentifier(conf.get("lengthsAndDelimiters").split(","));
		quote = conf.get("quote");
		charsetName = conf.get("charsetName");
		start = split.getStart();
		pos = start;
		end = start + split.getLength();
		file = split.getPath();
		fs = file.getFileSystem(conf);
		fileIn = fs.open(split.getPath());
		fileIn.seek(start);
		inputStreamReader = new InputStreamReader(fileIn, charsetName);
		singleChar = new char[1];
		stringBuilder = new StringBuilder();
	}

	@Override
	public void close() throws IOException {
		inputStreamReader.close();
		fileIn.close();
	}

	@Override
	public LongWritable createKey() {
		return new LongWritable();
	}

	@Override
	public Text createValue() {
		return new Text("");
	}

	@Override
	public synchronized float getProgress() throws IOException {
		if (pos == end)
			return 0.0f;
		else {
			return Math.min(1.0f, (pos - start) / (float) (end - start));
		}
	}

	@Override
	public synchronized boolean next(LongWritable key, Text value)
			throws IOException, RuntimeException {
		boolean fieldNotFound, isMatchingDelimiterInProgress = false, isSecondLastCharNewline = false, isThirdLastCharNewline = false;
		boolean quoteCharFound = false;
		int fieldLength, delimiterCharCounter;
		stringBuilder.setLength(0);
		if (!isEOFEncountered() && !isSecondLastCharNewline
				&& !isThirdLastCharNewline) {
			for (int i = 0; i < lengthsAndDelimiters.length
					&& !isSecondLastCharNewline && !isThirdLastCharNewline; i++) {
					fieldLength = Integer.parseInt(lengthsAndDelimiters[i]);
					if (!(pos + fieldLength > end)) {
						multipleChars = new char[fieldLength];
						inputStreamReader.read(multipleChars);
						pos += new String(multipleChars).getBytes(charsetName).length;
						stringBuilder.append(multipleChars);
					} else if ((isSecondLastChar() && isSecondLastCharNewline())
							|| (isThirdLastChar() && isThirdLastCharNewline())) {
						stringBuilder.setLength(0);
						isSecondLastCharNewline = true;
						isThirdLastCharNewline = true;
					} else {
						String message = "The input data is not according to specified schema. Expected data with delimiters or lengths as "
								+ Arrays.toString(lengthsAndDelimiters)
								+ ", got: " + stringBuilder.toString();
						throw new RuntimeException(message);
					}
			}
		} else {
			return false;
		}
		if (!isThirdLastCharNewline && !isSecondLastCharNewline) {
			value.set(stringBuilder.toString());
			return true;
		} else {
			return false;
		}

	}


	private boolean isThirdLastCharNewline() throws IOException {
		inputStreamReader.read(singleChar);
		pos += new String(singleChar).getBytes(charsetName).length;
		stringBuilder.append(singleChar);
		return stringBuilder.toString().contentEquals("\r");
	}

	private boolean isThirdLastChar() {
		return pos == end - 2;
	}

	private boolean isSecondLastCharNewline() throws IOException {
		inputStreamReader.read(singleChar);
		pos += new String(singleChar).getBytes(charsetName).length;
		stringBuilder.append(singleChar);
		return stringBuilder.toString().contentEquals("\n");
	}

	private boolean isSecondLastChar() {
		return pos == end - 1;
	}

	@Override
	public synchronized long getPos() throws IOException {
		return pos;
	}

	private boolean isEOFEncountered() {
		return pos >= end;
	}
}