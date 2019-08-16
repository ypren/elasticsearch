package ypren.demo.elasticsearch.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Query {

    /**
     * to query data stored in elasticsearch.
     * here, regarding there are already data stored in elasticsearch.
     */
    public void query() {
        log.info("query execute.");
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        Request syncRequest = new Request("GET", "/megacorp/employee/1");
        try {
            Response response = restClient.performRequest(syncRequest);
            log.info("query response {}", response);

            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder builder = new StringBuilder();
            String temp;
            while (null != (temp = bufferedReader.readLine())) {
                builder.append(temp);
            }

            log.info("query response string {}", builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
