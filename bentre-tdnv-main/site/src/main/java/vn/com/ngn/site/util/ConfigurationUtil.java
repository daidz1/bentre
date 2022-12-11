package vn.com.ngn.site.util;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ConfigurationUtil implements EnvironmentAware {
    private static Environment env;

    public static String getProperty(String key){
        return env.getProperty(key);
    }

	@Override
	public void setEnvironment(Environment environment) {
		env = environment;
	}
}
