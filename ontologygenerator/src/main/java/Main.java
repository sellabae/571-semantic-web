
import java.io.*;

import org.apache.jena.rdf.model.*;

public class Main {

    public static void main(String[] args) throws IOException {

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

        SparqlQueries.queryModel();

    } // end of main

}
