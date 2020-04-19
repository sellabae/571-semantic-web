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
                // String pathToOutput = absolutePath +
                // "\\ontologygenerator\\dataset\\SolarOutput.rdf";
                String pathToOutput = absolutePath + "/ontologygenerator/dataset/SolarOutput.rdf";

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
                String pathToSolar = absolutePath + "/ontologygenerator/dataset/solar.csv";
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
                Resource eclipse = model.createResource("http://webprotege.stanford.edu/eclipse");

                /*------------------------------------------- [ 1 Catalog Number] -----------------------------------------*/

                Resource catalogId = model.createResource();
                Property hasCatalogNumber = model.createProperty("http://webprotege.stanford.edu/hascatalogId");
                catalogId.addLiteral(hasCatalogNumber, csv_row_cells[0]);

                /*------------------------------------------- [2 Calendar Date] -----------------------------------------*/

                // Classes
                Resource recordDate = model.createResource("http://webprotege.stanford.edu/recordedDate");
                Resource monthClass = model.createResource("http://webprotege.stanford.edu/month");
                Resource dayClass = model.createResource("http://webprotege.stanford.edu/day");
                Resource yearClass = model.createResource("http://webprotege.stanford.edu/year");

                // instances
                Resource date = model.createResource();
                Literal recDate = model.createLiteral(csv_row_cells[1]);
                Property onDate = model.createProperty("http://webprotege.stanford.edu/onDate");
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
                Resource recordTime = model.createResource("http://webprotege.stanford.edu/recordedTime");

                // instance
                Resource eclipseTime = model.createResource();
                Property dateTime = model.createProperty(xsdNamespace, "time");
                Literal recordedTime = model.createLiteral(csv_row_cells[2]);
                eclipseTime.addLiteral(dateTime, recordedTime);

                // typing instance
                eclipseTime.addProperty(RDF.type, recordTime);

                /*---------------------------------------------[4 Eclipse Type]--------------------------------------------*/

                // eliipse type class
                Resource eclipseSolarTypeClass = model.createResource("http://webprotege.stanford.edu/solarEclipse");

                // create instance resource
                Resource eclipseType = model.createResource();
                Literal eclType = model.createLiteral(csv_row_cells[3]);
                Property hasEclipseType = model.createProperty("http://webprotege.stanford.edu/hasEclipseType");
                eclipseType.addLiteral(hasEclipseType, eclType);

                // typing instance to eclipse type
                eclipseType.addProperty(RDF.type, eclipseSolarTypeClass);

                /*-------------------------------------------[5 Eclipse Magnitude] ----------------------------------------*/
                // class
                Resource magnitudeClass = model.createResource("http://webprotege.stanford.edu/magnitude");

                // instance
                Resource eclipseMagnitude = model.createResource();
                Literal mag = model.createTypedLiteral(new Double(csv_row_cells[4]));
                Property hasMagnitude = model.createProperty("http://webprotege.stanford.edu/isMagnitude");
                eclipseMagnitude.addLiteral(hasMagnitude, mag);

                // typing instance
                eclipseMagnitude.addProperty(RDF.type, magnitudeClass);

                /*--------------------------------------------[6 Geolocation ] --------------------------------------------*/

                // classes
                Resource geoLocation = model.createResource("http://webprotege.stanford.edu/geolocation");
                Resource latitudeClass = model.createResource("http://webprotege.stanford.edu/latitude");
                Resource longitudeClass = model.createResource("http://webprotege.stanford.edu/longitude");

                // instance
                Resource point = model.createResource();

                // create latitude node and points it to the latitude literal value
                Resource latitude = model.createResource();
                Literal latiValue = model.createLiteral(csv_row_cells[5]);
                Property isLatitude = model.createProperty("http://webprotege.stanford.edu/isLatitude");
                latitude.addLiteral(isLatitude, latiValue);

                // create longitude node and points it to the longitude literal value
                Resource longitude = model.createResource();
                Literal longiValue = model.createLiteral(csv_row_cells[6]);
                Property isLongitude = model.createProperty("http://webprotege.stanford.edu/isLongitude");
                longitude.addLiteral(isLongitude, longiValue);

                // typing instances
                point.addProperty(RDF.type, geoLocation);
                latitude.addProperty(RDF.type, latitudeClass);
                longitude.addProperty(RDF.type, longitudeClass);

                /*--------------------------------------------[Model Statements] ---------------------------------------*/

                // A DATE ONLY HAS THREE OBJECTS AND THREE TYPES OF PROPERTIES(PREDICATES)
                // add day month and year saved as year month day

                // adding thetogether Long and Lat as point and forms a statement
                Property hasLatitude = model.createProperty("http://webprotege.stanford.edu/hasLatitude");
                Property hasLongitude = model.createProperty("http://webprotege.stanford.edu/hasLongitude");
                Property hasPoint = model.createProperty("http://www.opengis.net/gml", "Point");

                model.add(point, hasLongitude, longitude);
                model.add(point, hasLatitude, latitude);
                model.add(point, hasPoint, csv_row_cells[5] + " " + csv_row_cells[6]);

                Property hasYear = model.createProperty("http://webprotege.stanford.edu/hasYear");
                Property hasMonth = model.createProperty("http://webprotege.stanford.edu/hasMonth");
                Property hasDay = model.createProperty("http://webprotege.stanford.edu/hasDay");

                model.add(date, hasYear, year);
                model.add(date, hasMonth, month);
                model.add(date, hasDay, day);

                Property hasDate = model.createProperty("http://webprotege.stanford.edu/hasDate");
                Property hasEclipseTime = model.createProperty("http://webprotege.stanford.edu/hasEclipseTime");
                Property hasSolarEclipseType = model
                                .createProperty("http://webprotege.stanford.edu/hasSolarEclipseType");
                Property hasEclipseMagnitude = model
                                .createProperty("http://webprotege.stanford.edu/hasEclispseMagnitude");
                Property hasGeolocation = model.createProperty("http://webprotege.stanford.edu/hasGeolocation");

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