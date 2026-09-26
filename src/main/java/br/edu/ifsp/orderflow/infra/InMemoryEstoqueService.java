package br.edu.ifsp.orderflow.infra;

import br.edu.ifsp.orderflow.domain.ItemPedido;
import br.edu.ifsp.orderflow.domain.Pedido;
import br.edu.ifsp.orderflow.domain.Produto;
import br.edu.ifsp.orderflow.service.IEstoqueService;

import java.util.HashMap;
import java.util.Map;

public class InMemoryEstoqueService implements IEstoqueService {

    private final Map<String, Integer> estoque = new HashMap<>();

    @Override
    public void adicionarEstoque(Produto produto, int quantidade) {
        int qtdAtual = this.estoque.getOrDefault(produto.getId(), 0);
        this.estoque.put(produto.getId(), quantidade + qtdAtual);
    }

    @Override
    public int quantidadeDisponivel(Produto produto) {

        return this.estoque.getOrDefault(produto.getId(), 0);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean reservar(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            int disponivel = quantidadeDisponivel(item.getProduto());
            if (disponivel < item.getQuantidade()) {
                return false;
            }
        }

        this.sleep(50);

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            String produtoId = produto.getId();
            int qtdAtual = this.estoque.getOrDefault(produtoId, 0);
            this.estoque.put(produtoId, qtdAtual - item.getQuantidade());
        }
        return true;
    }

    @Override
    public void liberar(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            adicionarEstoque(item.getProduto(), item.getQuantidade());
        }
    }
}
