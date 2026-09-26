package br.edu.ifsp.orderflow.events;

import java.time.Instant;

// Temos dois eventos. Quantos handler precisamos implementar? 2
// Por quẽ? porque são tipos diferentes e, além disso, cada um terá uma responsabilidade única

public record PagamentoRecusado(
        String pedidoId,
        String motivo,
        Instant ocorridoEm
) implements IDomainEvent {
}
