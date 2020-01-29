package com.importa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.importa.bo.Log;
import com.importa.repo.LogRepository;

@SpringBootApplication
public class StartApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartApplication.class);

	@Autowired
    private LogRepository logRepo;
	
    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    @Override
    public void run(String... args) {

        log.info("StartApplication...");

        logRepo.save(new Log("com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...", "INFO", 1L));
        logRepo.save(new Log("o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8080 (http)", "INFO", 1L));
        logRepo.save(new Log("INFO  o.s.web.servlet.DispatcherServlet - Initializing Servlet 'dispatcherServlet'", "INFO", 1L));
        logRepo.save(new Log("INFO  o.s.web.servlet.DispatcherServlet - Completed initialization in 14 ms", "INFO", 1L));
        logRepo.save(new Log("com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Pool stats (total=5, active=0, idle=5, waiting=0)", "DEBUG", 1000L));

        System.out.println("\nlogs findAll()");
        logRepo.findAll().forEach(x -> System.out.println(x));

        System.out.println("\nlog findById(1L)");
        logRepo.findById(1l).ifPresent(x -> System.out.println(x));

        System.out.println("\nlog findByName('token')");
        logRepo.findByName("token").forEach(x -> System.out.println(x));


    }

}