package br.edu.ifsp.orderflow.events;

/**
 * Um IEventBus (ou barramento de evento) funciona como um quadro de avisos,
 * onde serviços publicam eventos e os handles registrados são avisasdos.
 */
public interface IEventBus {
    <E extends IDomainEvent> void publish(E event);

    <E extends IDomainEvent> void register(IEventHandler<E> handler);
}
