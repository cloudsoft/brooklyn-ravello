package io.cloudsoft.ravello.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

@ThreadSafe
public class RavelloHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(RavelloHttpClient.class);
    private static final ObjectMapper MAPPER = RavelloObjectMapper.newObjectMapper();

    /**
     * TODO: exception handling.
     */

    private final URI endpoint;
    private final DefaultHttpClient httpClient = new DefaultHttpClient(new PoolingClientConnectionManager());

    public RavelloHttpClient(String endpoint, String username, String password) {
        try {
            this.endpoint = new URI(checkNotNull(endpoint, "endpoint"));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Couldn't turn endpoint into URI", e);
        }
        // Set username and password on httpClient
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(this.endpoint.getHost(), AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(username, password));
        httpClient.setCredentialsProvider(credentialsProvider);
    }

    public HttpResponseWrapper get(String uri) {
        return doRequest(new HttpGet(endpoint + uri));
    }

    public HttpResponseWrapper post(String uri) {
        return doRequest(new HttpPost(endpoint + uri));
    }

    public HttpResponseWrapper post(String uri, Object body) {
        HttpPost request = new HttpPost(endpoint+uri);
        attachEntityToRequest(request, checkNotNull(body, "body"));
        return doRequest(request);
    }

    public HttpResponseWrapper put(String uri, Object body) {
        HttpPut request = new HttpPut(endpoint+checkNotNull(uri, "uri"));
        attachEntityToRequest(request, checkNotNull(body, "body"));
        return doRequest(request);
    }

    public HttpResponseWrapper delete(String uri) {
        return doRequest(new HttpDelete(endpoint+uri));
    }

    private HttpResponseWrapper doRequest(HttpRequestBase request) {
        request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        LOG.debug("Requesting: " + request);
        try {
            HttpResponse response = httpClient.execute(request);
            HttpResponseWrapper w = new HttpResponseWrapper(response, MAPPER);
            if (w.isErrorResponse()) {
                LOG.warn("Request {} errored: {}", request, response.toString());
                String out = w.getResponseContentAsString();
                LOG.warn(out.equals("") ? "(No response body)" : out);
            } else {
                LOG.debug(response.toString());
            }
            return new HttpResponseWrapper(response, MAPPER);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private void attachEntityToRequest(HttpEntityEnclosingRequestBase request, Object entity) {
        StringWriter writer = new StringWriter();
        try {
            MAPPER.writeValue(writer, entity);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

        LOG.trace("Marshalled "+entity.getClass().getSimpleName()+": " + writer.toString());
        HttpEntity httpEntity = new StringEntity(writer.toString(), ContentType.APPLICATION_JSON);
        request.setEntity(httpEntity);
    }

}
