
package com.spark.fixedlength;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
public class FixedWidthInputFormat extends
		FileInputFormat<LongWritable, Text> {

	@Override
	public RecordReader<LongWritable, Text> getRecordReader(
			InputSplit genericSplit, JobConf job, Reporter reporter)
			throws IOException {

		reporter.setStatus(genericSplit.toString());
		return new FixedWidthRecordReader(job,
				(FileSplit) genericSplit);
	}

	@Override
	protected boolean isSplitable(FileSystem fs, Path filename) {
		return false;
	}
}
