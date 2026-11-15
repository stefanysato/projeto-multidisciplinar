package br.com.raizesdonordeste.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_un", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    protected ItemPedido() {}

    public ItemPedido(
            Pedido pedido,
            Produto produto,
            Integer quantidade,
            BigDecimal precoUnitario) {
        this.pedido = pedido;
        this.produto = produto;

        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior do que zero.");
        }

        if (precoUnitario.signum() <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior do que zero.");
        }

        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    void desvincularPedido() {
        this.pedido = null;
    }

    public BigDecimal calcularSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
}
