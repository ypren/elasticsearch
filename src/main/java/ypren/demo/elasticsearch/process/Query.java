package ypren.demo.elasticsearch.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Query {

    /**
     * to query data stored in elasticsearch.
     * here, regarding there are already data stored in elasticsearch.
     */
    public void query() throws InterruptedException {
        log.info("query execute.");
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        Request syncRequest = new Request("GET", "/megacorp/employee/1");
        Request asyncRequest = new Request("GET", "/megacorp/employee/2");

        restClient.performRequestAsync(asyncRequest, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                log.info("async response success.");
                log.info("query asyncResponse content {}", getResponseContent(response));
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(Exception exception) {
                log.error("async response fail.");
                countDownLatch.countDown();
            }
        });

        try {
            Response syncResponse = restClient.performRequest(syncRequest);
            log.info("query syncResponse content {}", getResponseContent(syncResponse));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }

        countDownLatch.await();
        try {
            restClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getResponseContent(Response response) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){
            String temp;
            while (null != (temp = bufferedReader.readLine())) {
                builder.append(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
