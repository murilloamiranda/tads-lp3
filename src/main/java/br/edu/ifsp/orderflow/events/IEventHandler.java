package br.edu.ifsp.orderflow.events;

/**
 * Algo que irá consumir/reagir a um tipo especifico de eventio
 * (uma class que implementa IDomaninEvent)
 *
 * O parãmetr de tipo E garante, em tempo de comilação, que
 * um handler de PagamentoAprovado nunca receba um PagamentoRecusado
 */

public interface IEventHandler <E extends IDomainEvent>{

    void handle(E event);

    /**
     * Qual tipo de evento este handle trata/consome?
     *
     * Necessário por causa do apagamento de tipo do java (tpe erasure)
     * @return
     */
    Class<E> eventType();
}
