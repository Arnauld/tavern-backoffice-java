package technbolts.compta.price.command;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.core.infrastructure.Command;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@Command
public class CreateCatalogCommand {

    @JsonProperty
    private final String label;

    @JsonCreator
    public CreateCatalogCommand(@JsonProperty("label") String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreateCatalogCommand that = (CreateCatalogCommand) o;

        if (label != null ? !label.equals(that.label) : that.label != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return label != null ? label.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CreateCatalogCommand{" +
                "label='" + label + '\'' +
                '}';
    }
}
