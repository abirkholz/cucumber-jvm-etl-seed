package cucumber_jvm_etl_seed;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "junit:target/cucumber-junit.xml", "html:target/cucumber-html"},
    tags = {"@pull_merge"}
)
public class RunCukesTest {
}
