package model;

import util.ValidacaoException;

public class Cidadao {
    private int id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private String bairro;

    public Cidadao(int id, String nome, String email, String telefone, String endereco, String bairro) {
        this.id = id;
        setNome(nome);
        setEmail(email);
        setTelefone(telefone);
        setEndereco(endereco);
        setBairro(bairro);
    }

    public Cidadao(String nome, String email, String telefone, String endereco, String bairro) {
        this(0, nome, email, telefone, endereco, bairro);
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) throw new ValidacaoException("Nome e obrigatorio.");
        this.nome = nome.trim();
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) throw new ValidacaoException("E-mail invalido.");
        this.email = email.trim().toLowerCase();
    }

    public void setTelefone(String telefone) {
        if (telefone == null || telefone.replaceAll("\\D", "").length() < 10)
            throw new ValidacaoException("Telefone deve ter pelo menos 10 digitos.");
        this.telefone = telefone.trim();
    }

    public void setEndereco(String endereco) {
        if (endereco == null || endereco.isBlank()) throw new ValidacaoException("Endereco e obrigatorio.");
        this.endereco = endereco.trim();
    }

    public void setBairro(String bairro) {
        if (bairro == null || bairro.isBlank()) throw new ValidacaoException("Bairro e obrigatorio.");
        this.bairro = bairro.trim();
    }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getEndereco() { return endereco; }
    public String getBairro() { return bairro; }

    @Override
    public String toString() {
        return id + " | " + nome + " | " + email + " | " + bairro;
    }
}
