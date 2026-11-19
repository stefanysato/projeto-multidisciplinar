package br.com.raizesdonordeste.api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao_pontos")
public class MovimentacaoPontos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_fidelidade_id", nullable = false)
    private ContaFidelidade contaFidelidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private int pontos;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected MovimentacaoPontos() {}

    public MovimentacaoPontos(
            ContaFidelidade contaFidelidade,
            Pedido pedido,
            TipoMovimentacao tipo,
            int pontos
    ) {
        if (pontos <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de pontos deve ser maior que zero"
            );
        }
        this.contaFidelidade = contaFidelidade;
        this.pedido = pedido;
        this.tipo = tipo;
        this.pontos = pontos;
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public ContaFidelidade getContaFidelidade() {
        return contaFidelidade;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public int getPontos() {
        return pontos;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
