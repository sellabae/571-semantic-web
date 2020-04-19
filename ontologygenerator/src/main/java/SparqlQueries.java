import java.io.*;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.vocabulary.*;

public class SparqlQueries {

    public static void queryModel(String filepath) throws FileNotFoundException {

        // Open rdf file containing models
        InputStream rdfGraph = new FileInputStream(new File(filepath));

        // create an empty in-memory model and populate it from our graph
        Model model = ModelFactory.createMemModelMaker().createModel();
        model.read(rdfGraph, null); // null base URI, since model URIs are absolute
        rdfGraph.close();

        String prefixes = "PREFIX base: <http://webprotege.stanford.edu/> "
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> ";

        String select = "SELECT ?month ?october ";

        String patterns = "WHERE {?eclise base:month  ?month . ?month owl:month ?october }";
        // create a new query
        String queryString = prefixes + select + patterns;

        // create a new query from the QueryFactory
        Query query = QueryFactory.create(queryString);

        // Exectue the query and obtain results
        QueryExecution queryResults = QueryExecutionFactory.create(queryString, model);
        ResultSet results = queryResults.execSelect();

        // output query results
        ResultSetFormatter.out(System.out, results, query);
        // free up resources used in running the query

    }

}
