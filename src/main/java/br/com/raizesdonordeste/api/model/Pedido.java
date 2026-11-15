package br.com.raizesdonordeste.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id", nullable = false)
    private Usuario registradoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_fidelidade_id")
    private ContaFidelidade contaFidelidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemPedido> itens =  new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalPedido canal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected Pedido() {}

    public Pedido(Unidade unidade, Usuario registradoPor, ContaFidelidade contaFidelidade, CanalPedido canal) {
        this.unidade = unidade;
        this.registradoPor = registradoPor;
        this.contaFidelidade = contaFidelidade;
        this.canal = canal;
        this.status = StatusPedido.RECEBIDO;
        this.valorTotal = BigDecimal.ZERO;
        this.criadoEm = LocalDateTime.now();
    }

    public void adicionarItem(
            Produto produto,
            Integer quantidade,
            BigDecimal precoUnitario
    ) {
        ItemPedido item = new ItemPedido(
                this,
                produto,
                quantidade,
                precoUnitario
        );
        itens.add(item);
        calcularTotal();
    }

    public void removerItem(ItemPedido item) {
        itens.remove(item);
        item.desvincularPedido();
        calcularTotal();
    }

    private void calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedido item : itens) {
            total = total.add(item.calcularSubtotal());
        }

        this.valorTotal = total;
    }

    public void atualizarStatus(StatusPedido novoStatus) {
        boolean transicaoValida = switch (this.status) {
            case RECEBIDO ->
                    novoStatus == StatusPedido.EM_PREPARACAO ||
                            novoStatus == StatusPedido.CANCELADO;

            case EM_PREPARACAO ->
                    novoStatus == StatusPedido.PRONTO ||
                            novoStatus == StatusPedido.CANCELADO;

            case PRONTO ->
                    novoStatus == StatusPedido.ENTREGUE ||
                            novoStatus == StatusPedido.CANCELADO;

            case ENTREGUE, CANCELADO -> false;
        };

        if (!transicaoValida) {
            throw new IllegalStateException(
                    "Não é possível alterar o status de " + this.status + " para " + novoStatus + "."
            );
        }

        this.status = novoStatus;
    }

    public Long getId() {
        return id;
    }

    public Usuario getRegistradoPor() {
        return registradoPor;
    }

    public ContaFidelidade getContaFidelidade() {
        return contaFidelidade;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public CanalPedido getCanal() {
        return canal;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
