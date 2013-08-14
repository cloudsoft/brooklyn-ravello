package io.cloudsoft.ravello.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

@ThreadSafe
public class RavelloHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(RavelloHttpClient.class);

    /**
     * TODO: exception handling.
     */

    private static final ObjectMapper MAPPER = RavelloObjectMapper.newObjectMapper();

    private final URI endpoint;
    private final DefaultHttpClient httpClient = new DefaultHttpClient(new PoolingClientConnectionManager());

    public RavelloHttpClient(String endpoint, String username, String password) {
        try {
            this.endpoint = new URI(checkNotNull(endpoint, "endpoint"));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Couldn't turn endpoint into URI", e);
        }
        setCredentials(username, password);
    }

    private void setCredentials(String username, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(endpoint.getHost(), AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(username, password));
        httpClient.setCredentialsProvider(credentialsProvider);
    }

    public void get(String uri) {
        doRequest(new HttpGet(endpoint + uri));
    }

    public <T> T get(String uri, Class<T> unmarshalAs) {
        return doRequest(new HttpGet(endpoint + uri), unmarshalAs);
    }

    public <T> List<T> getList(String uri, Class<T> unmarshalAs) {
        JavaType collectionType = MAPPER.getTypeFactory().constructCollectionType(List.class, unmarshalAs);
        return doRequest(new HttpGet(endpoint + uri), Optional.of(collectionType));
    }

    public void post(String uri) {
        doRequest(new HttpPost(endpoint + uri));
    }

    public void post(String uri, Object body) {
        HttpPost request = new HttpPost(endpoint+uri);
        attachEntityToRequest(request, checkNotNull(body, "body"));
        doRequest(request, Optional.<JavaType>absent());
    }

    public <T> T post(String uri, Object body, Class<T> unmarshalAs) {
        HttpPost request = new HttpPost(endpoint+uri);
        attachEntityToRequest(request, checkNotNull(body, "body"));
        return doRequest(request, unmarshalAs);
    }

    public <T> T put(String uri, Object body, Class<T> unmarshalAs) {
        HttpPut request = new HttpPut(endpoint+checkNotNull(uri, "uri"));
        attachEntityToRequest(request, checkNotNull(body, "body"));
        return doRequest(request, unmarshalAs);
    }

    public void put(String uri, Object body) {
        HttpPut request = new HttpPut(endpoint+checkNotNull(uri, "uri"));
        attachEntityToRequest(request, checkNotNull(body, "body"));
        doRequest(request);
    }

    public void delete(String uri) {
        doRequest(new HttpDelete(endpoint+uri));
    }

    private void doRequest(HttpRequestBase request) {
        doRequest(request, Optional.<JavaType>absent());
    }

    private <T> T doRequest(HttpRequestBase request, Class<T> unmarshalAs) {
        return doRequest(request, Optional.of(MAPPER.constructType(unmarshalAs)));
    }

    private <T> T doRequest(HttpRequestBase request, Optional<JavaType> unmarshalAs) {
        request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        LOG.debug("Requesting: " + request);
        try {
            HttpResponse response = httpClient.execute(request);
            T responseEntity = null;
            if (isErrorResponse(response)) {
                LOG.warn("Request errored: " + response.toString());
                String out = getResponseContentAsString(response);
                LOG.warn(out.equals("") ? "(No response body)" : out);
            } else {
                LOG.debug(response.toString());
                if (unmarshalAs.isPresent()) {
                    responseEntity = unmarshalResponseEntity(response, unmarshalAs.get());
                }
                EntityUtils.consume(response.getEntity());
            }
            return responseEntity;
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

    private String getResponseContentAsString(HttpResponse response) {
        InputStream in = null;
        try {
            in = response.getEntity().getContent();
            return CharStreams.toString(new InputStreamReader(in, "UTF-8"));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            if (in != null) {
                Closeables.closeQuietly(in);
            }
        }
    }

    private <T> T unmarshalResponseEntity(HttpResponse response, JavaType type) {
        if (isErrorResponse(response)) {
            LOG.debug("Request errored[{}], not unmarshalling to {}",
                    response.getStatusLine().getStatusCode(), type.getGenericSignature());
            return null;
        }

        String responseContent = getResponseContentAsString(response);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Unmarshalling response to " + type.getGenericSignature());
            LOG.trace(responseContent);
        }
        try {
            T unmarshalled = MAPPER.readValue(responseContent, type);
            LOG.info(unmarshalled.toString());
            return unmarshalled;
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    /** @return true if response status code is not between 200 and 399. */
    private boolean isErrorResponse(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode < 200 || statusCode >= 400;
    }
}
