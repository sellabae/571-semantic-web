
import java.io.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

public class Main {

    public static void main(String[] args) throws IOException {

        // // structure of the csv file ROW = SUBJECT COLUMN NAME = CLASSES (for now )
        // CELL
        // // = object (literal or resource)
        // // Model model = basicStructure();

        // String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        // String pathToLunar = "\\ontologygenerator\\dataset\\lunar.csv";
        // String pathToSolar = "\\ontologygenerator\\dataset\\solar.csv";
        // String csvLunarFilepath = absolutePath + pathToLunar;
        // // System.out.println(lunarFile.getAbsolutePath());

        // BufferedReader fileReader = new BufferedReader(new
        // FileReader(csvLunarFilepath));
        // String csv_row = fileReader.readLine();
        // String[] column_names = csv_row.split(",");

        // while (csv_row != null) {

        // String[] csv_row_cells = csv_row.split(",");
        // if (csv_row_cells.length > 0) {
        // System.out.println(csv_row_cells[0]);
        // }

        // csv_row = fileReader.readLine();

        // }

        // fileReader.close();

        // // reading the column names
        // for (int i = 0; i < column_names.length; i++) {
        // System.out.print(column_names[i] + ", ");
        // }

        // // System.out.println("Lunar File Exists...Now Reading its content");

        // // opening the csv file

        // Writing RDF
        Model model = rdfModelStructure();
        model.write(System.out);

        rdfModelStructure();

    } // end of main

    public static Model rdfModelStructure() {

        // Create an empty model for the CSV File
        Model model = ModelFactory.createDefaultModel();


        //create main node
        Resource mainNode = model.createResource();

        mainNode.addProperty(RDF.subject, "Solar Eclipse");
        mainNode.addProperty(RDF.predicate, RDFS.isDefinedBy);


        // Create an empty resource
        Resource test = model.createResource();

        // add property to relate subject and object where "Predicate is the object (in
        // this case a literal")

        //Example of one node

        test.addProperty(RDF.subject, "00001A");
        test.addProperty(RDF.object, "00001");
        // create solar eclipse properties
        test.addProperty(RDF.predicate, "catalog number");
        test.addProperty(RDF.predicate, "calendar date");
        test.addProperty(RDF.predicate, "eclipse time");
        test.addProperty(RDF.predicate, "eclipse type");
        test.addProperty(RDF.predicate, "eclipse magnitude");
        test.addProperty(RDF.predicate, "eclipse latitude");
        test.addProperty(RDF.predicate, "eclipse longitude");

        mainNode.addProperty(RDF.object, test);


        return model;
    }

    public static Model basicStructure() {

        // Create an empty model for the CSV File
        Model model = ModelFactory.createDefaultModel();

        // Create an empty resource
        Resource test = model.createResource();

        // add property to relate subject and object where "Predicate is the object (in
        // this case a literal")

        test.addProperty(RDF.predicate, "predicate option goes here");

        // to create another empty resource that points to another resource
        test.addProperty(VCARD.N, model.createResource().addProperty(VCARD4.hasKey, "left branch ")
                .addProperty(VCARD4.hasKey, "right branch"));

        return model;
    }

}
