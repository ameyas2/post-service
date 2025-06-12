package org.post;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Log4j2
@SpringBootApplication
public class PostInit {
    public static void main(String[] args) throws UnknownHostException {
        System.setProperty("hostName", InetAddress.getLocalHost().getHostName());
        System.setProperty("hostAddress", InetAddress.getLocalHost().getHostAddress());
        SpringApplication.run(PostInit.class, args);
    }
}