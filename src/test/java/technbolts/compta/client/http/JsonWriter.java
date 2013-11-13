package technbolts.compta.client.http;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JsonWriter {
    private ObjectMapper mapper = new ObjectMapper();

    public void write(HttpEntityEnclosingRequest request, JsonNode node) throws IOException {
        String format = mapper.writeValueAsString(node);
        write(request, format);
    }
    public void write(HttpEntityEnclosingRequest request, String json) throws IOException {
        StringEntity input = new StringEntity(json);
        input.setContentType("application/json");
        input.setContentEncoding("UTF-8");
        request.setEntity(input);
    }
}
