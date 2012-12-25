package sim.stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import sim.main.Global;
import sim.stats.StatsObject.StatsPeriod;

public abstract class ResultWriter {
	private final static String ext = ".tab";

	public static void createDirectory(String name) throws Exception {
		// create output directories for this simulation run
		File folder = new File(name);

		// If the folder exists, bail out
		if (folder.exists()) {
			return;
		}

		// Now try to create it
		if (!folder.mkdir()) {
			// If it fails, actually check it failed!
			// Sometimes it is created by another thread first
			if (!folder.exists())
				throw new Exception("Could not create directory! (" + folder + ")");
		}

	}

	//TODO: macquire tidy this up
	/**
	 * This method appends the current results to the end of a tab file
	 */
	public static void appendResults(Map<String, StatsObject.StatsPeriod> data) throws Exception {
		Iterator<String> i = data.keySet().iterator();

		while(i.hasNext()) {
			// if there's only one stats period, only output the global result set
			String periodName;
			if (data.keySet().size() == 2) {
				i.next();
				i.next();
				periodName = StatsObject.GLOBAL;
			}
			else {
				periodName = i.next();
			}

			String fileName = Global.folder + periodName;

			File file = new File(fileName + ext);
			BufferedWriter out;

			Map<String,Object> results = data.get(periodName);

			if (file.exists()) {
				LineNumberReader in = new LineNumberReader(new FileReader(file));
				int valueCount = 0;

				String headerString = in.readLine();
				String[] headers = headerString.split("\t");
				while(in.readLine() != null) {valueCount++;}
				in.close();
				in = new LineNumberReader(new FileReader(file));

				String resultString = "";
				String padString = "";

				for(int ii=0;ii<headers.length;ii++) {
					if (results.containsKey(headers[ii])) {
						resultString += results.get(headers[ii]).toString() + "\t";
					}
					else {
						resultString += "0 \t";
					}
				}

				resultString = resultString.trim();

				// check to see if we have any values not currently included
				Iterator<String> j = results.keySet().iterator();

				while(j.hasNext()) {
					String currAttribute = j.next();

					boolean contained = false;

					for(int ii=0;ii<headers.length;ii++) {
						if (currAttribute.equals(headers[ii])) {
							contained = true;
							break;
						}
					}

					if (!contained) {
						headerString += "\t" + currAttribute;
						resultString += "\t" + results.get(currAttribute).toString();
						padString += "\t 0";
					}
				}

				/* write out new results file */
				File fileTemp = new File(fileName + "-temp" + ext);
				out = new BufferedWriter(new FileWriter(fileTemp,true));

				out.write(headerString + "\n");
				in.readLine();

				String currLine = "";

				while((currLine = in.readLine()) != null) {
					out.write(currLine + padString + "\n");
				}

				out.write(resultString + "\n");
				in.close();
				out.close();

				// swap old result file for completed new one
				file.delete();
				fileTemp.renameTo(new File(fileName + ext));
			}
			else {
				// write the first set of results
				out = new BufferedWriter(new FileWriter(file));

				Iterator<String> j = results.keySet().iterator();
				String headers = "";
				String values = "";
				while(j.hasNext()) {
					String key = j.next();
					headers += key + "\t";
					values += results.get(key) + "\t";
				}
				out.write(headers.trim() + "\n");
				out.write(values.trim() + "\n");

				out.close();
			}
		}
	}

	/**
	 * Writes the data into the filename as a tab delimited file
	 * If the file exists it gets overidden
	 * @param filename
	 * @param data
	 * @throws Exception
	 */
	public static void writeResults(String filename, Map<String, StatsObject.StatsPeriod> data) throws Exception {
		Iterator<Entry<String, StatsPeriod>> i = data.entrySet().iterator();

		// Loop for each stats period
		while(i.hasNext()) {

			Entry<String, StatsPeriod> e = i.next();
			StatsPeriod results = e.getValue();
			String periodName = e.getKey();

			String fileName = Global.folder + filename + '.' + periodName;

			// write the first set of results
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName + ext, false));

			Iterator<String> j = results.keySet().iterator();
			String headers = "";
			String values = "";
			while(j.hasNext()) {
				String key = j.next();
				headers += key + "\t";
				values += results.get(key) + "\t";
			}
			out.write(headers.trim() + "\n");
			out.write(values.trim() + "\n");

			out.close();

		}
	}

}
