package cucumber_jvm_etl_seed;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber_jvm_etl_seed.database.Connect;
import cucumber_jvm_etl_seed.database.Query;
import org.junit.Assert;

import java.sql.ResultSet;

public class CommonSteps {

    @Given("^a connection to environment \"([^\"]*)\" with database \"([^\"]*)\"$")
    public void a_connection_to_server_and_database(String environment, String db) throws Throwable {
         Connect.setConnection(environment, db);
         Assert.assertNotNull("Connection to database failed.", Connect.getConnection());
    }

    @When("^I execute the stored procedure \"([^\"]*)\"$")
    public void i_execute_the_stored_procedure(String procedure) throws Throwable {
        ResultSet result = Query.callStoredProcedure(procedure);
        Assert.assertNotNull(result);
    }
}
