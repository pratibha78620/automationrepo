package herokuapp.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by singhp1 on 14/08/2017.
 */
public class JsonObjectMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonObjectMapper.class);

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    public static ObjectMapper getInstance() {
        return objectMapper;
    }

    public static <T> T fromJson(String stringContent, Class<T> clazz) {
        try {
            JsonObject json = (JsonObject) new JsonParser().parse(stringContent);
            if (json.has("content"))
                stringContent = json.getAsJsonObject("content").toString();
            return getInstance().readValue(stringContent, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object object) {
        try {
            return getInstance().writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
