import java.io.*;
import java.nio.file.Paths;

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
                
                /*------------------------------------------- [Name] -----------------------------------------*/

                // TO-DO: This should actually be in <Description rdf:about=name>

                Resource meteorite = model.createResource();
                Property hasName = model.createProperty("http://webprotege.stanford.edu/hasName");
                // Literal literalName = model.createLiteral(csv_row_cells[0]);
                meteorite.addLiteral(hasName, csv_row_cells[0]);
                /*------------------------------------------- [Year] -----------------------------------------*/

                Literal literalYear = model.createTypedLiteral(new Integer(Integer.parseInt(csv_row_cells[4])));
                Property owlYear = model.createProperty(owlNamespace, "year");
                meteorite.addLiteral(owlYear, literalYear);


                /*-----------------------------=------------- [Class] -----------------------------------------*/
       
                Literal literalClass = model.createLiteral(csv_row_cells[1]);
                Property hasClassType = model.createProperty("http://webprotege.stanford.edu/hasClassType");
                meteorite.addLiteral(hasClassType, literalClass);


                /*---------------------------------------------[Mass]-----------------------------------------*/

             
                Property weighs = model.createProperty("http://webprotege.stanford.edu/isWeight");
                Literal literalMass = model.createTypedLiteral(new Double( Double.parseDouble(csv_row_cells[2])));
                meteorite.addLiteral(weighs, literalMass);


                /*-------------------------------------------[Fell or Found] ----------------------------------------*/
              
                Property wasFoundOrFell = model.createProperty("http://webprotege.stanford.edu/wasFoundOrFell");
                Literal literalFell = model.createLiteral(csv_row_cells[3]);
                meteorite.addLiteral(wasFoundOrFell, literalFell);


                /*--------------------------------------------[Geolocation ] ----------------------------------------*/

             
                Literal latiValue = model.createTypedLiteral(new Double(Double.parseDouble(csv_row_cells[5])));
       
                Property hasLatitude = model.createProperty("http://webprotege.stanford.edu/hasLatitude");

                meteorite.addLiteral(hasLatitude, latiValue);

       
                Literal longiValue = model.createTypedLiteral( new Double(Double.parseDouble(csv_row_cells[6])));
   
                Property hasLongitude = model.createProperty("http://webprotege.stanford.edu/hasLongitude");

                meteorite.addLiteral(hasLongitude, longiValue);



                /*--------------------------------------------[Model Statements] ---------------------------------------*/


                Property hasPoint = model.createProperty("http://www.opengis.net/gml", "Point");

                model.add(meteorite, hasPoint, csv_row_cells[5] + " " + csv_row_cells[6]);

                /*--------------------------------------------[Model Statements] ----------------------------------------*/


                meteorite.addProperty(RDF.type, meteoriteResource);
                return model;
        }
}