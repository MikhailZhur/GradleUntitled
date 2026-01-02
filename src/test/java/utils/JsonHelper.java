package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class JsonHelper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T fromJson(String jsonPath, Class<T> out){
        try {
            return mapper.readValue(new File(jsonPath),out);
        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public static String toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

}
