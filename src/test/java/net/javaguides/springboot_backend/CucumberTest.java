package net.javaguides.springboot_backend;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/test/resources/features",
		glue = "net.javaguides.springboot_backend.steps",
		plugin = {"pretty", "html:target/cucumber-report.html"},
		publish = false
)
public class CucumberTest {
}
