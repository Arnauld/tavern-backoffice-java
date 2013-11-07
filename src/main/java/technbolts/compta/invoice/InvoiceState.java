package technbolts.compta.invoice;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import technbolts.core.infrastructure.Id;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class InvoiceState {
    private InvoiceStatus status;
    private List<Item> items;
    private BigDecimal price;
    private Map<Id, Discount> itemDiscounts = Maps.newHashMap();
    private Id entityId = Id.undefined();

    void onEvent(InvoiceCreatedEvent event) {
        this.entityId = event.entityId();
        this.status = InvoiceStatus.Created;
        this.items = Lists.newArrayList(event.items());
    }

    void onEvent(InvoicePaidEvent event) {
        this.status = InvoiceStatus.Paid;
    }

    void onEvent(InvoiceItemAddedEvent event) {
        this.items.addAll(event.getItemsAdded());
    }

    void onEvent(InvoiceItemDiscountedEvent event) {
        itemDiscounts.put(event.getItemReferenceId(), event.getDiscount());
    }

    public Id entityId() {
        return entityId;
    }

    public List<Item> items() {
        return Collections.unmodifiableList(items);
    }

    public boolean hasItem(Id itemReferenceId) {
        for (Item item : items) {
            if (item.referenceId().equals(itemReferenceId)) {
                return true;
            }
        }
        return false;
    }

    public BigDecimal price() {
        BigDecimal total = BigDecimal.ZERO;
        for (Item item : items()) {
            BigDecimal itemPrice;
            Discount discount = itemDiscounts.get(item.referenceId());
            if (discount != null) {
                itemPrice = discount.calculatePrice(item);
            } else {
                itemPrice = item.price();
            }
            total = total.add(itemPrice);
        }
        return total;
    }

}
