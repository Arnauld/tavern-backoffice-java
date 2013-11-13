package technbolts.compta.price;

import org.apache.commons.cli.ParseException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import technbolts.compta.client.http.HttpClient;
import technbolts.compta.client.http.JsonReader;
import technbolts.compta.client.http.JsonWriter;
import technbolts.compta.server.Server;
import technbolts.compta.server.support.PortProvider;
import technbolts.compta.server.support.SwoopServerCountDownOnceStartedListener;
import technbolts.core.infrastructure.support.JdbcConnectionPools;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.fest.assertions.Assertions.assertThat;
import static swoop.Swoop.listener;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CatalogUsecasesITest {

    public static final String UUID_PATTERN = "[a-f0-9]{8}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{12}";
    private static Server server;

    @BeforeClass
    public static void setUp() throws InterruptedException, ParseException {
        Integer port = PortProvider.acquire();
        String[] args = new String[]{
                "-port", String.valueOf(port),
                "-dbUrl", JdbcConnectionPools.acquireUrl()
        };

        CountDownLatch latch = new CountDownLatch(1);
        listener(new SwoopServerCountDownOnceStartedListener(latch));

        server = new Server();
        server.start(args);

        // wait for server to be started
        latch.await();
    }

    @AfterClass
    public static void tearDown() {
        server.stop();
    }

    private JsonReader jsonReader = new JsonReader();
    private JsonWriter jsonWriter = new JsonWriter();
    private String baseUrl;
    private HttpClient httpClient;
//    private WebTester webTester;

    @Before
    public void initWebTester() {
        httpClient = new HttpClient();
        baseUrl = "http://localhost:" + server.getPort();

//        webTester = new WebTester();
//        webTester.setBaseUrl("http://localhost:" + server.getPort());
//        webTester.beginAt("/about");
//        String contentType = webTester.getHeader("Content-type");
//        assertThat(contentType).isEqualTo("application/json; charset=UTF-8");
//        assertThat(webTester.getPageSource()).isEqualTo("{\"name\":\"compta\"}");

    }

    @Test
    public void about__json_utf8() throws IOException {
        HttpGet aboutGet = new HttpGet(baseUrl + "/about");
        HttpResponse httpResponse = httpClient.execute(aboutGet);
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);
        Header header = httpResponse.getFirstHeader("Content-type");
        assertThat(header.getValue()).isEqualTo("application/json; charset=UTF-8");

        JsonNode node = jsonReader.read(httpResponse.getEntity());
        assertThat(node).isNotNull();
        assertThat(node.get("name")).isNotNull();
        assertThat(node.get("name").asText()).isEqualTo("compta");
    }

    @Test
    public void catalogs__get_then_create_then_get_ordered_alphabetically() throws IOException {
        // --- Get catalogs
        JsonNode catalogs0 = getCatalogs();
        assertThat(catalogs0.size()).isEqualTo(0);

        // --- Create a new catalog
        String catalogId0 = createCatalog("Drinks");
        String catalogId1 = createCatalog("Zoom");
        String catalogId2 = createCatalog("Broom");

        // --- Get catalogs
        JsonNode catalogs1 = getCatalogs();
        assertThat(catalogs1.size()).isEqualTo(3);

        JsonNode e0 = catalogs1.get(0);
        assertThat(e0.get("catalog_id")).describedAs(e0.toString()).isNotNull();
        assertThat(e0.get("catalog_id").asText()).isEqualTo(catalogId2);
        assertThat(e0.get("label").asText()).isEqualTo("Broom");

        JsonNode e1 = catalogs1.get(1);
        assertThat(e1.get("catalog_id")).describedAs(e1.toString()).isNotNull();
        assertThat(e1.get("catalog_id").asText()).isEqualTo(catalogId0);
        assertThat(e1.get("label").asText()).isEqualTo("Drinks");

        JsonNode e2 = catalogs1.get(2);
        assertThat(e2.get("catalog_id")).describedAs(e2.toString()).isNotNull();
        assertThat(e2.get("catalog_id").asText()).isEqualTo(catalogId1);
        assertThat(e2.get("label").asText()).isEqualTo("Zoom");
    }

    private JsonNode getCatalogs() throws IOException {
        HttpGet catalogsGet = new HttpGet(baseUrl + "/catalogs");
        HttpResponse httpResponse = httpClient.execute(catalogsGet);
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);

        JsonNode node = jsonReader.read(httpResponse.getEntity());
        assertThat((Object) node).isInstanceOf(ArrayNode.class);
        return node;
    }

    private String createCatalog(String label) throws IOException {
        HttpResponse httpResponse;
        JsonNode node;
        HttpPost catalogPost = new HttpPost(baseUrl + "/catalog");
        jsonWriter.write(catalogPost, ("{'label':'" + label + "'}").replace("'", "\""));
        httpResponse = httpClient.execute(catalogPost);

        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);
        node = jsonReader.read(httpResponse.getEntity());
        assertThat(node).isNotNull();
        assertThat(node.get("catalog_id")).describedAs(node.toString()).isNotNull();

        String catalogId = node.get("catalog_id").asText();
        assertThat(catalogId).matches(UUID_PATTERN);
        return catalogId;
    }

    @Test
    public void create_catalog() {

    }
}
