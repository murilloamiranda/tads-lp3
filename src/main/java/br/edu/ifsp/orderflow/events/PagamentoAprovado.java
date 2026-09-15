package br.edu.ifsp.orderflow.events;

import java.time.Instant;

/**
 * eVENTO Q SERA PUBLICANDO QUANDO UM PAGAMENTO FOR APROVADO
 * @param pedidoId
 * @param transacaoId
 * @param ocorridoEm
 */

public record PagamentoAprovado(
        String pedidoId,
        String transacaoId,
        Instant ocorridoEm
) implements IDomainEvent {}

