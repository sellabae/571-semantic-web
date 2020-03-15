import java.io.*;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

public class SolarEclipse {

    public static void writeRDFOutputFile(Model model) throws FileNotFoundException {

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String pathToOutput = absolutePath + "\\ontologygenerator\\dataset\\output.txt";

        File outputFile = new File(pathToOutput);

        if (outputFile.exists()) {

            outputFile.delete();
            FileOutputStream output = new FileOutputStream(outputFile);
            model.write(output);

        } else {
            FileOutputStream output = new FileOutputStream(outputFile);
            model.write(output);
        }
    }

    // build solar model
    public static Model convertCsv2rdf() throws IOException {
        // opening the csv file

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String pathToSolar = absolutePath + "\\ontologygenerator\\dataset\\solar.csv";
        // System.out.println(lunarFile.getAbsolutePath());

        BufferedReader fileReader = new BufferedReader(new FileReader(pathToSolar));
        String csv_row = fileReader.readLine();
        String[] column_names = csv_row.split(",");

        Model model = ModelFactory.createDefaultModel();

        csv_row = fileReader.readLine();

        while (csv_row != null) {

            String[] csv_row_cells = csv_row.split(",");
            model = solarEclipseBaseModel(column_names, model, csv_row_cells);

            csv_row = fileReader.readLine();

        }

        fileReader.close();

        // reading the column names
        // for (int i = 0; i < column_names.length; i++) {
        // System.out.print(column_names[i] + ", ");
        // }

        return model;
        // System.out.println("Lunar File Exists...Now Reading its content");
    }

    public static Model solarEclipseBaseModel(String[] column_names, Model model, String[] csv_row_cells) {

        String owlNamespace = "http://www.w3.org/2002/07/owl#";
        String xsdNamespace = "http://www.w3.org/2001/XMLSchema#";
        String exNamespace = "http://example.org/time/";

        // COMMENT[YASHUA]: don't know if we actually need this part now ???
        // Linking the resource to a solar eclipse

        Resource solarEclipse = model.createResource("https://eclipse.gsfc.nasa.gov/solar.html");

        /*------------------------------------------- [Catalog Number] -----------------------------------------*/

        Resource catalog_number = model.createResource();
        // Literal catal_num = model.createLiteral("00001");
        catalog_number.addLiteral(RDF.subject, csv_row_cells[0]);
        /*------------------------------------------- [Calendar Date] -----------------------------------------*/

        Resource date = model.createResource();

        Literal csv_date = model.createLiteral(csv_row_cells[1]);
        date.addLiteral(RDF.subject, csv_date);

        String[] splitDate = csv_row_cells[1].split(" ");

        // if format of date was saved differently -- instead of yyyy mm dd was saved as
        // yy-mm-dd
        if (splitDate.length == 1) {
            splitDate = csv_row_cells[1].split("-");
            // System.out.println(splitDate[0]);
        }

        Resource year = model.createResource();
        Literal yyyy = model.createLiteral(splitDate[0]);
        Property owlYr = model.createProperty(owlNamespace, "year");
        year.addLiteral(owlYr, yyyy);

        Resource month = model.createResource();
        Literal mm = model.createLiteral(splitDate[1]);
        Property owlMonth = model.createProperty(owlNamespace, "month");
        month.addLiteral(owlMonth, mm);

        Resource day = model.createResource();
        Literal dd = model.createLiteral(splitDate[2]);
        Property owlDate = model.createProperty(owlNamespace, "day");
        day.addLiteral(owlDate, dd);

        // A DATE ONLY HAS THREE OBJECTS AND THREE TYPES OF PROPERTIES(PREDICATES)
        // add day month and year saved as year month day
        date.addProperty(RDF.predicate, day);
        date.addProperty(RDF.predicate, month);
        date.addProperty(RDF.predicate, year);

        /*-----------------------------=------------- [Recorded Time] -----------------------------------------*/
        Resource eclipse_time = model.createResource();

        Literal rec_time = model.createLiteral(csv_row_cells[2]);
        eclipse_time.addLiteral(RDF.subject, rec_time);

        /*---------------------------------------------[Eclipse Type]-----------------------------------------*/
        Resource eclipse_type = model.createResource();
        Literal ecl_type = model.createLiteral(csv_row_cells[3]);
        eclipse_type.addLiteral(RDF.subject, ecl_type);

        /*-------------------------------------------[Eclipse Magnitude] ----------------------------------------*/
        Resource eclipse_magnitude = model.createResource();
        Literal mag = model.createTypedLiteral(new Double(csv_row_cells[4]));
        eclipse_magnitude.addLiteral(RDF.subject, mag);

        /*--------------------------------------------[Geolocation ] ----------------------------------------*/

        // create geolocation node and make the instance of
        Resource geoLocation = model.createResource();

        // create latitude node and points it to the latitude literal value
        Resource latitude = model.createResource(); // creates the node for the latitude
        Literal lat_value = model.createLiteral(csv_row_cells[5]); // prepares the literal value that the node will //
                                                                   // point to
        latitude.addLiteral(RDF.subject, lat_value); // assigns the literal value of the latitude
        latitude.addProperty(RDF.type, geoLocation); // makes the latitude of type Geolocation

        // create longitude node and points it to the longitude literal value
        Resource longitude = model.createResource();
        Literal long_value = model.createLiteral(csv_row_cells[6]);
        longitude.addLiteral(RDF.subject, long_value);
        longitude.addProperty(RDF.type, geoLocation);

        // creates the statement for the geolocation has latitude and longitude
        model.add(geoLocation, RDF.predicate, latitude);
        model.add(geoLocation, RDF.predicate, longitude);

        /*--------------------------------------------[Model Statements] ----------------------------------------*/

        model.add(solarEclipse, RDF.predicate, catalog_number);
        model.add(catalog_number, RDF.predicate, date);
        model.add(catalog_number, RDF.predicate, eclipse_time);
        model.add(catalog_number, RDF.predicate, eclipse_type);
        model.add(catalog_number, RDF.predicate, eclipse_magnitude);
        model.add(catalog_number, RDF.predicate, geoLocation);

        return model;
    }
}