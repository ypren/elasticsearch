package ypren.demo.elasticsearch.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
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

        // set basic authentication, not that authentication can be disabled by clients,
        // and if 401 is returned, the exact same request with the basic authentication header
        // will be resend, sample as follows.
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user",
         "pwd"       ));

        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200))
                // specify http host and config timeouts.
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        return requestConfigBuilder.setConnectTimeout(5000)
                                .setSocketTimeout(60000);
                    }
                })
                // set basic authentication.
                // SSL can be set by httpClientConfigCallback.
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        // disable Preemptive Authentication.
                        httpClientBuilder.disableAuthCaching();
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                })
                .build();
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
