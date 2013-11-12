package technbolts.compta.price.view;

import technbolts.core.infrastructure.Id;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CatalogView {
    private final Id id;
    private final long version;
    private final String label;

    public CatalogView(Id id, long version, String label) {
        //To change body of created methods use File | Settings | File Templates.
        this.id = id;
        this.version = version;
        this.label = label;
    }

    public Id getId() {
        return id;
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
        if (!id.equals(that.id)) return false;
        if (!label.equals(that.label)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + label.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CatalogView{" +
                "id=" + id +
                ", v" + version +
                ", label='" + label + '\'' +
                '}';
    }
}
