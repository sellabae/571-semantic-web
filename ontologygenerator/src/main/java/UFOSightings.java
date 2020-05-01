import java.io.*;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
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

                return model;
        }

        public static Model UFOSightingsBaseModel(String[] csv_row_cells, Model model) {

                /*------------------------------------------- [ setting namespaces ] -----------------------------------------*/

                model.setNsPrefix("prt", "http://webprotege.stanford.edu/");
                model.setNsPrefix("time", "http://www.w3.org/2006/time#");
                model.setNsPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
                model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
                model.setNsPrefix("ogc", "http://www.opengis.net/gml/");

                String owlTimeNamespace = "http://www.w3.org/2006/time#";

                String xsdNamespace = "http://www.w3.org/2001/XMLSchema#";

                Resource ufoSightingClass = model.createResource("http://webprotege.stanford.edu/ufoSighting");

                Resource ufoSighting = model.createResource();

         

                /*------------------------------------------- [Calendar] -----------------------------------------*/

                try {
                        SimpleDateFormat format = new SimpleDateFormat("mm/dd/yyyy HH:mm");
                        Date date = format.parse(csv_row_cells[0]);
                        long timestamp = date.getTime();
                        String timeString = String.valueOf(timestamp);
                        Literal csv_dateTime = model.createLiteral(timeString);
                        Property hasUnix = model.createProperty("http://webprotege.stanford.edu/hasUnixTime");
                        ufoSighting.addLiteral(hasUnix, csv_dateTime);
                } catch (ParseException e) {

                }

                String[] splitDate = csv_row_cells[0].split("[/ ]");
                Literal yyyy;
                Literal mm;
                Literal dd;
                // String dateString;
                GregorianCalendar date = new GregorianCalendar();

                if (splitDate.length == 1) {
                        yyyy = model.createLiteral("");
                        mm = model.createLiteral("");
                        dd = model.createLiteral("");
                        // dateString = "yyyy/mm/dd";
                } else {
                        int year = new Integer(Integer.parseInt(splitDate[2]));
                        int month = new Integer(Integer.parseInt(splitDate[0]));
                        int day = new Integer(Integer.parseInt(splitDate[1]));
                        yyyy = model.createTypedLiteral(year);
                        mm = model.createTypedLiteral(month);
                        dd = model.createTypedLiteral(day);
                        date = new GregorianCalendar(year, month-1, day);
                }

                // Resource date = model.createResource();
                Literal recDate = model.createTypedLiteral(date);
                Property onDate = model.createProperty(xsdNamespace, "date");
                ufoSighting.addLiteral(onDate, recDate);

                // Resource year = model.createResource();
                Property owlYr = model.createProperty(owlTimeNamespace, "year");
                ufoSighting.addLiteral(owlYr, yyyy);

                // Resource month = model.createResource();
                Property owlMonth = model.createProperty(owlTimeNamespace, "month");
                ufoSighting.addLiteral(owlMonth, mm);

                // Resource day = model.createResource();
                Property owlDate = model.createProperty(owlTimeNamespace, "day");
                ufoSighting.addLiteral(owlDate, dd);
                /*------------------------------------------- [City] -----------------------------------------*/

                Literal cityValue = model.createLiteral(csv_row_cells[1]);
                Property inCity = model.createProperty("http://webprotege.stanford.edu/inCity");
                ufoSighting.addLiteral(inCity, cityValue);

                /*-----------------------------=------------- [State] -----------------------------------------*/

                Literal stateValue = model.createLiteral(csv_row_cells[2]);
                Property inState = model.createProperty("http://webprotege.stanford.edu/inState");
                ufoSighting.addLiteral(inState, stateValue);

                /*---------------------------------------------[ Country ]-----------------------------------------*/

                Literal countryValue = model.createLiteral(csv_row_cells[3]);
                Property inCountry = model.createProperty("http://webprotege.stanford.edu/inCountry");
                ufoSighting.addLiteral(inCountry, countryValue);

                /*-------------------------------------------[ Shape ] ----------------------------------------*/

                Literal shapeValue = model.createLiteral(csv_row_cells[4]);
                Property isShape = model.createProperty("http://webprotege.stanford.edu/isShape");
                ufoSighting.addLiteral(isShape, shapeValue);

                // shape.addProperty(RDF.type, shapeClass);

                /*-------------------------------------------[ Duration ] ----------------------------------------*/

                Literal durationValue = model.createTypedLiteral(new Double(Double.parseDouble(csv_row_cells[5])));
                Property timeDuration = model.createProperty(owlTimeNamespace, "duration");
                ufoSighting.addLiteral(timeDuration, durationValue);

                /*--------------------------------------------[Geolocation ] ----------------------------------------*/

                // // create latitude node and points it to the latitude literal value

                Literal latValue = model.createTypedLiteral(new Double(Double.parseDouble(csv_row_cells[6])));
                Property hasLatitude = model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
                ufoSighting.addLiteral(hasLatitude, latValue);

                // create longitude node and points it to the longitude literal value
                Literal longValue = model.createTypedLiteral(new Double(Double.parseDouble(csv_row_cells[7])));
                Property hasLongitude = model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");
                ufoSighting.addLiteral(hasLongitude, longValue);

                /*--------------------------------------------[ UFO Sightings ] ---------------------------------------*/

                
                Literal cmnt = model.createLiteral(csv_row_cells[9]);
                Property comment = model.createProperty("http://webprotege.stanford.edu/comment");

                ufoSighting.addLiteral(comment, cmnt);



                /*--------------------------------------------[Model Statements] ----------------------------------------*/

                Property hasPoint = model.createProperty("http://www.opengis.net/gml/Point");
                model.add(ufoSighting, hasPoint, csv_row_cells[6] + " " + csv_row_cells[7]);
                ufoSighting.addProperty(RDF.type, ufoSightingClass);
                return model;
        }
}