import java.io.*;
import java.nio.file.Paths;
import java.rmi.server.Skeleton;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

public class Meteorites {

        public static void writeRDFOutputFile(Model model) throws IOException {

                String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
                // String pathToOutput = absolutePath +
                // "\\ontologygenerator\\dataset\\MeteoriteOutput.rdf";
                String pathToOutput = absolutePath + "/ontologygenerator/rdf/MeteoriteOutput.rdf";

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
                        // System.out.println(
                        // "-----------------------------------------------------------------------------------------");
                        String[] csv_row_cells = csv_row.split(",");

                        int i = 0;
                        int j = 0;
                        String[] edited_cells = new String[csv_row_cells.length];
                        while (i < csv_row_cells.length) {
                                if (!csv_row_cells[i].equals("")) {
                                        if (csv_row_cells[i].charAt(0) == '"') {
                                                // System.out.println(csv_row_cells[i]);
                                                // System.out.println(csv_row_cells[i + 1]);

                                                csv_row_cells[i] = csv_row_cells[i] + csv_row_cells[i + 1];
                                                edited_cells[j] = csv_row_cells[i];
                                                i += 2;
                                        } else {
                                                edited_cells[j] = csv_row_cells[i];
                                                i += 1;

                                        }

                                } else {
                                        edited_cells[j] = "";
                                        i += 1;

                                }
                                j += 1;
                        }

                        // for (int k = 0; k < edited_cells.length; k++) {
                        // System.out.println(edited_cells[k]);
                        // }

                        model = MeteoriteBaseModel(column_names, model, edited_cells);

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

                /*------------------------------------------- [ setting namespaces ] -----------------------------------------*/

                model.setNsPrefix("prt", "http://webprotege.stanford.edu/");
                model.setNsPrefix("time", "http://www.w3.org/2006/time#");
                model.setNsPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
                model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
                model.setNsPrefix("ogc", "http://www.opengis.net/gml/");

                String owlTimeNamespace = "http://www.w3.org/2006/time#";

                Resource meteoriteResource = model.createResource("http://webprotege.stanford.edu/meteorite");
                Resource skyEvent = model.createResource("http://webprotege.stanford.edu/skyEvent");

                model.add(meteoriteResource, RDFS.subClassOf, skyEvent);


                /*------------------------------------------- [Name] -----------------------------------------*/

                // TO-DO: This should actually be in <Description rdf:about=name>

                Resource meteorite = model.createResource();
                Property hasName = model.createProperty("http://webprotege.stanford.edu/hasName");
                // Literal literalName = model.createLiteral(csv_row_cells[0]);
                meteorite.addLiteral(hasName, csv_row_cells[0]);
                /*------------------------------------------- [Year] -----------------------------------------*/

                if (!csv_row_cells[4].equals("")) {

                        Literal literalYear = model.createTypedLiteral(new Integer(Integer.parseInt(csv_row_cells[4])));
                        Property owlYear = model.createProperty(owlTimeNamespace, "year");
                        meteorite.addLiteral(owlYear, literalYear);
                }

                /*-----------------------------=------------- [Class] -----------------------------------------*/

                Literal literalClass = model.createLiteral(csv_row_cells[1]);
                Property hasClassType = model.createProperty("http://webprotege.stanford.edu/hasClassType");
                meteorite.addLiteral(hasClassType, literalClass);

                /*---------------------------------------------[Mass]-----------------------------------------*/

                Property weighs = model.createProperty("http://webprotege.stanford.edu/isWeight");

                if (!csv_row_cells[2].equals("")) {
                        Literal literalMass = model
                                        .createTypedLiteral(new Double(Double.parseDouble(csv_row_cells[2])));
                        meteorite.addLiteral(weighs, literalMass);
                }

                /*-------------------------------------------[Fell or Found] ----------------------------------------*/

                Property wasFoundOrFell = model.createProperty("http://webprotege.stanford.edu/wasFoundOrFell");
                Literal literalFell = model.createLiteral(csv_row_cells[3]);
                meteorite.addLiteral(wasFoundOrFell, literalFell);

                /*--------------------------------------------[Geolocation ] ----------------------------------------*/

                model.setNsPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");

                if (!csv_row_cells[5].equals("")) {
                        Literal latiValue = model.createTypedLiteral(new Double(Double.parseDouble(csv_row_cells[5])));

                        Property hasLatitude = model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");

                        meteorite.addLiteral(hasLatitude, latiValue);

                        Literal longiValue = model.createTypedLiteral(new Double(Double.parseDouble(csv_row_cells[6])));

                        Property hasLongitude = model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");

                        meteorite.addLiteral(hasLongitude, longiValue);

                }


                /*--------------------------------------------[Model Statements] ---------------------------------------*/

                Property hasPoint = model.createProperty("http://www.opengis.net/gml/Point");

                model.add(meteorite, hasPoint, csv_row_cells[5] + " " + csv_row_cells[6]);


                meteorite.addProperty(RDF.type, meteoriteResource);


                return model;
        }
}