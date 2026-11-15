package br.com.raizesdonordeste.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "estoque_unidade", uniqueConstraints = {
        @UniqueConstraint(
                name = "estoque_produto_unidade",
                columnNames = {"produto_id", "unidade_id"}
        )
})
public class EstoqueUnidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @Column(nullable = false)
    private int quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    protected EstoqueUnidade() {}

    public EstoqueUnidade(
            Produto produto,
            Unidade unidade,
            int quantidade,
            BigDecimal preco
    ) {
        this.produto = Objects.requireNonNull(produto);
        this.unidade = Objects.requireNonNull(unidade);

        if (quantidade < 0) {
            throw new IllegalArgumentException(
                    "A quantidade não pode ser negativa"
            );
        }

        validarPreco(preco);

        this.quantidade = quantidade;
        this.preco = preco;
    }

    public void adicionarEstoque(int quantidade) {
        validarQuantidade(quantidade);
        this.quantidade += quantidade;
    }

    public void retirarEstoque(int quantidade) {
        validarQuantidade(quantidade);

        if (quantidade > this.quantidade) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente"
            );
        }

        this.quantidade -= quantidade;
    }

    public void atualizarPreco(BigDecimal novoPreco) {
        validarPreco(novoPreco);
        this.preco = novoPreco;
    }

    private void validarPreco(BigDecimal preco) {
        Objects.requireNonNull(preco, "O preço é obrigatório");

        if (preco.signum() < 0) {
            throw new IllegalArgumentException(
                    "O preço não pode ser negativo"
            );
        }
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade adicionada deve ser positiva"
            );
        }
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPreco() {
        return preco;
    }
}
