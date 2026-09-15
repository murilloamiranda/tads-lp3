package br.edu.ifsp.orderflow.events;

import java.time.Instant;

/**
 * Representa algo que aconteceu no dominio, como:
 * PedidoCriado, PagamentoAprovado e etc. Quem publica um evento, não sabe e me, precisa saber,
 * quem vai consumir/reagir a ele
 */

public interface IDomainEvent {
    Instant ocorridoEm();
}
