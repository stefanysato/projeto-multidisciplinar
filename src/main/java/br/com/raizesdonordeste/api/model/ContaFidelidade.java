package br.com.raizesdonordeste.api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contas_fidelidade")
public class ContaFidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario titular;

    @Column(name = "saldo_pontos", nullable = false)
    private int saldoPontos = 0;

    @Column(name="criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected ContaFidelidade() {}

    public ContaFidelidade(Usuario titular) {
        this.titular = titular;

        if (titular.getRole() != Role.CLIENTE) {
            throw new IllegalArgumentException(
                    "Somente clientes podem ter conta de fidelidade."
            );
        }

        this.saldoPontos = 0;
        this.criadoEm = LocalDateTime.now();
    }

    public void adicionarPontos(int pontos) {
        validarQuantidade(pontos);

        this.saldoPontos += pontos;
    }

    public void resgatarPontos(int pontos) {
        validarQuantidade(pontos);

        if (pontos > this.saldoPontos) {
            throw new IllegalArgumentException(
                    "Saldo de pontos insuficiente."
            );
        }

        this.saldoPontos -= pontos;
    }

    private void validarQuantidade(int pontos) {
        if (pontos <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de pontos deve ser maior que zero."
            );
        }
    }

    public Long getId() {
        return id;
    }

    public Usuario getTitular() {
        return titular;
    }

    public int getSaldoPontos() {
        return saldoPontos;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
