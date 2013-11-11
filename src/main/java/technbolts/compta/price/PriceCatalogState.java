package technbolts.compta.price;

import com.google.common.collect.Sets;
import technbolts.core.infrastructure.Id;

import java.util.Collections;
import java.util.Set;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class PriceCatalogState {

    private Id entityId = Id.undefined();
    private String label;
    private final Set<Id> entryIds = Sets.newLinkedHashSet();

    public Id entityId() {
        return entityId;
    }

    void onEvent(PriceCatalogCreatedEvent event) {
        this.entityId = event.entityId();
        this.label = event.getLabel();
    }

    void onEvent(PriceCatalogEntryCreatedEvent event) {
        entryIds.add(event.getEntryId());
    }

    public String getLabel() {
        return label;
    }

    public Set<Id> entryIds() {
        return Collections.unmodifiableSet(entryIds);
    }


}
