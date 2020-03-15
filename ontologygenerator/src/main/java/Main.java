
import java.io.*;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

public class Main {

    public static void main(String[] args) throws IOException {

        // Writing RDF
        Model model = openCsvFile();
        // model = basicStructure();
        model.write(System.out);

    } // end of main

    // needs to receive the file path once having been verified the file does
    // exist....Parameter:string
    public static Model openCsvFile() throws IOException {
        // opening the csv file

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String pathToSolar = "\\ontologygenerator\\dataset\\solar.csv";
        String csvSolarPath = absolutePath + pathToSolar;
        // System.out.println(lunarFile.getAbsolutePath());

        BufferedReader fileReader = new BufferedReader(new FileReader(csvSolarPath));
        String csv_row = fileReader.readLine();
        String[] column_names = csv_row.split(",");

        Model model = ModelFactory.createDefaultModel();

        csv_row = fileReader.readLine();

        while (csv_row != null) {

            String[] csv_row_cells = csv_row.split(",");
            if (csv_row_cells.length > 0) {
                model = solarEclipseBaseModel(csv_row_cells, model);
            }
           
            csv_row = fileReader.readLine();

        }

        fileReader.close();

        // reading the column names
        for (int i = 0; i < column_names.length; i++) {
            System.out.print(column_names[i] + ", ");
        }

        return model;
        // System.out.println("Lunar File Exists...Now Reading its content");
    }

    public static Model solarEclipseBaseModel(String[] csv_row_cells, Model model) {

        Resource solarEclipse = model.createResource();
        Literal se_id = model.createLiteral(csv_row_cells[0] + "SE");
        solarEclipse.addLiteral(RDF.value, se_id);

        /*------------------------------------------- [Catalog Number] -----------------------------------------*/

        Resource catalog_number = model.createResource();
        // Literal catal_num = model.createLiteral("00001");
        catalog_number.addLiteral(RDF.predicate, csv_row_cells[0]);
        /*------------------------------------------- [Calendar Date] -----------------------------------------*/

        /*-----------------------------=------------- [Recorded Time] -----------------------------------------*/
        Resource eclipse_time = model.createResource();

        Literal rec_time = model.createLiteral(csv_row_cells[2]);
        eclipse_time.addLiteral(RDF.predicate, rec_time);

        /*---------------------------------------------[Eclipse Type]-----------------------------------------*/
        Resource eclipse_type = model.createResource();
        Literal ecl_type = model.createLiteral(csv_row_cells[3]);
        eclipse_type.addLiteral(RDF.predicate, ecl_type);

        /*-------------------------------------------[Eclipse Magnitude] ----------------------------------------*/
        Resource eclipse_magnitude = model.createResource();
        eclipse_magnitude.addLiteral(RDF.predicate, csv_row_cells[4]);

        /*--------------------------------------------[Geolocation ] ----------------------------------------*/

        // create geolocation node and make the instance of
        Resource geoLocation = model.createResource();

        // create latitude node and points it to the latitude literal value
        Resource latitude = model.createResource(); // creates the node for the latitude
        Literal lat_value = model.createLiteral(csv_row_cells[5]); // prepares the literal value that the node will
                                                                   // point to
        latitude.addLiteral(RDF.predicate, lat_value); // assigns the literal value of the latitude
        latitude.addProperty(RDF.type, geoLocation); // makes the latitude of type Geolocation

        // create longitude node and points it to the longitude literal value
        Resource longitude = model.createResource();
        Literal long_value = model.createLiteral(csv_row_cells[6]);
        longitude.addLiteral(RDF.predicate, long_value);
        longitude.addProperty(RDF.type, geoLocation);

        // creates the statement for the geolocation has latitude and longitude
        model.add(geoLocation, RDF.predicate, latitude);
        model.add(geoLocation, RDF.predicate, longitude);

        /*--------------------------------------------[Model Statements] ----------------------------------------*/

        model.add(solarEclipse, RDF.predicate, catalog_number);
        // NEED CALENDAR TIME
        model.add(solarEclipse, RDF.predicate, eclipse_time);
        model.add(solarEclipse, RDF.predicate, eclipse_type);
        model.add(solarEclipse, RDF.predicate, eclipse_magnitude);
        model.add(solarEclipse, RDF.predicate, geoLocation);

        return model;
    }

    public static Model basicStructure() {

        // Create an empty model for the CSV File
        Model model = ModelFactory.createDefaultModel();

        // Create an empty resource
        Resource test = model.createResource();

        // add property to relate subject and object where "Predicate is the object (in
        // this case a literal")

        test.addProperty(RDF.predicate, "predicate option goes here");

        // to create another empty resource that points to another resource
        test.addProperty(VCARD.N, model.createResource().addProperty(VCARD4.hasKey, "left branch ")
                .addProperty(VCARD4.hasKey, "right branch"));

        return model;
    }

}
