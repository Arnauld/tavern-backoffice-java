package technbolts.compta.price.view;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.core.infrastructure.Id;
import technbolts.core.infrastructure.View;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@View
public class CatalogView {

    @JsonProperty("catalog_id")
    private final Id catalogId;

    @JsonProperty("version")
    private final long version;

    @JsonProperty("label")
    private final String label;

    @JsonCreator
    public CatalogView(@JsonProperty("catalogId") Id catalogId,
                       @JsonProperty("version") long version,
                       @JsonProperty("label") String label) {
        this.catalogId = catalogId;
        this.version = version;
        this.label = label;
    }

    public Id getCatalogId() {
        return catalogId;
    }

    public long getVersion() {
        return version;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatalogView that = (CatalogView) o;

        if (version != that.version) return false;
        if (!catalogId.equals(that.catalogId)) return false;
        if (!label.equals(that.label)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = catalogId.hashCode();
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + label.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CatalogView{" +
                "id=" + catalogId +
                ", v" + version +
                ", label='" + label + '\'' +
                '}';
    }
}
