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

    // build meteorite model
    public static Model convertCsv2rdf() throws IOException {
        // opening the csv file

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String pathToSolar = absolutePath + "\\ontologygenerator\\dataset\\meteorites.csv";

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
    }

    public static Model MeteoriteBaseModel( String[] column_names, Model model, String[] csv_row_cells) {

        // String owlNamespace= "http://www.w3.org/2002/07/owl#";
        // String xsdNamespace = "http://www.w3.org/2001/XMLSchema#";
        // String exNamespace = "http://example.org/time/";
        String georssNamespace = "http://www.georss.org/georss";

        Resource meteoriteResource = model.createResource("https://data.nasa.gov/Space-Science/Meteorite-Landings/gh4g-9sfh/data");

        /*------------------------------------------- [Name] -----------------------------------------*/

        //TO-DO: This should actually be in <Description rdf:about=name>

        Resource meteorite = model.createResource();
        Property hasName = model.createProperty("http://webprotege.stanford.edu/R8IqsOPT1nR9XAQSrFuh2vl", "hasName");
//        Literal literalName = model.createLiteral(csv_row_cells[0]);
        meteorite.addLiteral(hasName, csv_row_cells[0]);
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
        Property weighs = model.createProperty("http://webprotege.stanford.edu/R7WItf8cFKHXAgXpcn9qAII", "weighs");
//        Literal literalMass = model.createLiteral(csv_row_cells[2]);
        mass.addLiteral(weighs, csv_row_cells[2]);

        /*-------------------------------------------[Fell or Found] ----------------------------------------*/
        Resource fellFound = model.createResource();
        Property wasFoundOrFell = model.createProperty("http://webprotege.stanford.edu/RN4zvRqrViPXwGVRK0RC4F", "wasFoundOrFell");
//        Literal literalFell = model.createLiteral(csv_row_cells[3]);
        fellFound.addLiteral(wasFoundOrFell, csv_row_cells[3]);

        /*--------------------------------------------[Geolocation ] ----------------------------------------*/

        // find uri for the geolocation , latitude, and longitude in georss ontology
        Property geoPoint = model.createProperty(georssNamespace, "point");
        Property geoLat = model.createProperty(georssNamespace, "lat");
        Property geoLong = model.createProperty(georssNamespace, "long");

        // create geolocation node and make the instance of
        Resource geoLocation = model.createResource(geoPoint);

        // create latitude node and points it to the latitude literal value
        Resource latitude = model.createResource(geoLat);
        Literal latiValue = model.createLiteral(csv_row_cells[5]);
        Property isLatitude = model.createProperty("http://webprotege.stanford.edu/RBgyEpVqD0AV1ILL37Mm3QF",
                "isLatitude");
        latitude.addLiteral(isLatitude, latiValue);

        // create longitude node and points it to the longitude literal value
        Resource longitude = model.createResource(geoLong);
        Literal longiValue = model.createLiteral(csv_row_cells[6]);
        Property isLongitude = model.createProperty("http://webprotege.stanford.edu/RCuDcxjZyI5mrZERCOYhR6V",
                "isLongitude");
        longitude.addLiteral(isLongitude, longiValue);

        /*--------------------------------------------[Model Statements] ---------------------------------------*/

        // adding thetogether Long and Lat as point and forms a statement
        Property hasLatitude = model.createProperty("http://webprotege.stanford.edu/R71035Ho9VoqMTn7bzIc21B",
                "hasLatitude");
        Property hasLongitude = model.createProperty("http://webprotege.stanford.edu/R9FbDFOr8bMgMHTcqF2Gxij",
                "hasLongitude");
        Property hasPoint = model.createProperty("http://www.opengis.net/gml", "Point");

        model.add(geoLocation, hasLongitude, longitude);
        model.add(geoLocation, hasLatitude, latitude);
        model.add(geoLocation, hasPoint, csv_row_cells[5] + " " + csv_row_cells[6]);


        /*--------------------------------------------[Model Statements] ----------------------------------------*/

        Property hasYear = model.createProperty("http://webprotege.stanford.edu/RDomYoSiLe5SCG2vckwHgFi", "hasYear");
        Property hasMass = model.createProperty("http://webprotege.stanford.edu/R839CxlOT9WSMg6HLCXhXuA", "hasMass");
        Property fellOrFound = model.createProperty("http://webprotege.stanford.edu/R9TqYzBYag5SoPJCuwz3TzX", "hasFellOrFound");
        Property hasGeolocation = model.createProperty("http://webprotege.stanford.edu/R7zoJhYOcFDOQk8Gn6eHIxC",
                "hasGeolocation");

        model.add(meteorite, hasYear, year);
        model.add(meteorite, RDF.predicate, recClass);
        model.add(meteorite, hasMass, mass);
        model.add(meteorite, fellOrFound, fellFound);
        model.add(meteorite, hasGeolocation, geoLocation);

        meteorite.addProperty(RDF.type, meteoriteResource);
        return model;
    }
}