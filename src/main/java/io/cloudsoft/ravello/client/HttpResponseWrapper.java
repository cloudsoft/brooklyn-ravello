package io.cloudsoft.ravello.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

public class HttpResponseWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(HttpResponseWrapper.class);

    private final HttpResponse response;
    private final ObjectMapper mapper;
    
    HttpResponseWrapper(HttpResponse response, ObjectMapper mapper) {
        this.response = response;
        this.mapper = mapper;
    }

    public int getStatusCode() {
        return response.getStatusLine().getStatusCode();
    }

    public boolean isErrorResponse() {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode < 200 || statusCode >= 400;
    }

    public HttpResponseWrapper consumeResponse() {
        try {
            EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        return this;
    }

    public <T> T get(Class<T> type) {
        T object = unmarshalResponseEntity(mapper.constructType(type));
        consumeResponse();
        return object;
    }

    public <T> List<T> getList(Class<T> listType) {
        JavaType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, listType);
        List<T> object = unmarshalResponseEntity(mapper.constructType(collectionType));
        consumeResponse();
        return object;
    }

    private <T> T unmarshalResponseEntity(JavaType type) {
        if (isErrorResponse()) {
            LOG.debug("Request errored[{}], not unmarshalling to {}",
                    response.getStatusLine().getStatusCode(), type.getGenericSignature());
            return null;
        }

        String responseContent = getResponseContentAsString();
        if (LOG.isTraceEnabled()) LOG.trace(responseContent);
        try {
            T unmarshalled = mapper.readValue(responseContent, type);
            if (LOG.isTraceEnabled()) LOG.trace("Unmarshalled: " + unmarshalled.toString());
            return unmarshalled;
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public String getResponseContentAsString() {
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

}
