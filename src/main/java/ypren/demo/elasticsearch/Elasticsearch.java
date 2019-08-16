package ypren.demo.elasticsearch;

import ypren.demo.elasticsearch.process.Query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Elasticsearch {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Elasticsearch.class, args);
        try {
            ctx.getBean(Query.class).query();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
