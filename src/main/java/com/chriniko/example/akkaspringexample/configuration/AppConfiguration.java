package com.chriniko.example.akkaspringexample.configuration;

import akka.actor.ActorSystem;
import com.chriniko.example.akkaspringexample.integration.akka.SpringAkkaExtension;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration

@ComponentScan(
        basePackages = {
                "com.chriniko.example.akkaspringexample"
        }
)

@Lazy

public class AppConfiguration {


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SpringAkkaExtension springAkkaExtension;


    @Bean
    public ActorSystem actorSystem() {

        ActorSystem system = ActorSystem.create("akka-crimes-processing-system", akkaConfiguration());

        springAkkaExtension.initialize(applicationContext);

        return system;
    }

    @Bean
    public Config akkaConfiguration() {
        return ConfigFactory.load();
    }
}
