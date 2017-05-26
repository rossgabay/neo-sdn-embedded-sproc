
package com.rgabay.embedded_sdn_sproc;

import com.rgabay.embedded_sdn_sproc.domain.Person;
import com.rgabay.embedded_sdn_sproc.procedure.PersonProc;
import com.rgabay.embedded_sdn_sproc.repository.PersonRepository;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.impl.proc.Procedures;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.service.Components;
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

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@EnableNeo4jRepositories
@EnableTransactionManagement
public class Application {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);

	}

	@Bean
    @Transactional
	CommandLineRunner demo(PersonRepository personRepository, Neo4jTemplate template) {
		return args -> {

            EmbeddedDriver embeddedDriver = (EmbeddedDriver) Components.driver();
            GraphDatabaseService databaseService = embeddedDriver.getGraphDatabaseService();
            ((GraphDatabaseAPI) databaseService).getDependencyResolver().resolveDependency(Procedures.class).registerProcedure(PersonProc.class);

            Person greg = new Person("Greg");
            greg.setArbitraryLongs(Arrays.asList(new Long(1), new Long(2)));

			personRepository.save(greg);

            personRepository.getPlist().forEach(System.out::println);


            System.out.println("now with sproc - annotated...");
            personRepository.getPlistProc().forEach(System.out::println);

            System.out.println("now with sproc - direct query...");
            Result r = template.query("CALL com.rgabay.sprocnode(\"Person\")", Collections.EMPTY_MAP);
            r.forEach(System.out::println);

		};
	}

    // embedded driver config example, to make the db non-permanent, remove .setURI call
	@Bean
    public Configuration configuration() {
        Configuration config = new Configuration();
        config
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
                //.setURI("file:///var/tmp/graph.db");
        return config;
    }

	// HTTP driver config example

	/*@Bean
	public Configuration configuration() {
		Configuration config = new Configuration();
		config
				.driverConfiguration()
				.setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
				.setURI("http://localhost:7474");
		return config;
	}*/

	@Bean
	public SessionFactory sessionFactory() {
		return new SessionFactory(configuration(), "com.rgabay.embedded_sdn_sproc");

	}

    @Bean
    public Neo4jTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new Neo4jTransactionManager(sessionFactory);
    }

    @Bean
    public Neo4jTemplate neo4jTemplate(SessionFactory sessionFactory) {
        return new Neo4jTemplate(sessionFactory);
    }
}
