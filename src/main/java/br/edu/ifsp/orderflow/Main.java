package br.edu.ifsp.orderflow;

import br.edu.ifsp.orderflow.domain.Cliente;
import br.edu.ifsp.orderflow.domain.ItemPedido;
import br.edu.ifsp.orderflow.domain.Pedido;
import br.edu.ifsp.orderflow.domain.Produto;
import br.edu.ifsp.orderflow.events.IEventBus;
import br.edu.ifsp.orderflow.events.PagamentoAprovado;
import br.edu.ifsp.orderflow.events.SimpleEventBus;
import br.edu.ifsp.orderflow.events.handlers.*;
import br.edu.ifsp.orderflow.infra.ConsoleNotificacaoService;
import br.edu.ifsp.orderflow.infra.FakePagamentoGateway;
import br.edu.ifsp.orderflow.infra.InMemoryEstoqueService;
import br.edu.ifsp.orderflow.infra.InMemoryPedidoRepository;
import br.edu.ifsp.orderflow.service.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        IEstoqueService estoqueService = new InMemoryEstoqueService();
        IPedidoRepository pedidoRepository = new InMemoryPedidoRepository();
        INotificacaoService notificacaoService = new ConsoleNotificacaoService();
        IPagamentoGateway pagamentoGateway = new FakePagamentoGateway();


        IEventBus eventBus = new SimpleEventBus();
        Metricas metricas = new Metricas();

        eventBus.register(new PedidoCriadoLogHandler());
        eventBus.register(new PagamentoRecusadoNotificacaoHandler(pedidoRepository, notificacaoService));
        eventBus.register(new PagamentoRecusadoNotificacaoHandler(pedidoRepository, notificacaoService));
        eventBus.register(new PedidoCanceladoNotificacaoHandler(pedidoRepository, notificacaoService));
        eventBus.register(new PagamentoAprovadoMetricasHandler(metricas));
        eventBus.register(new PagamentoRecusadoMetricasHandler(metricas));

        PedidoService pedidoService = new PedidoService(estoqueService, pedidoRepository, pagamentoGateway, eventBus);

                        //==CATALOGO==//

        Produto mouse = new Produto("sku-1", "Mouse sem fio", new BigDecimal("120.00"));
        Produto teclado = new Produto("sku-2", "Teclado mecanico", new BigDecimal("350.00"));
        Produto monitor = new Produto("sku-3", "Monitor 27 pol", new BigDecimal("1800.00"));

        estoqueService.adicionarEstoque(mouse,10);
        estoqueService.adicionarEstoque(teclado,6);
        estoqueService.adicionarEstoque(monitor,2);

        Cliente ana = new Cliente("Ana", "ana@email.com");
        Cliente bruno = new Cliente("Bruno", "bruno@email.com");

                         //==PEDIDOS==//

        //pedido 1 - aprovado
        Pedido p1 = new Pedido(ana);
        p1.adicionarItem(new ItemPedido(mouse, 2));
        p1.adicionarItem(new ItemPedido(teclado, 1));

        //pedido 2 - não aprovado (maior que 5k)
        Pedido p2 = new Pedido(bruno);
        p2.adicionarItem(new ItemPedido(monitor, 2));
        p2.adicionarItem(new ItemPedido(teclado, 5));

        //pedido 3 - não aprovado (estoque insuficiente)
        Pedido p3 = new Pedido(bruno);
        p3.adicionarItem(new ItemPedido(monitor, 5)); //só existe 2 monitores no estoque


        long inicio = System.currentTimeMillis();

        processar(pedidoService, p1);
        processar(pedidoService, p2);
        processar(pedidoService, p3);

        long fim = System.currentTimeMillis();

        System.out.println();
        System.out.println("Pedidos no repositorio: " + pedidoRepository.findAll().size());
        System.out.println(metricas.resumo());
        System.out.printf("Tempo total: %.1f s\n", (fim - inicio)/1000.0);
    }

    private static void processar(PedidoService service, Pedido pedido){
        System.out.println();
        System.out.println("Processando" + pedido + " ...");
        Pedido resultado = service.processar(pedido);
        System.out.println(" Resultado: " + resultado);
    }
}
