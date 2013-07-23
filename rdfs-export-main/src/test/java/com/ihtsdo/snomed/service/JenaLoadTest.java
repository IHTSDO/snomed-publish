package com.ihtsdo.snomed.service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.ihtsdo.snomed.client.rdfs.RdfsExportMain;

public class JenaLoadTest {

    private static final Logger LOG = LoggerFactory.getLogger( RdfsExportMain.class );

    //@Test
    public void shouldLoadAllTriples() throws FileNotFoundException, IOException{
        LOG.debug("Starting load test...");
        Stopwatch stopwatch = new Stopwatch().start();
        
        try(
//                BufferedInputStream bis = new BufferedInputStream(new FileInputStream("/tmp/results.rdfs"), 999999999);
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream("/tmp/results.rdfs"), 9999);
                InputStreamReader ir = new InputStreamReader(bis, "utf-8");
           )
        {
            Model model = ModelFactory.createDefaultModel(); 
            model.read(bis, null);
        }   
        
        stopwatch.stop();
        LOG.info("Program completion in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }
    
}
