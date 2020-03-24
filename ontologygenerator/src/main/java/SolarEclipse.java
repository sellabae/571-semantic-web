import java.io.*;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.vocabulary.*;

public class SolarEclipse {

        public static void validateSolarModel(Model model) {
                InfModel infModel = ModelFactory.createRDFSModel(model);
                ValidityReport validity = infModel.validate();
                if (validity.isValid()) {
                        System.out.println("Model is Valid");
                } else {
                        System.out.println("INVALID");
                        for (Iterator<ValidityReport.Report> i = validity.getReports(); i.hasNext();) {
                                System.out.println(" - " + i.next());
                        }
                }

        }

        public static void writeRDFOutputFile(Model model) throws IOException {

                String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
                String pathToOutput = absolutePath + "\\ontologygenerator\\dataset\\SolarOutput.rdf";

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
                String pathToSolar = absolutePath + "\\ontologygenerator\\dataset\\solar.csv";
                // System.out.println(lunarFile.getAbsolutePath());

                BufferedReader fileReader = new BufferedReader(new FileReader(pathToSolar));

                // read column names
                String csvRow = fileReader.readLine();

                // initiate the model
                Model model = ModelFactory.createDefaultModel();

                // read first row of data
                csvRow = fileReader.readLine();

                while (csvRow != null) {

                        String[] csv_row_cells = csvRow.split(",");
                        model = solarEclipseBaseModel(model, csv_row_cells);
                        csvRow = fileReader.readLine();

                }

                fileReader.close();
                return model;
        }

        public static Model solarEclipseBaseModel(Model model, String[] csv_row_cells) {

                String owlNamespace = "http://www.w3.org/2002/07/owl#";
                String xsdNamespace = "http://www.w3.org/2001/XMLSchema#";
                // String georssNamespace = "http://www.georss.org/georss";

                // create a solar eclipse
                Resource eclipse = model.createResource("http://webprotege.stanford.edu/RNhcZbZWYvFtrxsJF2D3Am");

                /*------------------------------------------- [ 1 Catalog Number] -----------------------------------------*/

                Resource catalogId = model.createResource();
                Property hasCatalogNumber = model.createProperty(
                                "http://webprotege.stanford.edu/RrD72JudN46BjQGGwNeWrN", "hascatalogId");
                catalogId.addLiteral(hasCatalogNumber, csv_row_cells[0]);

                /*------------------------------------------- [2 Calendar Date] -----------------------------------------*/

                // Classes
                Resource recordDate = model.createResource("http://webprotege.stanford.edu/RCnxPgbCWfKs79Jc9Q9kIHp");
                Resource monthClass = model.createResource("http://webprotege.stanford.edu/R7rlF1W41T2W05qZrCxDIVf");
                Resource dayClass = model.createResource("http://webprotege.stanford.edu/R8JQhygzEiESrtOg3pcIdch");
                Resource yearClass = model.createResource("http://webprotege.stanford.edu/RxcPrs8R3VZ7UvWYmlrQfD");

                // instances
                Resource date = model.createResource();
                Literal recDate = model.createLiteral(csv_row_cells[1]);
                Property onDate = model.createProperty("http://webprotege.stanford.edu/RouMhc22fYnJnR3Ea4egoG",
                                "onDate");
                date.addLiteral(onDate, recDate);

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

                // connect instances to classes
                year.addProperty(RDF.type, yearClass);
                month.addProperty(RDF.type, monthClass);
                day.addProperty(RDF.type, dayClass);
                date.addProperty(RDF.type, recordDate);

                /*-----------------------------=------------- [3 Recorded Time] -----------------------------------------*/

                // classes
                Resource recordTime = model.createResource("http://webprotege.stanford.edu/RBLfargsiYe66oGM4Eah6Mg");

                // instance
                Resource eclipseTime = model.createResource();
                Property dateTime = model.createProperty(xsdNamespace, "time");
                Literal recordedTime = model.createLiteral(csv_row_cells[2]);
                eclipseTime.addLiteral(dateTime, recordedTime);

                // typing instance
                eclipseTime.addProperty(RDF.type, recordTime);

                /*---------------------------------------------[4 Eclipse Type]--------------------------------------------*/

                // eliipse type class
                Resource eclipseSolarTypeClass = model
                                .createResource("http://webprotege.stanford.edu/RDXWerHagclWqX4imfL8ENJ");

                // create instance resource
                Resource eclipseType = model.createResource();
                Literal eclType = model.createLiteral(csv_row_cells[3]);
                Property hasEclipseType = model.createProperty("http://webprotege.stanford.edu/R91zh9cMKECVIyrZWmzy4mi",
                                "hasEclipseType");
                eclipseType.addLiteral(hasEclipseType, eclType);

                // typing instance to eclipse type
                eclipseType.addProperty(RDF.type, eclipseSolarTypeClass);

                /*-------------------------------------------[5 Eclipse Magnitude] ----------------------------------------*/
                // class
                Resource magnitudeClass = model
                                .createResource("http://webprotege.stanford.edu/R89ucvdokGPbBhkWfr89i3z");

                // instance
                Resource eclipseMagnitude = model.createResource();
                Literal mag = model.createTypedLiteral(new Double(csv_row_cells[4]));
                Property hasMagnitude = model.createProperty("http://webprotege.stanford.edu/R94as1ZnfNsGxhvhqwQ8sd7",
                                "isMagnitude");
                eclipseMagnitude.addLiteral(hasMagnitude, mag);

                // typing instance
                eclipseMagnitude.addProperty(RDF.type, magnitudeClass);

                /*--------------------------------------------[6 Geolocation ] --------------------------------------------*/

                // classes
                Resource geoLocation = model.createResource("http://webprotege.stanford.edu/RC91qsDMoPbWXBQGsHAmbxT");
                Resource latitudeClass = model.createResource("http://webprotege.stanford.edu/RCQ2qqHZ6ujfTYOwOseU9SG");
                Resource longitudeClass = model
                                .createResource("http://webprotege.stanford.edu/R7TZdYUOfdOBINhOuHRI92j");

                // instance
                Resource point = model.createResource();

                // create latitude node and points it to the latitude literal value
                Resource latitude = model.createResource();
                Literal latiValue = model.createLiteral(csv_row_cells[5]);
                Property isLatitude = model.createProperty("http://webprotege.stanford.edu/RBgyEpVqD0AV1ILL37Mm3QF",
                                "isLatitude");
                latitude.addLiteral(isLatitude, latiValue);

                // create longitude node and points it to the longitude literal value
                Resource longitude = model.createResource();
                Literal longiValue = model.createLiteral(csv_row_cells[6]);
                Property isLongitude = model.createProperty("http://webprotege.stanford.edu/RCuDcxjZyI5mrZERCOYhR6V",
                                "isLongitude");
                longitude.addLiteral(isLongitude, longiValue);

                // typing instances
                point.addProperty(RDF.type, geoLocation);
                latitude.addProperty(RDF.type, latitudeClass);
                longitude.addProperty(RDF.type, longitudeClass);

                /*--------------------------------------------[Model Statements] ---------------------------------------*/

                // A DATE ONLY HAS THREE OBJECTS AND THREE TYPES OF PROPERTIES(PREDICATES)
                // add day month and year saved as year month day

                // adding thetogether Long and Lat as point and forms a statement
                Property hasLatitude = model.createProperty("http://webprotege.stanford.edu/R71035Ho9VoqMTn7bzIc21B",
                                "hasLatitude");
                Property hasLongitude = model.createProperty("http://webprotege.stanford.edu/R9FbDFOr8bMgMHTcqF2Gxij",
                                "hasLongitude");
                Property hasPoint = model.createProperty("http://www.opengis.net/gml", "Point");

                model.add(point, hasLongitude, longitude);
                model.add(point, hasLatitude, latitude);
                model.add(point, hasPoint, csv_row_cells[5] + " " + csv_row_cells[6]);

                Property hasYear = model.createProperty("http://webprotege.stanford.edu/RDomYoSiLe5SCG2vckwHgFi",
                                "hasYear");
                Property hasMonth = model.createProperty("http://webprotege.stanford.edu/RCZicQ5fitSZSerq917vqUU",
                                "hasMonth");
                Property hasDay = model.createProperty("http://webprotege.stanford.edu/RCR0NsOaKxx92l3xhkVWKmv",
                                "hasDay");

                model.add(date, hasYear, year);
                model.add(date, hasMonth, month);
                model.add(date, hasDay, day);

                Property hasDate = model.createProperty("http://webprotege.stanford.edu/R9Q4bA5QlW7PHvsliEATc9p",
                                "hasDate");
                Property hasEclipseTime = model.createProperty("http://webprotege.stanford.edu/RC2h81iQbILTT8GtLrNywJc",
                                "hasEclipseTime");
                Property hasSolarEclipseType = model.createProperty(
                                "http://webprotege.stanford.edu/RUyOgYZPk8f5yiseLEH9ig", "hasSolarEclipseType");
                Property hasEclipseMagnitude = model.createProperty(
                                "http://webprotege.stanford.edu/RDgtrF2YIO6DRoloobzIyY0", "hasEclispseMagnitude");
                Property hasGeolocation = model.createProperty("http://webprotege.stanford.edu/R7zoJhYOcFDOQk8Gn6eHIxC",
                                "hasGeolocation");

                model.add(catalogId, hasDate, date);
                model.add(catalogId, hasEclipseTime, eclipseTime);
                model.add(catalogId, hasSolarEclipseType, eclipseType);
                model.add(catalogId, hasEclipseMagnitude, eclipseMagnitude);
                model.add(catalogId, hasGeolocation, point);

                // this catalog row is an instance of a solar eclipe
                catalogId.addProperty(RDF.type, eclipse);

                return model;
        }
}