package org.coderclan.guidepost;


import org.coderclan.guidepost.demo.jdbctemplate.JdbcTemplateTest;
import org.coderclan.guidepost.demo.jpa.JpaTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) {

        ConfigurableApplicationContext c = SpringApplication.run(ExampleApplication.class, args);
        JpaTest jpaTest = c.getBean(JpaTest.class);
        JdbcTemplateTest jdbcTemplateTest = c.getBean(JdbcTemplateTest.class);
        while (true) {
            try {
                jpaTest.createNewUser();
                jpaTest.getUser();

                jdbcTemplateTest.delete();
                jdbcTemplateTest.insert();
                jdbcTemplateTest.query();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
