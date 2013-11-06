package technbolts.compta.invoice;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import technbolts.compta.infrastructure.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static technbolts.compta.infrastructure.VersionedDomainEvent.applyOnAsSideEffect;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Invoice extends AbstractEntity {

    public static Invoice create(List<Item> lines) {
        return create(Id.next(Invoice.class), lines);
    }
    public static Invoice create(Id invoiceId, List<Item> lines) {
        Invoice invoice = new Invoice();
        invoice.doCreate(invoiceId, lines);
        return invoice;
    }

    public static Invoice loadFromHistory(Stream<VersionedDomainEvent> stream) {
        if(!stream.hasRemaining())
            throw new EmptyStreamException();

        Invoice invoice = new Invoice();
        stream.consume(applyOnAsSideEffect(invoice));
        return invoice;
    }

    private InvoiceStatus status;
    private List<Item> items;
    private BigDecimal price;
    private Map<Id, Discount> itemDiscounts = Maps.newHashMap();

    private void doCreate(Id orderId, List<Item> lines) {
        applyNewEvent(new InvoiceCreatedEvent(orderId, lines));
    }

    void onEvent(InvoiceCreatedEvent event) {
        assignId(event.entityId());
        this.status = InvoiceStatus.Created;
        this.items  = Lists.newArrayList(event.items());
    }

    void onEvent(InvoicePaidEvent event) {
        this.status = InvoiceStatus.Paid;
    }

    void onEvent(InvoiceItemAddedEvent event) {
        this.items.addAll(event.getItemAdded());
    }

    void onEvent(InvoiceItemDiscountedEvent event) {
        itemDiscounts.put(event.getItemReferenceId(), event.getDiscount());
    }

    public void applyDiscount(Id referenceId, Discount discount) {
        if(hasItem(referenceId))
            applyNewEvent(new InvoiceItemDiscountedEvent(entityId(), referenceId, discount));
        else
            throw new DiscountNotApplicationException("Item not referenced " + referenceId);
    }

    private boolean hasItem(Id itemReferenceId) {
        for(Item item : items) {
            if(item.referenceId().equals(itemReferenceId)) {
                return true;
            }
        }
        return false;
    }

    public List<Item> items() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal price() {
        BigDecimal total = BigDecimal.ZERO;
        for(Item item : items) {
            BigDecimal itemPrice;
            Discount discount = itemDiscounts.get(item.referenceId());
            if(discount != null) {
                itemPrice = discount.calculatePrice(item);
            }
            else {
                itemPrice = item.price();
            }
            total = total.add(itemPrice);
        }
        return total;
    }


}
