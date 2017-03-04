package cucumber_jvm_etl_seed;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import cucumber_jvm_etl_seed.database.Metadata;
import cucumber_jvm_etl_seed.database.Query;
import cucumber_jvm_etl_seed.database.Records;
import cucumber_jvm_etl_seed.database.Utility;
import cucumber_jvm_etl_seed.shared.XMLParser;
import org.junit.Assert;
import org.w3c.dom.Document;


public class SeedSteps {

    @When("^I query the database for the \"([^\"]*)\" schema$")
    public void i_query_the_database_for_the_schema(String table_name) throws Throwable {
       List schema =  Metadata.getTableColumnMetadata(table_name);
       assertNotNull(schema);
       Hooks.setCucumberWorld(table_name, schema);
    }

    @Then("^the \"([^\"]*)\" schema should match the \"([^\"]*)\" schema$")
    public void the_schema_should_match_the_schema(String table_a, String table_b) throws Throwable {
        Map<String, Object> world = Hooks.getCucumberWorld();
        Object table_a_schema = world.get(table_a);
        Object table_b_schema = world.get(table_b);
        assertEquals(table_a_schema, table_b_schema);
    }

    @When("^I run \"([^\"]*)\" queries from file \"([^\"]*)\" against source table \"([^\"]*)\"$")
    public void i_run_queries_from_file_against_source_table(String queryType, String fileName, String sourceTable) throws Throwable {
        Document xmlDoc = XMLParser.parseFile("cucumber_jvm_etl_seed/test_data/"+fileName);
        List queries = Utility.getQueryStringsFromXml(queryType, sourceTable, xmlDoc);

        for (Object query: queries) {
            switch (queryType) {
                case "insert":
                    ResultSet insertResult = Query.insert(query.toString());
                    Assert.assertNotNull(insertResult);
                    Records.addQueryRecord(sourceTable, queryType, insertResult,null, null);
                    break;
                case "update":
                    List updateKeys = Query.updateReturnKeys(query.toString(), sourceTable);
                    Assert.assertTrue(updateKeys.size() > 0);
                    Records.addQueryRecord(sourceTable, queryType, null, null, updateKeys);
                    break;
                case "delete":
                    ResultSet deleteResult = Query.deleteReturnResultSet(query.toString(), sourceTable);
                    Assert.assertNotNull(deleteResult);
                    Records.addQueryRecord(sourceTable, queryType, deleteResult, null, null);
                    break;
            }
        }
    }

    @Then("^target table \"([^\"]*)\" should receive the \"([^\"]*)\" rows from source table \"([^\"]*)\"$")
    public void target_table_should_receive_the_rows_from_source_table(String targetTable, String queryType, String sourceTable) throws Throwable {
        List<Map<String, Object>> queryRecords = Records.getQueryRecords();
        List<ResultSet> sourceRows = Records.getRowsFromQueryRecords(sourceTable, queryType, queryRecords);
        List<List<Map<String, String>>> sourceRecords = Records.formatRows(sourceRows);
        List<Map<String, String>> mutualColumns = Metadata.getMutualTableColumns(sourceTable, targetTable);
        List<String> selectQueries = Utility.createSelectForTargetValidation(targetTable, sourceRecords, mutualColumns);

        for (String query: selectQueries) {
            ResultSet result = Query.select(query);
            String errMsg = String.format("\n The %s result was not found in target table %s. \n QUERY: \n\t %s", queryType, targetTable, query);
            Assert.assertTrue(errMsg, result.next());
        }
    }

    @Then("^target table \"([^\"]*)\" should not receive the deleted rows from source table \"([^\"]*)\"$")
    public void target_table_should_not_receive_the_deleted_rows_from_source_table(String targetTable, String sourceTable) throws Throwable {
        List<Map<String, Object>> queryRecords = Records.getQueryRecords();
        List<ResultSet> sourceRows = Records.getRowsFromQueryRecords(sourceTable, "delete", queryRecords);
        List<List<Map<String, String>>> sourceRecords = Records.formatRows(sourceRows);
        List<Map<String, String>> mutualColumns = Metadata.getMutualTableColumns(sourceTable, targetTable);
        List<String> selectQueries = Utility.createSelectForTargetValidation(targetTable, sourceRecords, mutualColumns);

        for (String query: selectQueries) {
            ResultSet result = Query.select(query);
            String errMsg = String.format("\n The delete row should not exist in target table %s. \n QUERY: \n\t %s", targetTable, query);
            Assert.assertFalse(errMsg, result.next());
        }
    }

}
