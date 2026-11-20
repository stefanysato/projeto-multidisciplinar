
package br.com.raizesdonordeste.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected Pagamento() {}

    public Pagamento(Pedido pedido, BigDecimal valor) {
        this.pedido = pedido;
        this.valor = valor;
        this.status = StatusPagamento.PENDENTE;
        this.criadoEm = LocalDateTime.now();
    }

    public void aprovar() {
        this.status = StatusPagamento.APROVADO;
    }

    public void recusar() {
        this.status = StatusPagamento.RECUSADO;
    }

    public void estornar() {
        this.status = StatusPagamento.ESTORNADO;
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
