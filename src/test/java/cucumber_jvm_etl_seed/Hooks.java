package cucumber_jvm_etl_seed;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber_jvm_etl_seed.database.Connect;
import cucumber_jvm_etl_seed.database.Query;
import cucumber_jvm_etl_seed.database.Records;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class Hooks extends RunCukesTest {

    private static Map<String, Object> cucumberWorld = new HashMap<>();

    @Before(order=10)
    public void before(Scenario scenario) {
        cucumberWorld.put("scenario", scenario);
    }

    @After
    public void after(Scenario scenario) throws SQLException {
        // Clear scenario variables
        if (!cucumberWorld.isEmpty()){
            cucumberWorld.clear();
        }

        if (!Connect.getConnection().isClosed()){
            Connect.closeConnection();
        }

        if (!Records.getQueryRecords().isEmpty()){
            Records.clearQueryRecords();
        }
    }

    @Before("@pull_merge")
    public void beforePullMerge(Scenario scenario) throws SQLException {
        cucumberWorld.put("currentHook","@pull_merge");
        Connect.setConnection("mssql-docker", "demo");
        Query.truncateTable("BankRegFinlEOPFiling");
        Query.truncateTable("snlRegFilings");
        //For update
        Query.insert("INSERT INTO BankRegFinlEOPFiling VALUES ('16C13C75-05DC-4B48-8942-5A35A7999F3A',NEWID(),16104889,CURRENT_TIMESTAMP,'TEST_FILING','INSTN_FILING_TYPE',1,CURRENT_TIMESTAMP)");
        //For delete
        Query.insert("INSERT INTO BankRegFinlEOPFiling VALUES ('BE2A46E1-71A0-4BA8-B0D3-2CB24FBE5AD5',NEWID(),16104890,CURRENT_TIMESTAMP,'TEST_FILING','INSTN_FILING_TYPE',1,CURRENT_TIMESTAMP)");
        Connect.closeConnection();
    }

    @After("@pull_merge")
    public void afterPullMerge(Scenario scenario) {
        cucumberWorld.clear();
    }


    /* * * * * * * * * * * * * * * *  M A C H I N E R Y  * * * * * * * * * * * * * * * */
    public static void setCucumberWorld(String key, Object value) {
        if(cucumberWorld.containsKey(key)){
            cucumberWorld.remove(key);
        }
        cucumberWorld.put(key, value);
    }

    public static Map<String, Object> getCucumberWorld(){
        return cucumberWorld;
    }

}
