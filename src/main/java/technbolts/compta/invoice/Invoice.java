package technbolts.compta.invoice;

import technbolts.core.infrastructure.*;

import java.math.BigDecimal;
import java.util.List;

import static technbolts.core.infrastructure.VersionedDomainEvent.applyOnAsSideEffect;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Invoice extends AbstractEntity {

    public Invoice(UnitOfWork unitOfWork) {
        super(unitOfWork);
    }

    public static Invoice create(UnitOfWork unitOfWork, List<Item> lines) {
        return create(unitOfWork, Id.next(Invoice.class), lines);
    }
    public static Invoice create(UnitOfWork unitOfWork, Id invoiceId, List<Item> lines) {
        Invoice invoice = new Invoice(unitOfWork);
        invoice.doCreate(invoiceId, lines);
        return invoice;
    }

    public static Invoice loadFromHistory(UnitOfWork unitOfWork, Stream<VersionedDomainEvent> stream) {
        if(!stream.hasRemaining())
            throw new EmptyStreamException();

        Invoice invoice = new Invoice(unitOfWork);
        stream.consume(applyOnAsSideEffect(invoice));
        return invoice;
    }

    private InvoiceState state = new InvoiceState();

    private void doCreate(Id orderId, List<Item> lines) {
        applyNewEvent(new InvoiceCreatedEvent(orderId, lines));
    }

    public void applyDiscount(Id referenceId, Discount discount) {
        if(state.hasItem(referenceId))
            applyNewEvent(new InvoiceItemDiscountedEvent(entityId(), referenceId, discount));
        else
            throw new DiscountNotApplicationException("Item not referenced " + referenceId);
    }

    @Override
    public Id entityId() {
        return state.entityId();
    }

    public List<Item> items() {
        return state.items();
    }

    public BigDecimal price() {
        return state.price();
    }

    @Override
    public <T> T adaptTo(Class<T> required) {
        if(required.equals(InvoiceState.class))
            return (T)state;
        return super.adaptTo(required);
    }
}
