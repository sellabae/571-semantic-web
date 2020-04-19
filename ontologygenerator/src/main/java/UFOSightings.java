import java.io.*;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.vocabulary.*;

public class UFOSightings {

        public static void validateUFOModel(Model model) {
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

        public static void writeRDFOutputFile(Model model) throws FileNotFoundException {

                String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
                // String pathToUFO = absolutePath +
                // "\\ontologygenerator\\dataset\\UFOSightings.rdf";

                String pathToOutput = absolutePath + "/ontologygenerator/rdf/UFOoutput.rdf";

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
                // String pathToUFO = absolutePath +
                // "\\ontologygenerator\\dataset\\UFOSightings.csv";
                String pathToUFO = absolutePath + "/ontologygenerator/dataset/UFOSightings.csv";

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
                // String georssNamespace = "http://www.georss.org/georss";
                String timeNamespace = "http://www.w3.org/2006/time#";

                Resource ufoSighting = model.createResource("http://webprotege.stanford.edu/RDC5CoBvmuQr9JdXNzJV4i7");

                /*------------------------------------------- [Calendar] -----------------------------------------*/
                Resource unixClass = model.createResource("http://webprotege.stanford.edu/unixTime");
                Resource monthClass = model.createResource("http://webprotege.stanford.edu/month");
                Resource dayClass = model.createResource("http://webprotege.stanford.edu/day");
                Resource yearClass = model.createResource("http://webprotege.stanford.edu/year");
                Resource recordDate = model.createResource("http://webprotege.stanford.edu/recordedDate");

                Resource unixTime = model.createResource();
                try {
                        SimpleDateFormat format = new SimpleDateFormat("mm/dd/yyyy HH:mm");
                        Date date = format.parse(csv_row_cells[0]);
                        long timestamp = date.getTime();
                        String timeString = String.valueOf(timestamp);
                        Literal csv_dateTime = model.createLiteral(timeString);
                        Property isUnix = model.createProperty("http://webprotege.stanford.edu/isUnixTime");
                        unixTime.addLiteral(isUnix, csv_dateTime);
                } catch (ParseException e) {

                }

                String[] splitDate = csv_row_cells[0].split("[/ ]");
                Literal yyyy;
                Literal mm;
                Literal dd;
                String dateString;

                if (splitDate.length == 1) {
                        yyyy = model.createLiteral("");
                        mm = model.createLiteral("");
                        dd = model.createLiteral("");
                        dateString = "yyyy/mm/dd";
                } else {
                        yyyy = model.createLiteral(splitDate[2]);
                        mm = model.createLiteral(splitDate[0]);
                        dd = model.createLiteral(splitDate[1]);
                        dateString = splitDate[2] + "/" + splitDate[0] + "/" + splitDate[1];
                }

                Resource date = model.createResource();
                Literal recDate = model.createLiteral(dateString);
                Property onDate = model.createProperty("http://webprotege.stanford.edu/onDate");
                date.addLiteral(onDate, recDate);

                Resource year = model.createResource();
                Property owlYr = model.createProperty(owlNamespace, "year");
                year.addLiteral(owlYr, yyyy);

                Resource month = model.createResource();
                Property owlMonth = model.createProperty(owlNamespace, "month");
                month.addLiteral(owlMonth, mm);

                Resource day = model.createResource();
                Property owlDate = model.createProperty(owlNamespace, "day");
                day.addLiteral(owlDate, dd);

                year.addProperty(RDF.type, yearClass);
                month.addProperty(RDF.type, monthClass);
                day.addProperty(RDF.type, dayClass);
                unixTime.addProperty(RDF.type, unixClass);
                date.addProperty(RDF.type, recordDate);
                /*------------------------------------------- [City] -----------------------------------------*/

                Resource cityClass = model.createResource("http://webprotege.stanford.edu/city");
                Resource city = model.createResource();
                Literal cityValue = model.createLiteral(csv_row_cells[1]);
                Property isCity = model.createProperty("http://webprotege.stanford.edu/isCity");
                city.addLiteral(isCity, cityValue);

                city.addProperty(RDF.type, cityClass);

                /*-----------------------------=------------- [State] -----------------------------------------*/
                Resource stateClass = model.createResource("http://webprotege.stanford.edu/state");
                Resource state = model.createResource();
                Literal stateValue = model.createLiteral(csv_row_cells[2]);
                Property isState = model.createProperty("http://webprotege.stanford.edu/isState");
                state.addLiteral(isState, stateValue);

                state.addProperty(RDF.type, stateClass);
                /*---------------------------------------------[country]-----------------------------------------*/
                Resource countryClass = model.createResource("http://webprotege.stanford.edu/country");
                Resource country = model.createResource();
                Literal countryValue = model.createLiteral(csv_row_cells[3]);
                Property isCountry = model.createProperty("http://webprotege.stanford.edu/isCountry");
                country.addLiteral(isCountry, countryValue);

                country.addProperty(RDF.type, countryClass);
                /*-------------------------------------------[shape] ----------------------------------------*/
                Resource shapeClass = model.createResource("http://webprotege.stanford.edu/shape");
                Resource shape = model.createResource();
                Literal shapeValue = model.createLiteral(csv_row_cells[4]);
                Property isShape = model.createProperty("http://webprotege.stanford.edu/isShape");
                shape.addLiteral(isShape, shapeValue);

                shape.addProperty(RDF.type, shapeClass);

                /*-------------------------------------------[duration] ----------------------------------------*/
                Resource durationClass = model.createResource("http://webprotege.stanford.edu/duration");
                Resource duration = model.createResource();
                Literal durationValue = model.createLiteral(csv_row_cells[5]);
                Property timeDuration = model.createProperty(timeNamespace, "duration");
                duration.addLiteral(timeDuration, durationValue);

                duration.addProperty(RDF.type, durationClass);

                /*--------------------------------------------[Geolocation ] ----------------------------------------*/

                // classes
                Resource geoLocation = model.createResource("http://webprotege.stanford.edu/geolocation");
                Resource latitudeClass = model.createResource("http://webprotege.stanford.edu/latitude");
                Resource longitudeClass = model.createResource("http://webprotege.stanford.edu/longitude");
                // find uri for the geolocation , latitude, and longitude in georss ontology
                // Property geoPoint = model.createProperty(georssNamespace, "point");
                // Property geoLat = model.createProperty(georssNamespace, "lat");
                // Property geoLong = model.createProperty(georssNamespace, "long");
                // create geolocation node and make the instance of
                Resource point = model.createResource();

                // create latitude node and points it to the latitude literal value
                Resource latitude = model.createResource(); // creates the node for the latitude
                Literal latValue = model.createLiteral(csv_row_cells[6]); // prepares the literal value that the node
                                                                          // will
                Property isLatitude = model.createProperty("http://webprotege.stanford.edu/isLatitude");
                latitude.addLiteral(isLatitude, latValue);

                // create longitude node and points it to the longitude literal value
                Resource longitude = model.createResource();
                Literal longValue = model.createLiteral(csv_row_cells[7]);
                Property isLongitude = model.createProperty("http://webprotege.stanford.edu/isLongitude");
                longitude.addLiteral(isLongitude, longValue);

                point.addProperty(RDF.type, geoLocation);
                latitude.addProperty(RDF.type, latitudeClass);
                longitude.addProperty(RDF.type, longitudeClass);
                /*--------------------------------------------[Model Statements] ----------------------------------------*/

                Property hasLatitude = model.createProperty("http://webprotege.stanford.edu/hasLatitude");
                Property hasLongitude = model.createProperty("http://webprotege.stanford.edu/hasLongitude");
                Property hasPoint = model.createProperty("http://www.opengis.net/gml", "Point");

                model.add(point, hasLongitude, longitude);
                model.add(point, hasLatitude, latitude);
                model.add(point, hasPoint, csv_row_cells[6] + " " + csv_row_cells[7]);

                Property hasYear = model.createProperty("http://webprotege.stanford.edu/hasYear");
                Property hasMonth = model.createProperty("http://webprotege.stanford.edu/hasMonth");
                Property hasDay = model.createProperty("http://webprotege.stanford.edu/hasDay");
                Property hasDate = model.createProperty("http://webprotege.stanford.edu/hasDate");

                model.add(unixTime, hasYear, year); // possibly change to date? and add date
                model.add(unixTime, hasMonth, month);
                model.add(unixTime, hasDay, day);
                model.add(unixTime, hasDate, date);

                Property hasCity = model.createProperty("http://webprotege.stanford.edu/hasCity");

                Property hasState = model.createProperty("http://webprotege.stanford.edu/hasState");

                Property hasCountry = model.createProperty("http://webprotege.stanford.edu/hasCountry");

                Property hasShape = model.createProperty("http://webprotege.stanford.edu/hasShape");

                Property hasDuration = model.createProperty("http://webprotege.stanford.edu/hasDuration");

                Property hasGeolocation = model.createProperty("http://webprotege.stanford.edu/hasGeolocation");

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