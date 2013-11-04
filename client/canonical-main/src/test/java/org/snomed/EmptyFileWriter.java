package org.snomed;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.junit.Test;

public class EmptyFileWriter {
	
	BufferedWriter writer = null;
	private String header = "id	effectiveTime	active	moduleId	refsetId	referencedComponentId	targetComponentId\r\n";
	private String outputFileName = "der2_cRefset_AssociationReferenceSpanishExtensionFull_INT_20131031.txt";
	private static StringBuffer buffer = null;
	

	@Test
	public void process() {
		try {
		//append the header to the buffer
			buffer = new StringBuffer();
			buffer.append(header);
			//buffer.append(System.getProperty("line.separator"));

		//write the contents of the buffer to the output file
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), "utf-8"));
			writer.write(buffer.toString());
			writer.flush();
			writer.close();
		
			
			//write the contents of the buffer to the output file
			//FileWriter fw = new FileWriter (outputFileName);
			//BufferedWriter bw = new BufferedWriter(fw);
			//bw.write(buffer.toString());
			//bw.flush();
			//bw.close();
			
			
			
		} catch (Exception e){
		// do something
		
		//} finally {
		// clean up
			//try {
				//writer.close();
			//} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			//}
		}
	
	}
}
