
import java.io.*;

import org.apache.jena.rdf.model.*;

public class Main {

    public static void main(String[] args) throws IOException {

        Model solarModel = SolarEclipse.convertCsv2rdf();
        SolarEclipse.writeRDFOutputFile(solarModel);
        SolarEclipse.validateSolarModel(solarModel);

        Model meteoriteModel = Meteorites.convertCsv2rdf();
        Meteorites.writeRDFOutputFile(meteoriteModel);

        Model ufoModel = UFOSightings.convertCsv2rdf();
        UFOSightings.writeRDFOutputFile(ufoModel);
        /*
         * TO-DO: 
         *  1) CREATE OTHER MODELS
         *  2) IMPLEMENT OOD FOR CLEANER STRUCTURE 
         * 3) MAKE SURE OUR FORM FOR THE NODES ARE CORRECT THE
         * PROPERTY TYPES AND SO ON
         */

    } // end of main

}
