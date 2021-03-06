package technbolts.compta.server;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swoop.Request;
import swoop.Response;
import swoop.util.ContentTypes;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JsonIO {
    private Logger logger = LoggerFactory.getLogger(JsonIO.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    private Charset UTF8 = Charset.forName("utf-8");

    public void writeJson(Response response, Object value) {
        try {
            response.contentType(ContentTypes.json());
            response.charset(ContentTypes.UTF8);
            response.body(objectMapper.writeValueAsString(value));
        } catch (IOException e) {
            logger.error("Fail to write json for {}", value, e);
            response.status(500);
            response.body(ExceptionUtils.getStackTrace(e));
        }
    }

    public <T> T readJson(Request request, Class<T> type) throws IOException {
        return objectMapper.readValue(request.bodyAsBytes(), type);
    }
}
