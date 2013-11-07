package technbolts.compta.price;

import technbolts.core.infrastructure.Id;

import java.math.BigDecimal;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CatalogEntryState {
    private String label;
    private BigDecimal price;
    private Id entityId = Id.undefined();


    public String getLabel() {
        return label;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Id entityId() {
        return entityId;
    }

    void onEvent(CatalogEntryCreatedEvent event) {
        this.entityId = event.entityId();
        this.label = event.getLabel();
        this.price = event.getInitialPrice();
    }

    void onEvent(CatalogEntryPriceChangedEvent event) {
        this.price = event.getPrice();
    }

}
