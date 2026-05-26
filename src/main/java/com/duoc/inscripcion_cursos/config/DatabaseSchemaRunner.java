package com.duoc.inscripcion_cursos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Profile("init-db")
public class DatabaseSchemaRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaRunner.class);

    private final DataSource dataSource;
    private final ApplicationContext applicationContext;

    public DatabaseSchemaRunner(DataSource dataSource, ApplicationContext applicationContext) {
        this.dataSource = dataSource;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Creando tablas en Oracle Cloud...");
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("poblamiento-datos-oracle.sql"));
            log.info("Tablas creadas correctamente: cursos, inscripciones, inscripcion_detalles");
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("ORA-00955")) {
                log.warn("Algunos objetos ya existen en Oracle. Si las tablas ya estan creadas, puedes continuar.");
            } else {
                throw ex;
            }
        }
        SpringApplication.exit(applicationContext, () -> 0);
    }
}
