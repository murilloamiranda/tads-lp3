package br.edu.ifsp.orderflow.events;

import java.time.Instant;

public record PagamentoRecusado(
        String pedidoIDd,
        String motivo,
        Instant ocorridoEm
) implements IDomainEvent {}
