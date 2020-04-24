import java.io.*;
import java.nio.file.Paths;

import org.apache.jena.query.Query;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;

public class SparqlQueries {

    public static void queryModel() throws FileNotFoundException {

        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        // String pathToOutput = absolutePath +
        // "\\ontologygenerator\\dataset\\SolarOutput.rdf";
        String pathToOutput = absolutePath + "/ontologygenerator/rdf/SolarOutput.rdf";

        Model model = RDFDataMgr.loadModel(pathToOutput);

        String prefixes = "PREFIX base: <http://webprotege.stanford.edu/> PREFIX owl:<http://www.w3.org/2002/07/owl#> ";

        String select = "SELECT ?x ?catalogId ";

        String patterns = "WHERE { ?x base:hasName \"A\" . ?x owl:month 5 . ?x base:hasCatalogId ?catalogId}";
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
