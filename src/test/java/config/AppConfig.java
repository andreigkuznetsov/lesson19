package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config/config.properties"
})
public interface AppConfig extends Config {

    @Key("webUrl")
    String webUrl();

    @Key("apiUrl")
    String apiUrl();

    @Key("userLogin")
    String userLogin();

    @Key("userPassword")
    String userPassword();

    @Key("apiReqUrl")
    String apiReqUrl();

    @Key("requestBody")
    String requestBody();

    @Key("name")
    String name();

    @Key("job")
    String job();

    @Key("newrequestBody")
    String newrequestBody();

    @Key("newJob")
    String newJob();

    @Key("anotherrequestBody")
    String anotherrequestBody();

    @Key("ehToken")
    String ehToken();

    @Key("ehEmail")
    String ehEmail();

    @Key("apiAbsUrl")
    String apiAbsUrl();

    @Key("absData")
    String absData();

    @Key("absNoData")
    String absNoData();
}
