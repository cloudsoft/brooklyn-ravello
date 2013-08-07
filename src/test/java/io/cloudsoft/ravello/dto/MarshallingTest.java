package io.cloudsoft.ravello.dto;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import io.cloudsoft.ravello.client.RavelloObjectMapper;

public abstract class MarshallingTest {

    private static final ObjectMapper mapper = RavelloObjectMapper.newObjectMapper();

    public <T> T unmarshalFile(String filename, Class<T> as) {
        try {
            return mapper.readValue(Resources.getResource(MarshallingTest.class, filename), as);
        } catch (IOException e) {
            throw new RuntimeException("Unmarshal to "+as+" failed", e);
        }
    }

    public <T> T unmarshalFile(String filename, TypeReference<T> as) {
        try {
            return mapper.readValue(Resources.getResource(MarshallingTest.class, filename), as);
        } catch (IOException e) {
            throw new RuntimeException("Unmarshal to "+as+" failed", e);
        }
    }

    public <T> T unmarshal(String json, Class<T> as) {
        try {
            return mapper.readValue(new File("user.json"), as);
        } catch (IOException e) {
            throw new RuntimeException("Unmarshal to "+as+"failed", e);
        }
    }

}
