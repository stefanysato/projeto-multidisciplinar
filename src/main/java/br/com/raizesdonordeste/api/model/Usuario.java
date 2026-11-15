package br.com.raizesdonordeste.api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="usuarios")
public class Usuario {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name="senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name="criado_em", nullable = false, updatable = false)
    private final LocalDateTime criadoEm = LocalDateTime.now();

    // construtor exigido pelo JPA
    protected Usuario() {}

    // cadastro clientes
    public Usuario(
            String nome,
            String email,
            String senhaHash,
            Role role
    ) {
        this(nome, email, senhaHash, role, null);
    }

    // cadastro funcionários
    public Usuario(String nome, String email, String senhaHash, Role role, Unidade unidade) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.role = role;
        this.unidade = unidade;
    }

    public Unidade getUnidade() { return unidade; }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Role getRole() {
        return role;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }
}
