package io.cloudsoft.ravello.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
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
     * TODO: logging granularity. exception handling.
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

    private RavelloHttpClient setCredentials(String username, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(endpoint.getHost(), AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(username, password));
        httpClient.setCredentialsProvider(credentialsProvider);
        return this;
    }

    public HttpResponse get(String uri) {
        return doRequest(new HttpGet(endpoint + uri));
    }

    public <T> T get(String uri, Class<T> unmarshalAs) {
        HttpResponse response = get(uri);
        return unmarshalResponseEntity(response, MAPPER.constructType(unmarshalAs));
    }

    public <T> List<T> getList(String uri, Class<T> unmarshalAs) {
        HttpResponse response = get(uri);
        return unmarshalResponseEntity(response,
                MAPPER.getTypeFactory().constructCollectionType(List.class, unmarshalAs));
    }

    public HttpResponse post(String uri) {
        return post(uri, Optional.absent());
    }

    public HttpResponse post(String uri, Object body) {
        return post(uri, Optional.fromNullable(body));
    }

    private HttpResponse post(String uri, Optional<Object> body) {
        HttpPost request = new HttpPost(endpoint+uri);
        if (body.isPresent()) {
            attachEntityToRequest(request, body.get());
        }
        return doRequest(request);
    }

    public <T> T post(String uri, Object body, Class<T> unmarshalAs) {
        HttpResponse response = post(uri, body);
        return unmarshalResponseEntity(response, MAPPER.constructType(unmarshalAs));
    }

    public HttpResponse delete(String uri) {
        return doRequest(new HttpDelete(endpoint+uri));
    }

    private HttpResponse doRequest(HttpRequestBase request) {
        request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        LOG.info(request.toString());
        try {
            HttpResponse response = httpClient.execute(request);
            LOG.info(response.toString());
            if (isErrorResponse(response)) {
                String out = getResponseContentAsString(response);
                LOG.error(out.equals("") ? "(No response content)" : out);
            }
            return response;
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

        LOG.info("Marshalled "+entity.getClass().getSimpleName()+": " + writer.toString());
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
            LOG.info("Request errored[{}], not unmarshalling to {}",
                    response.getStatusLine().getStatusCode(), type.getGenericSignature());
            return null;
        }
        LOG.info("Unmarshalling response to " + type.getGenericSignature());
        String responseContent = getResponseContentAsString(response);
        LOG.info(responseContent);
        try {
            return MAPPER.readValue(responseContent, type);
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
