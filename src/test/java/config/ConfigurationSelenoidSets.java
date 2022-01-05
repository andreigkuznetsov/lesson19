package config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config/configurationSelenoid.properties"})
public interface ConfigurationSelenoidSets extends Config {

    String remoteLogin();
    String remotePass();

}
