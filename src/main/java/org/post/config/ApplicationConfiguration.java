package org.post.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.post.http.UserServiceHTTP;
import org.posts.model.PostsEventInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ApplicationConfiguration {

    @Value("${external-server.hazelcast.host}")
    private String hazelcastHost;

    @Value("${external-server.hazelcast.port}")
    private String hazelcastPort;

    @Value("${external-server.hazelcast.cluster-name}")
    private String hazelcastClusterName;

    @Value("${external-server.user-service.host}")
    private String userServiceHost;

    @Bean
    public ClientConfig clientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName(hazelcastClusterName);
        clientConfig.getNetworkConfig().addAddress(hazelcastHost + ":" + hazelcastPort);
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
        return RestClient.builder().baseUrl(userServiceHost);
    }

    @Bean
    public KafkaTemplate<String, PostsEventInfo> kafkaTemplate(ProducerFactory<String, PostsEventInfo> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
