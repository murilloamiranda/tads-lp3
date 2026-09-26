package br.edu.ifsp.orderflow.service;

import br.edu.ifsp.orderflow.domain.Pedido;
import br.edu.ifsp.orderflow.domain.ResultadoPagamento;

public class PedidoService {
    private final IEstoqueService estoqueService;
    private final IPedidoRepository pedidoRepository;
    private final IPagamentoGateway pagamentoGateway;
    private final INotificacaoService notificacaoService;

    public PedidoService(
            IEstoqueService estoqueService,
            IPedidoRepository pedidoRepository,
            IPagamentoGateway pagamentoGateway,
            INotificacaoService notificacaoService
    ) {

        this.estoqueService = estoqueService;
        this.pedidoRepository = pedidoRepository;
        this.pagamentoGateway = pagamentoGateway;
        this.notificacaoService = notificacaoService;
    }

    public Pedido processar(Pedido pedido) {
        boolean foiReservado = this.estoqueService.reservar(pedido);

        if (!foiReservado) {
            pedido.cancelar();
            this.pedidoRepository.save(pedido);
            return pedido;
        }

        // processar o pagamento
        ResultadoPagamento resultado = this.pagamentoGateway.pagar(pedido);

        boolean naoAprovado = !resultado.isAprovado();
        if (naoAprovado) {
            this.estoqueService.liberar(pedido);
            pedido.cancelar();
            this.pedidoRepository.save(pedido);
            return pedido;
        }

        // salvar se o pagamento ocorreu com sucesso
        pedido.marcarComoPago();
        this.pedidoRepository.save(pedido);

        // notificar o pedido
        this.notificacaoService.notificar(
                pedido.getCliente(),
                "Pagamento aprovado! Pedido " + pedido.getIdCurto() + " confirmado"
        );

        // retorna o pedido
        return pedido;
    }
}
