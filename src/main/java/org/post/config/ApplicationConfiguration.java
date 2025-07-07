package org.post.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.post.http.UserServiceHTTP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public ClientConfig clientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("hazel_cluster");
        clientConfig.getNetworkConfig().addAddress("192.168.29.206:5701");
        return clientConfig;
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        return HazelcastClient.newHazelcastClient(clientConfig());
    }

    @Bean
    public UserServiceHTTP userServiceHTTP() {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(userServiceRestClient().build())).build();
        return factory.createClient(UserServiceHTTP.class);
    }

    @Bean
    public RestClient.Builder userServiceRestClient() {
        return RestClient.builder().baseUrl("http://localhost:10002");
    }
}
