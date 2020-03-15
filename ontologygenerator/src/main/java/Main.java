
import java.io.*;

import org.apache.jena.rdf.model.*;

public class Main {

    public static void main(String[] args) throws IOException {

        Model model = SolarEclipse.convertCsv2rdf();
        SolarEclipse.writeRDFOutputFile(model);
        /*
         * TO-DO: 
         *  1) CREATE OTHER MODELS
         *  2) IMPLEMENT OOD FOR CLEANER STRUCTURE 
         * 3) MAKE SURE OUR FORM FOR THE NODES ARE CORRECT THE
         * PROPERTY TYPES AND SO ON
         */

    } // end of main

}
