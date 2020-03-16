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
    public static Model convertCsv2rdf() throws IOException {
        // opening the csv file

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String pathToUFO = absolutePath + "\\ontologygenerator\\dataset\\UFOSightings.csv";
        // System.out.println(lunarFile.getAbsolutePath());

        BufferedReader fileReader = new BufferedReader(new FileReader(pathToUFO));
        String csv_row = fileReader.readLine();

        Model model = ModelFactory.createDefaultModel();

        csv_row = fileReader.readLine();

        while (csv_row != null) {

            String[] csv_row_cells = csv_row.split(",");
            model = UFOSightingsBaseModel(csv_row_cells, model);

            csv_row = fileReader.readLine();

        }

        fileReader.close();

        // // reading the column names
        // for (int i = 0; i < column_names.length; i++) {
        // System.out.print(column_names[i] + ", ");
        // }

        return model;
        // System.out.println("Lunar File Exists...Now Reading its content");
    }

    public static Model UFOSightingsBaseModel(String[] csv_row_cells, Model model) {

        String owlNamespace = "http://www.w3.org/2002/07/owl#";
        String georssNamespace = "http://www.georss.org/georss";
        String timeNamespace = "http://www.w3.org/2006/time#";

        Resource ufoSighting = model.createResource("http://webprotege.stanford.edu/RDC5CoBvmuQr9JdXNzJV4i7");

        /*------------------------------------------- [Calendar] -----------------------------------------*/

        Resource unixTime = model.createResource("http://webprotege.stanford.edu/RLb9bHBRcMLFB7QEpD98at");
        try {
            SimpleDateFormat format = new SimpleDateFormat("mm/dd/yyyy HH:mm");
            Date date = format.parse(csv_row_cells[0]);
            long timestamp = date.getTime();
            String timeString = String.valueOf(timestamp);
            Literal csv_dateTime = model.createLiteral(timeString);
            Property isUnix = model.createProperty("http://webprotege.stanford.edu/Rxdi8o7Xanct1IYtOlmXSN", "isUnixTime");
            unixTime.addLiteral(isUnix, csv_dateTime);
        } catch (ParseException e) {
            System.out.println("date formating issue");
        }

        String[] splitDate = csv_row_cells[0].split("[/ ]");

        Resource year = model.createResource("http://webprotege.stanford.edu/RxcPrs8R3VZ7UvWYmlrQfD");
        Literal yyyy = model.createLiteral(splitDate[2]);
        Property owlYr = model.createProperty(owlNamespace, "year");
        year.addLiteral(owlYr, yyyy);

        Resource month = model.createResource("http://webprotege.stanford.edu/R7rlF1W41T2W05qZrCxDIVf");
        Literal mm = model.createLiteral(splitDate[0]);
        Property owlMonth = model.createProperty(owlNamespace, "month");
        month.addLiteral(owlMonth, mm);

        Resource day = model.createResource("http://webprotege.stanford.edu/R8JQhygzEiESrtOg3pcIdch");
        Literal dd = model.createLiteral(splitDate[1]);
        Property owlDate = model.createProperty(owlNamespace, "day");
        day.addLiteral(owlDate, dd);
        /*------------------------------------------- [City] -----------------------------------------*/

        Resource city = model.createResource("http://webprotege.stanford.edu/RDJCPCGgVJVlCiyzOzUjS4E");
        Literal cityValue = model.createLiteral(csv_row_cells[1]);
        Property isCity = model.createProperty("http://webprotege.stanford.edu/R8r0qhFeupftK4LvNe8Som2",
                "isCity");
        city.addLiteral(isCity, cityValue);

        /*-----------------------------=------------- [State] -----------------------------------------*/
        Resource state = model.createResource("http://webprotege.stanford.edu/RljympFXFnyhHIw17jE469");
        Literal stateValue = model.createLiteral(csv_row_cells[2]);
        Property isState = model.createProperty("http://webprotege.stanford.edu/R80x80hnUcJVfcjlkpWNCjR",
                "isState");
        state.addLiteral(isState, stateValue);

        /*---------------------------------------------[country]-----------------------------------------*/
        Resource country = model.createResource("http://webprotege.stanford.edu/RBwNmRM4ifVFMBEx5999bL2");
        Literal countryValue = model.createLiteral(csv_row_cells[3]);
        Property isCountry = model.createProperty("http://webprotege.stanford.edu/R7I0Zs44j5iNqiZanHWj4LO",
                "isCountry");
        country.addLiteral(isCountry, countryValue);

        /*-------------------------------------------[shape] ----------------------------------------*/
        Resource shape = model.createResource("http://webprotege.stanford.edu/Roh0bGZCc7KH6aBLbSIBc9");
        Literal shapeValue = model.createLiteral(csv_row_cells[4]);
        Property isShape = model.createProperty("http://webprotege.stanford.edu/RDuWn2yU6K8ZUaFC1Lr3FLp",
                "isShape");
        shape.addLiteral(isShape, shapeValue);

        /*-------------------------------------------[duration] ----------------------------------------*/
        Resource duration = model.createResource("http://webprotege.stanford.edu/RCgDCFSzX5cherJc9ypaCEX");
        Literal durationValue = model.createLiteral(csv_row_cells[5]);
        Property timeDuration = model.createProperty(timeNamespace, "duration");
        duration.addLiteral(timeDuration, durationValue);

        /*--------------------------------------------[Geolocation ] ----------------------------------------*/

        // find uri for the geolocation , latitude, and longitude in georss ontology
        Property geoPoint = model.createProperty(georssNamespace, "point");
        Property geoLat = model.createProperty(georssNamespace, "lat");
        Property geoLong = model.createProperty(georssNamespace, "long");
        // create geolocation node and make the instance of
        Resource geoLocation = model.createResource(geoPoint);

        // create latitude node and points it to the latitude literal value
        Resource latitude = model.createResource(geoLat); // creates the node for the latitude
        Literal latValue = model.createLiteral(csv_row_cells[6]); // prepares the literal value that the node will
        Property isLatitude = model.createProperty("http://webprotege.stanford.edu/RBgyEpVqD0AV1ILL37Mm3QF",
                "isLatitude");
        latitude.addLiteral(isLatitude, latValue);

        // create longitude node and points it to the longitude literal value
        Resource longitude = model.createResource(geoLong);
        Literal longValue = model.createLiteral(csv_row_cells[7]);
        Property isLongitude = model.createProperty("http://webprotege.stanford.edu/RCuDcxjZyI5mrZERCOYhR6V",
                "isLongitude");
        longitude.addLiteral(isLongitude, longValue);

        Property hasLatitude = model.createProperty("http://webprotege.stanford.edu/R71035Ho9VoqMTn7bzIc21B",
        "hasLatitude");
        Property hasLongitude = model.createProperty("http://webprotege.stanford.edu/R9FbDFOr8bMgMHTcqF2Gxij",
        "hasLongitude");
        Property hasPoint = model.createProperty("http://www.opengis.net/gml", "Point");
        model.add(geoLocation, hasLongitude, longitude);
        model.add(geoLocation, hasLatitude, latitude);
        model.add(geoLocation, hasPoint, csv_row_cells[6] + " " + csv_row_cells[7]);

        /*--------------------------------------------[Model Statements] ----------------------------------------*/

        Property hasYear = model.createProperty("http://webprotege.stanford.edu/RDomYoSiLe5SCG2vckwHgFi", "hasYear");
        Property hasMonth = model.createProperty("http://webprotege.stanford.edu/RCZicQ5fitSZSerq917vqUU", "hasMonth");
        Property hasDay = model.createProperty("http://webprotege.stanford.edu/RCR0NsOaKxx92l3xhkVWKmv", "hasDay");

        model.add(unixTime, hasYear, year); //possibly change to date? and add date
        model.add(unixTime, hasMonth, month);
        model.add(unixTime, hasDay, day);

        Property hasCity = model.createProperty("http://webprotege.stanford.edu/RBr43uf7cKSbry5dczYyDb4",
                "hasCity");

        Property hasState = model.createProperty("http://webprotege.stanford.edu/RDA52ZAj360QKrqzJj7Pz9r",
                "hasState");

        Property hasCountry = model.createProperty("http://webprotege.stanford.edu/R8icTLtCv2HGmZ3IDtAOSTi",
                "hasCountry");
        
        Property hasShape = model.createProperty("http://webprotege.stanford.edu/R9EOOYMmMXN9I0pVswK2ZxM",
                "hasShape");

        Property hasDuration = model.createProperty("http://webprotege.stanford.edu/R8Zf1mo249URQ81utVpTE18",
                "hasDuration");

        Property hasGeolocation = model.createProperty("http://webprotege.stanford.edu/R7zoJhYOcFDOQk8Gn6eHIxC",
                "hasGeolocation");

        model.add(unixTime, hasCity, city);
        model.add(unixTime, hasState, state);
        model.add(unixTime, hasCountry, country);
        model.add(unixTime, hasShape, shape);
        model.add(unixTime, hasDuration, duration);
        model.add(unixTime, hasGeolocation, geoLocation);

        unixTime.addProperty(RDF.type, ufoSighting);

        return model;
    }
}