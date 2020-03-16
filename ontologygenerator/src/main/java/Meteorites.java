import java.io.*;
import java.nio.file.Paths;


import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;


public class Meteorites {



    public static void writeRDFOutputFile(Model model) throws IOException {

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String pathToOutput = absolutePath + "\\ontologygenerator\\dataset\\output2.txt";

        File outputFile = new File(pathToOutput);
        if (outputFile.exists()) {
            outputFile.delete();
            FileOutputStream output = new FileOutputStream(outputFile);
            model.write(output);
        } else {
            outputFile.createNewFile();
            FileOutputStream output = new FileOutputStream(outputFile);
            model.write(output);
        }
    }

    // build solar model
    public static Model convertCsv2rdf() throws IOException {
        // opening the csv file

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String pathToSolar = absolutePath + "\\ontologygenerator\\dataset\\meteorites.csv";
        // System.out.println(lunarFile.getAbsolutePath());

        BufferedReader fileReader = new BufferedReader(new FileReader(pathToSolar));
        String csv_row = fileReader.readLine();
        String[] column_names = csv_row.split(",");

        Model model = ModelFactory.createDefaultModel();

        csv_row = fileReader.readLine();

        while (csv_row != null) {

            String[] csv_row_cells = csv_row.split(",");
            model = MeteoriteBaseModel(column_names, model, csv_row_cells);

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

    public static Model MeteoriteBaseModel( String[] column_names, Model model, String[] csv_row_cells) {

        // String owlNamespace= "http://www.w3.org/2002/07/owl#";
        // String xsdNamespace = "http://www.w3.org/2001/XMLSchema#";
        // String exNamespace = "http://example.org/time/";

        /*------------------------------------------- [Name] -----------------------------------------*/

        //TO-DO: This should actually be in <Description rdf:about=name>

        Resource meteorite = model.createResource();
        Literal literalName = model.createLiteral(csv_row_cells[0]);
        meteorite.addLiteral(RDF.subject, literalName);
        /*------------------------------------------- [Year] -----------------------------------------*/

        Resource year = model.createResource();
        Literal literalYear = model.createLiteral(csv_row_cells[4]);
        year.addLiteral(RDF.subject, literalYear);

        /*-----------------------------=------------- [Class] -----------------------------------------*/
        Resource recClass = model.createResource();
        Literal literalClass = model.createLiteral(csv_row_cells[1]);
        recClass.addLiteral(RDF.subject, literalClass);

        /*---------------------------------------------[Mass]-----------------------------------------*/
        Resource mass = model.createResource();
        Literal literalMass = model.createLiteral(csv_row_cells[2]);
        mass.addLiteral(RDF.subject, literalMass);

        /*-------------------------------------------[Fell or Found] ----------------------------------------*/
        Resource fellFound = model.createResource();
        Literal literalFell = model.createLiteral(csv_row_cells[3]);
        fellFound.addLiteral(RDF.subject, literalFell);

        /*--------------------------------------------[Geolocation ] ----------------------------------------*/

        // create geolocation node and make the instance of
        Resource geoLocation = model.createResource();

        // create latitude node and points it to the latitude literal value
        Resource latitude = model.createResource(); // creates the node for the latitude
        Literal latValue = model.createLiteral(csv_row_cells[5]); // prepares the literal value that the node will                                                           // point to
        latitude.addLiteral(RDF.subject, latValue); // assigns the literal value of the latitude
        latitude.addProperty(RDF.type, geoLocation); // makes the latitude of type Geolocation

        // create longitude node and points it to the longitude literal value
        Resource longitude = model.createResource();
        Literal longValue = model.createLiteral(csv_row_cells[6]);
        longitude.addLiteral(RDF.subject, longValue);
        longitude.addProperty(RDF.type, geoLocation);

        // creates the statement for the geolocation has latitude and longitude
        model.add(geoLocation, RDF.predicate, latitude);
        model.add(geoLocation, RDF.predicate, longitude);

        /*--------------------------------------------[Model Statements] ----------------------------------------*/

        model.add(meteorite, RDF.predicate, year);
        model.add(meteorite, RDF.predicate, recClass);
        model.add(meteorite, RDF.predicate, mass);
        model.add(meteorite, RDF.predicate, fellFound);
        model.add(meteorite, RDF.predicate, geoLocation);

        return model;
    }
}