package technbolts.compta.server;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swoop.Response;

import java.io.IOException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JsonIO {
    private Logger logger = LoggerFactory.getLogger(JsonIO.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    public void writeJson(Response response, Object value) {
        response.contentType("application/json");
        try {
            response.body(objectMapper.writeValueAsString(value));
        } catch (IOException e) {
            logger.error("Fail to write json for {}", value, e);
            response.status(500);
            response.body(ExceptionUtils.getStackTrace(e));
        }
    }
}
