package io.cip.services.cake.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "io.cip.services.cake.cucumber")
@ConfigurationParameter(key = "cucumber.publish.quiet", value = "true")
@ConfigurationParameter(
        key = "cucumber.plugin",
        value = "pretty, summary, timeline:target/reports/timeline, html:target/reports/cucumber.html"
)
@ConfigurationParameter(key = "cucumber.execution.parallel.config.strategy", value = "dynamic")
@ConfigurationParameter(key = "cucumber.execution.parallel.enable", value = "false")
public class RunCucumberTest {
}
