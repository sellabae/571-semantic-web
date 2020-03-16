import java.io.*;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

public class UFOSightings {

    public static void writeRDFOutputFile(Model model) throws FileNotFoundException {

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String pathToOutput = absolutePath + "\\ontologygenerator\\dataset\\UFOoutput.txt";

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
    public static Model convertCsv2rdf() throws IOException{
        // opening the csv file

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String pathToUFO = absolutePath + "\\ontologygenerator\\dataset\\UFOSightings.csv";
        // System.out.println(lunarFile.getAbsolutePath());

        BufferedReader fileReader = new BufferedReader(new FileReader(pathToUFO));
        String csv_row = fileReader.readLine();
        String[] column_names = csv_row.split(",");

        Model model = ModelFactory.createDefaultModel();

        csv_row = fileReader.readLine();

        while (csv_row != null) {

            String[] csv_row_cells = csv_row.split(",");
            model = UFOSightingsBaseModel(csv_row_cells, model);

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

    public static Model UFOSightingsBaseModel(String[] csv_row_cells, Model model){

        String owlNamespace= "http://www.w3.org/2002/07/owl#";
        String xsdNamespace = "http://www.w3.org/2001/XMLSchema#";
        String exNamespace = "http://example.org/time/";
        /*------------------------------------------- [Calendar] -----------------------------------------*/

        Resource unixTime = model.createResource();
        try{
        SimpleDateFormat format = new SimpleDateFormat("mm/dd/yyyy HH:mm");
        Date date = format.parse(csv_row_cells[0]);
        long timestamp = date.getTime();
        String timeString = String.valueOf(timestamp);
        Literal csv_dateTime = model.createLiteral(timeString);
        unixTime.addLiteral(RDF.predicate, csv_dateTime);
        } catch(ParseException e){
          System.out.println("date formating issue");
        }

        String[] splitDate = csv_row_cells[0].split("[/ ]");

        Resource year = model.createResource();
        Literal yyyy = model.createLiteral(splitDate[2]);
        Property owlYr = model.createProperty(owlNamespace, "year");
        year.addLiteral(owlYr, yyyy);

        Resource month = model.createResource();
        Literal mm = model.createLiteral(splitDate[0]);
        Property owlMonth = model.createProperty(owlNamespace, "month");
        month.addLiteral(owlMonth, mm);

        Resource day = model.createResource();
        Literal dd = model.createLiteral(splitDate[1]);
        Property owlDate = model.createProperty(owlNamespace, "day");
        day.addLiteral(owlDate, dd);

        //A DATE ONLY HAS THREE OBJECTS AND THREE TYPES OF PROPERTIES(PREDICATES)
        // add day month and year saved as year month day
        unixTime.addProperty(RDF.predicate, day);
        unixTime.addProperty(RDF.predicate, month);
        unixTime.addProperty(RDF.predicate, year);


        /*------------------------------------------- [City] -----------------------------------------*/

        Resource city = model.createResource();
        Literal cityValue = model.createLiteral(csv_row_cells[1]);
        city.addLiteral(RDF.predicate, cityValue);

        /*-----------------------------=------------- [State] -----------------------------------------*/
        Resource state = model.createResource();
        Literal stateValue = model.createLiteral(csv_row_cells[2]);
        state.addLiteral(RDF.predicate, stateValue);

        /*---------------------------------------------[country]-----------------------------------------*/
        Resource country = model.createResource();
        Literal countryValue = model.createLiteral(csv_row_cells[3]);
        country.addLiteral(RDF.predicate, countryValue);

        /*-------------------------------------------[shape] ----------------------------------------*/
        Resource shape = model.createResource();
        Literal shapeValue = model.createLiteral(csv_row_cells[4]);
        shape.addLiteral(RDF.predicate, shapeValue);

        /*-------------------------------------------[duration] ----------------------------------------*/
        Resource duration = model.createResource();
        Literal durationValue = model.createLiteral(csv_row_cells[5]);
        duration.addLiteral(RDF.predicate, durationValue);

        /*--------------------------------------------[Geolocation ] ----------------------------------------*/

        // create geolocation node and make the instance of
        Resource geoLocation = model.createResource();

        // create latitude node and points it to the latitude literal value
        Resource latitude = model.createResource(); // creates the node for the latitude
        Literal lat_value = model.createLiteral(csv_row_cells[6]); // prepares the literal value that the node will
                                                                   // point to
        latitude.addLiteral(RDF.predicate, lat_value); // assigns the literal value of the latitude
        latitude.addProperty(RDF.type, geoLocation); // makes the latitude of type Geolocation

        // create longitude node and points it to the longitude literal value
        Resource longitude = model.createResource();
        Literal long_value = model.createLiteral(csv_row_cells[7]);
        longitude.addLiteral(RDF.predicate, long_value);
        longitude.addProperty(RDF.type, geoLocation);

        // creates the statement for the geolocation has latitude and longitude
        model.add(geoLocation, RDF.predicate, latitude);
        model.add(geoLocation, RDF.predicate, longitude);

        /*--------------------------------------------[Model Statements] ----------------------------------------*/

        model.add(unixTime, RDF.predicate, city);
        model.add(unixTime, RDF.predicate, state);
        model.add(unixTime, RDF.predicate, country);
        model.add(unixTime, RDF.predicate, shape);
        model.add(unixTime, RDF.predicate, duration);
        model.add(unixTime, RDF.predicate, geoLocation);

        return model;
    }
}