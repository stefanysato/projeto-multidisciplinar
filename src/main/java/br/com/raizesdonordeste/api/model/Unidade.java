package br.com.raizesdonordeste.api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "unidades")
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 20)
    private String telefone;

    @Column(nullable = false, length = 150)
    private String logradouro;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(nullable = false, length = 100)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(nullable = false)
    private boolean ativa = true;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private final LocalDateTime criadaEm =  LocalDateTime.now();

    // construtor exigido pelo JPA
    protected Unidade() {}

    public Unidade(String nome, String telefone, String logradouro, String numero, String bairro, String cidade, String estado, String cep) {
        this.nome = nome;
        this.telefone = telefone;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public void ativar() {
        this.ativa = true;
    }

    public void desativar() {
        this.ativa = false;
    }

    public void atualizarDados(
            String nome,
            String telefone,
            String logradouro,
            String numero,
            String bairro,
            String cidade,
            String estado,
            String cep
    ) {
        this.nome = nome;
        this.telefone = telefone;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getCep() {
        return cep;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }
}
