//www.w3.org/2002/07/owl#
import java.io.*;

import org.apache.jena.rdf.model.*;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println(" IN SPARQL QUERIES ");

        MonthConverter.initializeHashMap();

        Model solarModel = SolarEclipse.convertCsv2rdf();
        SolarEclipse.writeRDFOutputFile(solarModel);
        // // SolarEclipse.validateSolarModel(solarModel);

        Model meteoriteModel = Meteorites.convertCsv2rdf();
        Meteorites.writeRDFOutputFile(meteoriteModel);

        Model ufoModel = UFOSightings.convertCsv2rdf();
        UFOSightings.writeRDFOutputFile(ufoModel);
        // // UFOSightings.validateUFOModel(ufoModel);

        Model lunarModel = LunarEclipse.convertCsv2rdf();
        LunarEclipse.writeRDFOutputFile(lunarModel);


    } // end of main

}
