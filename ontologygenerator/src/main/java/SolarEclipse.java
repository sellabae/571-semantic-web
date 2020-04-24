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
                String pathToOutput = absolutePath + "/ontologygenerator/rdf/SolarOutput.rdf";

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

                String owlTimeNamespace = "http://www.w3.org/2006/time#";
                String xsdNamespace = "http://www.w3.org/2001/XMLSchema#";

                Resource eclipse = model.createResource("http://webprotege.stanford.edu/eclipse");

                /*------------------------------------------- [ 1 Catalog Number] -----------------------------------------*/

                Resource catalogId = model.createResource();
                Property hasCatalogNumber = model.createProperty("http://webprotege.stanford.edu/hasCatalogId");

                Literal catId = model.createTypedLiteral(new Integer(Integer.parseInt(csv_row_cells[0])));
                catalogId.addLiteral(hasCatalogNumber, catId);

                /*------------------------------------------- [2 Calendar Date] -----------------------------------------*/

                Literal recDate = model.createLiteral(csv_row_cells[1]);
                Property onDate = model.createProperty("http://webprotege.stanford.edu/onDate");
                catalogId.addLiteral(onDate, recDate);

                String[] splitDate = csv_row_cells[1].split(" ");

                // if format of date was saved differently -- instead of yyyy mm dd was saved as
                // yy-mm-dd
                if (splitDate.length == 1) {
                        splitDate = csv_row_cells[1].split("-");
                        // System.out.println(splitDate[0]);
                }

                Literal yyyy = model.createTypedLiteral(new Integer(Integer.parseInt(splitDate[0])));
                Property owlYear = model.createProperty(owlTimeNamespace, "year");
                catalogId.addLiteral(owlYear, yyyy);

                int mon = MonthConverter.string2int(splitDate[1]);
                Literal mm = model.createTypedLiteral(new Integer(mon));
                Property owlMonth = model.createProperty(owlTimeNamespace, "month");
                catalogId.addLiteral(owlMonth, mm);

                Literal dd = model.createTypedLiteral(new Integer(Integer.parseInt(splitDate[2])));
                Property owlDate = model.createProperty(owlTimeNamespace, "day");
                catalogId.addLiteral(owlDate, dd);

                /*-----------------------------=------------- [3 Recorded Time] -----------------------------------------*/

                Property dateTime = model.createProperty(xsdNamespace, "time");
                Literal recordedTime = model.createLiteral(csv_row_cells[2]);
                catalogId.addLiteral(dateTime, recordedTime);

                /*---------------------------------------------[4 Eclipse Type]--------------------------------------------*/

                Resource eclipseSolarTypeClass = model.createResource("http://webprotege.stanford.edu/solarEclipse");

                Literal eclType = model.createLiteral(csv_row_cells[3]);
                Property hasEclipseType = model.createProperty("http://webprotege.stanford.edu/hasEclipseType");
                catalogId.addLiteral(hasEclipseType, eclType);

                catalogId.addProperty(RDF.type, eclipseSolarTypeClass);
                eclipseSolarTypeClass.addProperty(RDF.type, eclipse);

                /*-------------------------------------------[5 Eclipse Magnitude] ----------------------------------------*/

                Literal mag = model.createTypedLiteral(new Double(csv_row_cells[4]));
                Property hasMagnitude = model.createProperty("http://webprotege.stanford.edu/isMagnitude");
                catalogId.addLiteral(hasMagnitude, mag);

                /*--------------------------------------------[6 Geolocation ] --------------------------------------------*/

                Property hasLatitude = model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");

                // temporary string value that holds edit lat and long value (removed N S and
                // added negative positive)
                String value = csv_row_cells[5];

                // get last value
                char lastChar = value.charAt(value.length() - 1);

                // remove last value
                value = value.substring(0, value.length() - 1);

                // if the latitude is North then it is positive else it is negative
                int comparison = Character.compare(lastChar, 'N');
                if (comparison == 0) {
                        Literal latiValue = model.createTypedLiteral(new Double(Double.parseDouble(value)));
                        catalogId.addLiteral(hasLatitude, latiValue);
                } else {
                        Literal latiValue = model.createTypedLiteral(new Double(Double.parseDouble(value) * -1));
                        catalogId.addLiteral(hasLatitude, latiValue);
                }

                // longitude value now
                Property hasLongitude = model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");

                value = csv_row_cells[6];

                // get last value
                lastChar = value.charAt(value.length() - 1);

                // remove last value
                value = value.substring(0, value.length() - 1);

                // if the longitude is East then it is positive else it is negative
                comparison = Character.compare(lastChar, 'E');
                if (comparison == 0) {
                        Literal longiValue = model.createTypedLiteral(new Double(Double.parseDouble(value)));
                        catalogId.addLiteral(hasLongitude, longiValue);
                } else {
                        Literal longiValue = model.createTypedLiteral(new Double(Double.parseDouble(value) * -1));
                        catalogId.addLiteral(hasLongitude, longiValue);
                }

                /*--------------------------------------------[Model Statements] ---------------------------------------*/

                Property hasPoint = model.createProperty("http://www.opengis.net/gml/Point");

                model.add(catalogId, hasPoint, csv_row_cells[5] + " " + csv_row_cells[6]);

                return model;
        }
}