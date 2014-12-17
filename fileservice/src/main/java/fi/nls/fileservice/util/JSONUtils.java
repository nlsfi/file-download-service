package fi.nls.fileservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.nls.fileservice.common.DataAccessException;


public class JSONUtils {
	
    public static String object2JSON(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new DataAccessException(e);
        }
    }
}
