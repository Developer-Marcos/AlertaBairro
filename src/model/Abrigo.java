package model;

import util.ValidacaoException;

public class Abrigo {
    private int id;
    private String nome;
    private int capacidade;
    private int vagasDisponiveis;
    private String logradouro;
    private String bairro;

    public Abrigo(int id, String nome, int capacidade, int vagasDisponiveis, String logradouro, String bairro) {
        if (capacidade <= 0) throw new ValidacaoException("Capacidade deve ser maior que zero.");
        if (vagasDisponiveis < 0 || vagasDisponiveis > capacidade)
            throw new ValidacaoException("Vagas devem estar entre 0 e a capacidade.");
        if (nome == null || nome.isBlank()) throw new ValidacaoException("Nome e obrigatorio.");
        if (logradouro == null || logradouro.isBlank()) throw new ValidacaoException("Logradouro e obrigatorio.");
        if (bairro == null || bairro.isBlank()) throw new ValidacaoException("Bairro e obrigatorio.");
        this.id = id;
        this.nome = nome.trim();
        this.capacidade = capacidade;
        this.vagasDisponiveis = vagasDisponiveis;
        this.logradouro = logradouro.trim();
        this.bairro = bairro.trim();
    }

    public Abrigo(String nome, int capacidade, int vagasDisponiveis, String logradouro, String bairro) {
        this(0, nome, capacidade, vagasDisponiveis, logradouro, bairro);
    }

    public void ocuparVagas(int qtd) {
        if (qtd <= 0) throw new ValidacaoException("Quantidade deve ser positiva.");
        if (qtd > vagasDisponiveis)
            throw new ValidacaoException("So restam " + vagasDisponiveis + " vagas em " + nome + ".");
        vagasDisponiveis -= qtd;
    }

    public void liberarVagas(int qtd) {
        if (qtd <= 0) throw new ValidacaoException("Quantidade deve ser positiva.");
        if (vagasDisponiveis + qtd > capacidade)
            throw new ValidacaoException("Nao pode liberar mais vagas que a capacidade.");
        vagasDisponiveis += qtd;
    }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public int getVagasDisponiveis() { return vagasDisponiveis; }
    public String getLogradouro() { return logradouro; }
    public String getBairro() { return bairro; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Abrigo a && a.id == id && id != 0;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return id + " | " + nome + " | " + vagasDisponiveis + "/" + capacidade + " vagas | " + bairro;
    }
}
