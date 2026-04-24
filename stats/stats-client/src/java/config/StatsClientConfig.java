package java.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;

import java.StatsClient;

@Configuration
public class StatsClientConfig {
    @Autowired
    private Environment environment;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(environment.getProperty("stats.client.baseUrl"))
                .build();
    }

    @Bean
    public StatsClient statsClient(RestClient client) {
        return new StatsClient(client);
    }
}
