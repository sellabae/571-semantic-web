import java.io.*;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

public class SolarEclipse {

    public static void writeRDFOutputFile(Model model) throws IOException {

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String pathToOutput = absolutePath + "\\ontologygenerator\\dataset\\Solar.txt";

        File outputFile = new File(pathToOutput);

        if (outputFile.exists()) {

            outputFile.delete();
            FileOutputStream output = new FileOutputStream(outputFile);
            model.write(output, "RDF/XML-ABBREV");

        } else {
            outputFile.createNewFile();
            FileOutputStream output = new FileOutputStream(outputFile);
            model.write(output, "RDF/XML-ABBREV");
        }
    }

    // build solar model
    public static Model convertCsv2rdf() throws IOException {
        // opening the csv file

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        String pathToSolar = absolutePath + "\\ontologygenerator\\dataset\\solar.csv";
        // System.out.println(lunarFile.getAbsolutePath());

        BufferedReader fileReader = new BufferedReader(new FileReader(pathToSolar));
        
        //read column names
        String csvRow = fileReader.readLine();

        //initiate the model
        Model model = ModelFactory.createDefaultModel();

        //read first row of data
        csvRow = fileReader.readLine();

        while (csvRow != null) {

            String[] csv_row_cells = csvRow.split(",");
            model = solarEclipseBaseModel(model, csv_row_cells);
            csvRow = fileReader.readLine();

        }

        fileReader.close();

        // reading the column names
        // for (int i = 0; i < column_names.length; i++) {
        // System.out.print(column_names[i] + ", ");
        // }

        return model;
        // System.out.println("Lunar File Exists...Now Reading its content");
    }

    public static Model solarEclipseBaseModel(Model model, String[] csv_row_cells) {

        String owlNamespace = "http://www.w3.org/2002/07/owl#";
        String xsdNamespace = "http://www.w3.org/2001/XMLSchema#";
        String georssNamespace = "http://www.georss.org/georss";

        // COMMENT[YASHUA]: don't know if we actually need this part now ???
        // Linking the resource to a solar eclipse

        Resource solarEclipse = model.createResource("https://eclipse.gsfc.nasa.gov/solar.html");

        /*------------------------------------------- [Catalog Number] -----------------------------------------*/

        Resource catalogId = model.createResource();
        Property hasCatalogNumber = model.createProperty("http://webprotege.stanford.edu/RrD72JudN46BjQGGwNeWrN",
                "catalogId");
        catalogId.addLiteral(hasCatalogNumber, csv_row_cells[0]);

        /*------------------------------------------- [Calendar Date] -----------------------------------------*/

        Resource date = model.createResource();

        Literal rowDate = model.createLiteral(csv_row_cells[1]);
        date.addLiteral(RDF.subject, rowDate);

        String[] splitDate = csv_row_cells[1].split(" ");

        // if format of date was saved differently -- instead of yyyy mm dd was saved as
        // yy-mm-dd
        if (splitDate.length == 1) {
            splitDate = csv_row_cells[1].split("-");
            // System.out.println(splitDate[0]);
        }

        Resource year = model.createResource();
        Literal yyyy = model.createLiteral(splitDate[0]);
        Property owlYear = model.createProperty(owlNamespace, "year");
        year.addLiteral(owlYear, yyyy);

        Resource month = model.createResource();
        Literal mm = model.createLiteral(splitDate[1]);
        Property owlMonth = model.createProperty(owlNamespace, "month");
        month.addLiteral(owlMonth, mm);

        Resource day = model.createResource();
        Literal dd = model.createLiteral(splitDate[2]);
        Property owlDate = model.createProperty(owlNamespace, "day");
        day.addLiteral(owlDate, dd);

        /*-----------------------------=------------- [Recorded Time] -----------------------------------------*/

        Resource eclipseTime = model.createResource();
        Property dateTime = model.createProperty(xsdNamespace, "time");
        Literal recordedTime = model.createLiteral(csv_row_cells[2]);
        eclipseTime.addLiteral(dateTime, recordedTime);

        /*---------------------------------------------[Eclipse Type]--------------------------------------------*/

        Resource eclipseType = model.createResource();
        Literal eclType = model.createLiteral(csv_row_cells[3]);
        Property hasEclipseType = model.createProperty("http://webprotege.stanford.edu/R91zh9cMKECVIyrZWmzy4mi",
                "hasEclipseType");
        eclipseType.addLiteral(hasEclipseType, eclType);

        /*-------------------------------------------[Eclipse Magnitude] ----------------------------------------*/

        Resource eclipseMagnitude = model.createResource();
        Literal mag = model.createTypedLiteral(new Double(csv_row_cells[4]));
        Property hasMagnitude = model.createProperty("http://webprotege.stanford.edu/R94as1ZnfNsGxhvhqwQ8sd7",
                "isMagnitude");
        eclipseMagnitude.addLiteral(hasMagnitude, mag);

        /*--------------------------------------------[Geolocation ] --------------------------------------------*/

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

        // adding thetogether Long and Lat as point and forms a statement
        Property hasLatitude = model.createProperty("http://webprotege.stanford.edu/R71035Ho9VoqMTn7bzIc21B",
                "hasLatitude");
        Property hasLongitude = model.createProperty("http://webprotege.stanford.edu/R9FbDFOr8bMgMHTcqF2Gxij",
                "hasLongitude");
        Property hasPoint = model.createProperty("http://www.opengis.net/gml", "Point");
        model.add(geoLocation, hasLongitude, longitude);
        model.add(geoLocation, hasLatitude, latitude);
        model.add(geoLocation, hasPoint, csv_row_cells[5] + " " + csv_row_cells[6]);

        /*--------------------------------------------[Model Statements] ---------------------------------------*/

        // A DATE ONLY HAS THREE OBJECTS AND THREE TYPES OF PROPERTIES(PREDICATES)
        // add day month and year saved as year month day

        Property hasYear = model.createProperty("http://webprotege.stanford.edu/RDomYoSiLe5SCG2vckwHgFi", "hasYear");
        Property hasMonth = model.createProperty("http://webprotege.stanford.edu/RCZicQ5fitSZSerq917vqUU", "hasMonth");
        Property hasDay = model.createProperty("http://webprotege.stanford.edu/RCR0NsOaKxx92l3xhkVWKmv", "hasDay");

        model.add(date, hasYear, year);
        model.add(date, hasMonth, month);
        model.add(date, hasDay, day);

        Property hasDate = model.createProperty("http://webprotege.stanford.edu/R9Q4bA5QlW7PHvsliEATc9p", "hasDate");
        Property hasEclipseTime = model.createProperty("http://webprotege.stanford.edu/RC2h81iQbILTT8GtLrNywJc",
                "hasEclipseTime");
        Property hasSolarEclipseType = model.createProperty("http://webprotege.stanford.edu/RUyOgYZPk8f5yiseLEH9ig",
                "hasSolarEclipseType");
        Property hasEclipseMagnitude = model.createProperty("http://webprotege.stanford.edu/RDgtrF2YIO6DRoloobzIyY0",
                "hasEclispseMagnitude");
        Property hasGeolocation = model.createProperty("http://webprotege.stanford.edu/R7zoJhYOcFDOQk8Gn6eHIxC",
                "hasGeolocation");

        model.add(catalogId, hasDate, date);
        model.add(catalogId, hasEclipseTime, eclipseTime);
        model.add(catalogId, hasSolarEclipseType, eclipseType);
        model.add(catalogId, hasEclipseMagnitude, eclipseMagnitude);
        model.add(catalogId, hasGeolocation, geoLocation);

        catalogId.addProperty(RDF.type, solarEclipse);

        return model;
    }
}