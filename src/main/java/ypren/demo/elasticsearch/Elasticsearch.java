package ypren.demo.elasticsearch;

import ypren.demo.elasticsearch.process.Query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Elasticsearch {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Elasticsearch.class, args);
        ctx.getBean(Query.class).query();
        System.exit(0);
    }
}
