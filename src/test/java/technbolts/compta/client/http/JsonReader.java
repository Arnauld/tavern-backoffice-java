package technbolts.compta.client.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JsonReader {
    private ObjectMapper mapper = new ObjectMapper(); // create once, reuse

    public JsonNode read(HttpEntity entity) throws IOException {
        InputStream stream = entity.getContent();
        String content = null;
        try {
            content = IOUtils.toString(stream);
            return mapper.readTree(content);
        } catch (RuntimeException re) {
            throw new IOException("Fail to read entity: " + content, re);
        } catch (IOException re) {
            throw new IOException("Fail to read entity: " + content, re);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }
}
