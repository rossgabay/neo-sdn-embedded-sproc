
package com.rgabay.embedded_sdn_sproc;

import com.rgabay.embedded_sdn_sproc.domain.Person;
import com.rgabay.embedded_sdn_sproc.repository.PersonRepository;
import org.neo4j.graphdb.GraphDatabaseService;

import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.service.Components;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@EnableNeo4jRepositories
@EnableTransactionManagement
public class Application {

    private static final String PLUGIN_DIR = "/Users/rossgabay/neo/neo4j-enterprise-3.1.2/plugins";
    private final static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);

    }

    @Bean
    @Transactional
    CommandLineRunner demo(PersonRepository personRepository, Neo4jTemplate template, SessionFactory sessionFactory) {
        return args -> {

            Person greg = new Person("Greg");
            greg.setArbitraryLongs(Arrays.asList(new Long(1), new Long(2)));

            personRepository.save(greg);

            personRepository.getPlist().forEach(System.out::println);

            Session s =  sessionFactory.openSession();

            System.out.println("now with sproc...");
            Result r = s.query("CALL com.rgabay.sprocnode(\"Person\")", Collections.EMPTY_MAP);
            r.forEach(System.out::println);
        };
    }

    @Bean
    public SessionFactory sessionFactory() {
        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration();
        Components.configure(configuration);

        EmbeddedDriver driver = new EmbeddedDriver(graphDatabaseService());
        Components.setDriver(driver);

        configuration.driverConfiguration().setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");

        return new SessionFactory(configuration, "com.rgabay.embedded_sdn_sproc");
    }

    @Bean
    public Neo4jTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new Neo4jTransactionManager(sessionFactory);
    }

    @Bean
    public Neo4jTemplate neo4jTemplate(SessionFactory sessionFactory) {

        return new Neo4jTemplate(sessionFactory);
    }

    @Bean(destroyMethod = "shutdown")
    public GraphDatabaseService graphDatabaseService() {
        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(new File("target/graph.db"))
                .setConfig(GraphDatabaseSettings.plugin_dir, PLUGIN_DIR)
                .newGraphDatabase();

        return graphDatabaseService;
    }
}
