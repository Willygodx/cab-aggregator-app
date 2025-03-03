package by.modsen.ratingservice.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "html:target/cucumber-report.html"},
    features = {"src/test/resources/features"},
    glue = {"by.modsen.ratingservice.e2e"}
)
public class E2ERunnerTest {
}
