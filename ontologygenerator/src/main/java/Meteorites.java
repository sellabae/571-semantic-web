import java.io.*;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

public class Meteorites {

        public static void writeRDFOutputFile(Model model) throws IOException {

                String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
                // String pathToOutput = absolutePath +
                // "\\ontologygenerator\\dataset\\MeteoriteOutput.rdf";
                String pathToOutput = absolutePath + "/ontologygenerator/dataset/MeteoriteOutput.rdf";

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
                // String pathToSolar = absolutePath +
                // "\\ontologygenerator\\dataset\\meteorites.csv";
                String pathToSolar = absolutePath + "/ontologygenerator/dataset/meteorites.csv";

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

        public static Model MeteoriteBaseModel(String[] column_names, Model model, String[] csv_row_cells) {

                String owlNamespace = "http://www.w3.org/2002/07/owl#";
                // String xsdNamespace = "http://www.w3.org/2001/XMLSchema#";
                // String exNamespace = "http://example.org/time/";
                // String georssNamespace = "http://www.georss.org/georss";

                Resource meteoriteResource = model.createResource(
                                "https://data.nasa.gov/Space-Science/Meteorite-Landings/gh4g-9sfh/data");
                /*------------------------------------------- [Year] -----------------------------------------*/

                /*------------------------------------------- [Name] -----------------------------------------*/

                // TO-DO: This should actually be in <Description rdf:about=name>

                Resource meteorite = model.createResource();
                Property hasName = model.createProperty("http://webprotege.stanford.edu/hasName");
                // Literal literalName = model.createLiteral(csv_row_cells[0]);
                meteorite.addLiteral(hasName, csv_row_cells[0]);
                /*------------------------------------------- [Year] -----------------------------------------*/

                Resource yearClass = model.createResource("http://webprotege.stanford.edu/year");

                Resource year = model.createResource();
                Literal literalYear = model.createLiteral(csv_row_cells[4]);
                Property owlYear = model.createProperty(owlNamespace, "year");
                year.addLiteral(owlYear, literalYear);

                year.addProperty(RDF.type, yearClass);

                /*-----------------------------=------------- [Class] -----------------------------------------*/
                Resource classClass = model.createResource("http://webprotege.stanford.edu/classType");

                Resource recClass = model.createResource();
                Literal literalClass = model.createLiteral(csv_row_cells[1]);
                Property hasClassType = model.createProperty("http://webprotege.stanford.edu/hasClassType");
                recClass.addLiteral(hasClassType, literalClass);

                recClass.addProperty(RDF.type, classClass);

                /*---------------------------------------------[Mass]-----------------------------------------*/

                Resource massClass = model.createResource("http://webprotege.stanford.edu/mass");

                Resource mass = model.createResource();
                Property weighs = model.createProperty("http://webprotege.stanford.edu/isWeight");
                Literal literalMass = model.createLiteral(csv_row_cells[2]);
                mass.addLiteral(weighs, literalMass);

                mass.addProperty(RDF.type, massClass);

                /*-------------------------------------------[Fell or Found] ----------------------------------------*/
                Resource fellFoundClass = model.createResource();

                Resource fellFound = model.createResource();
                Property wasFoundOrFell = model.createProperty("http://webprotege.stanford.edu/wasFoundOrFell");
                Literal literalFell = model.createLiteral(csv_row_cells[3]);
                fellFound.addLiteral(wasFoundOrFell, literalFell);

                fellFound.addProperty(RDF.type, fellFoundClass);

                /*--------------------------------------------[Geolocation ] ----------------------------------------*/

                // classes
                Resource geoLocation = model.createResource("http://webprotege.stanford.edu/geolocation");
                Resource latitudeClass = model.createResource("http://webprotege.stanford.edu/latitude");
                Resource longitudeClass = model.createResource("http://webprotege.stanford.edu/longitude");

                // create geolocation node and make the instance of
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

                // instances
                point.addProperty(RDF.type, geoLocation);
                latitude.addProperty(RDF.type, latitudeClass);
                longitude.addProperty(RDF.type, longitudeClass);

                /*--------------------------------------------[Model Statements] ---------------------------------------*/

                // adding thetogether Long and Lat as point and forms a statement
                Property hasLatitude = model.createProperty("http://webprotege.stanford.edu/hasLatitude");
                Property hasLongitude = model.createProperty("http://webprotege.stanford.edu/hasLongitude");
                Property hasPoint = model.createProperty("http://www.opengis.net/gml", "Point");

                model.add(geoLocation, hasLongitude, longitude);
                model.add(geoLocation, hasLatitude, latitude);
                model.add(geoLocation, hasPoint, csv_row_cells[5] + " " + csv_row_cells[6]);

                /*--------------------------------------------[Model Statements] ----------------------------------------*/

                Property hasYear = model.createProperty("http://webprotege.stanford.edu/hasYear");
                Property hasMass = model.createProperty("http://webprotege.stanford.edu/hasMass");
                Property fellOrFound = model.createProperty("http://webprotege.stanford.edu/hasFellOrFound");
                Property hasGeolocation = model.createProperty("http://webprotege.stanford.edu/hasGeolocation");

                model.add(meteorite, hasYear, year);
                model.add(meteorite, RDF.predicate, recClass);
                model.add(meteorite, hasMass, mass);
                model.add(meteorite, fellOrFound, fellFound);
                model.add(meteorite, hasGeolocation, geoLocation);

                meteorite.addProperty(RDF.type, meteoriteResource);
                return model;
        }
}