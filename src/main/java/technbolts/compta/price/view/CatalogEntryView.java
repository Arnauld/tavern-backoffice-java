package technbolts.compta.price.view;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.core.infrastructure.Id;
import technbolts.core.infrastructure.View;

import java.math.BigDecimal;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@View
public class CatalogEntryView {

    @JsonProperty("catalog_id")
    private final Id catalogId;

    @JsonProperty("entry_id")
    private final Id entryId;

    @JsonProperty("version")
    private final long version;

    @JsonProperty("label")
    private final String label;

    @JsonProperty("price")
    private final BigDecimal price;

    @JsonCreator
    public CatalogEntryView(@JsonProperty("catalog_id") Id catalogId,
                            @JsonProperty("entry_id") Id entryId,
                            @JsonProperty("version") long version,
                            @JsonProperty("label") String label,
                            @JsonProperty("price") BigDecimal price) {
        this.catalogId = catalogId;
        this.entryId = entryId;
        this.version = version;
        this.label = label;
        this.price = price;
    }

    public Id getCatalogId() {
        return catalogId;
    }

    public Id getEntryId() {
        return entryId;
    }

    public long getVersion() {
        return version;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatalogEntryView that = (CatalogEntryView) o;

        if (version != that.version) return false;
        if (!catalogId.equals(that.catalogId)) return false;
        if (!entryId.equals(that.entryId)) return false;
        if (!label.equals(that.label)) return false;
        if (!price.equals(that.price)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = catalogId.hashCode();
        result = 31 * result + entryId.hashCode();
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + label.hashCode();
        result = 31 * result + price.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CatalogEntryView{" +
                "catalogId=" + catalogId +
                ", entryId=" + entryId +
                ", v" + version +
                ", label='" + label + '\'' +
                ", price=" + price +
                '}';
    }
}
