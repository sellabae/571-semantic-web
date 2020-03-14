import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

public class Main {
    
    public static void main(String[] args) {

        //structure of the csv file ROW = SUBJECT   COLUMN NAME = CLASSES (for now )  CELL = object (literal or resource)
        Model model = basicStructure();
      
        //Writing RDF
        model.write(System.out);

    } // end of main

    public static Model basicStructure() {
        // Create an empty model for the CSV File
        Model model = ModelFactory.createDefaultModel();

        // Create an empty resource
        Resource test = model.createResource();

        // add property to relate subject and object where "Predicate is the object (in
        // this case a literal")
        test.addProperty(VCARD.N, "Left Side of Tree");

        // to create another empty resource that points to another resource
        test.addProperty(VCARD.N,
                model.createResource().addProperty(VCARD.N, "left branch").addProperty(VCARD.N, "right branch"));

        return model;
    }

}
