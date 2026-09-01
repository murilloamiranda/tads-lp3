package br.edu.ifsp.orderflow.infra;

import br.edu.ifsp.orderflow.domain.ItemPedido;
import br.edu.ifsp.orderflow.domain.Pedido;
import br.edu.ifsp.orderflow.domain.Produto;
import br.edu.ifsp.orderflow.service.IEstoqueService;

import java.util.HashMap;
import java.util.Map;

public class InMemoryEstoqueService implements IEstoqueService {

    private final Map<String, Integer> estoque = new HashMap<>(); // estrutura de dados que armazena por meio de chave (string) e valor (Integer)

    @Override
    public void adicionarEstoque(Produto produto, int quantidade) {
        int qtdAtual = this.estoque.getOrDefault(produto.getId(),0);
        this.estoque.put(produto.getId(), quantidade + qtdAtual); // colocar no estoque uma quantidade

    }

    @Override
    public int quantidadeDisponivel(Produto produto) {
        return this.estoque.getOrDefault(produto.getId(), 0);
    }

    private void sleep (long millis){
        try {
             Thread.sleep(millis);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean reservar(Pedido pedido) {
        // Conferir se todos os produtos têm estoque
        for (ItemPedido item : pedido.getItens()) {

            int disponivel = this.quantidadeDisponivel(item.getProduto());

            // só vai reservar quando um item tiver disponivel no estoque
            if (item.getQuantidade() > disponivel) {
                return false;
            }

            this.sleep( 50);
        }

        /*
        List<ItemPedido> itens = pedido.getItens();

        for (int i = 0; i < itens.size(); i++) {
            ItemPedido item = itens.get(i);

            // olhar cada item do pedido, checar se a quantidade disponivel for menor, não é possível reservar
            if (item.getQuantidade() > this.quantidadeDisponivel(item.getProduto())){
                return false;
            }
          }
        */
        for (ItemPedido item : pedido.getItens()) {

            // String produtoId = item.getProduto().getId();
            // relaciona o produto com a quantidade que o pedido requer

            Produto produto = item.getProduto();
            String produtoId = produto.getId();
            int quantidadeAtual = this.estoque.getOrDefault(produtoId, 0); // HASHMAP - pegar o produto padrão
            this.estoque.put(produtoId, quantidadeAtual - item.getQuantidade());
        }
        return true;
    }

    @Override
    public void liberar(Pedido pedido) { // devolve para o estoque - falha no pagamento/cancelamento
        for (ItemPedido item : pedido.getItens()) {
            this.adicionarEstoque(item.getProduto(), item.getQuantidade()); // metodo adicionarEstoque
        }
        /*
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            String produtoId = produto.getId();

            int quantidadeAtual = this.estoque.getOrDefault(produtoId, 0); // HASHMAP
            this.estoque.put(produtoId, quantidadeAtual + item.getQuantidade());

        }
         */
    }
}