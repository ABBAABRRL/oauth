package finaltest.demo.utils;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public final class JsonUtils {

    private static final ObjectMapper Obj = new ObjectMapper();

    static {
        Obj.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        Obj.registerModule(new JavaTimeModule());
    }

    private JsonUtils() { }


    public static <T> T toObj(Object o,Class<T> clazz){
        return toObj(toJsonString(o),clazz);
    }
    public static String toJsonString(Object o) {
        try {
            return Obj.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> T toObj(String jsonStr, Class<T> clazz) {
        try {
            return Obj.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Exception occurred when map json string to specified obj", e);
        }
    }


}
