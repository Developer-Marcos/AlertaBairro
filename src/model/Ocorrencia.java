package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import util.ValidacaoException;

public abstract class Ocorrencia implements Alertavel {
    private int id;
    private String descricao;
    private LocalDateTime dataHora;
    private int severidade;
    private Cidadao cidadao;
    private List<Abrigo> abrigos = new ArrayList<>();

    protected Ocorrencia(int id, String descricao, LocalDateTime dataHora, int severidade, Cidadao cidadao) {
        this.id = id;
        this.dataHora = dataHora;
        setDescricao(descricao);
        setSeveridade(severidade);
        setCidadao(cidadao);
    }

    public abstract String getTipo();

    public static Ocorrencia nova(String tipo, int id, String descricao, LocalDateTime dataHora, int severidade, Cidadao cidadao) {
        if ("ALAGAMENTO".equals(tipo)) return new Alagamento(id, descricao, dataHora, severidade, cidadao);
        if ("DESLIZAMENTO".equals(tipo)) return new Deslizamento(id, descricao, dataHora, severidade, cidadao);
        throw new ValidacaoException("Tipo deve ser ALAGAMENTO ou DESLIZAMENTO.");
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) throw new ValidacaoException("Descricao e obrigatoria.");
        if (descricao.length() > 300) throw new ValidacaoException("Descricao passa de 300 caracteres.");
        this.descricao = descricao.trim();
    }

    public void setSeveridade(int severidade) {
        if (severidade < 1 || severidade > 5) throw new ValidacaoException("Severidade deve ser de 1 a 5.");
        this.severidade = severidade;
    }

    public void setCidadao(Cidadao cidadao) {
        if (cidadao == null) throw new ValidacaoException("Ocorrencia precisa de um cidadao.");
        this.cidadao = cidadao;
    }

    public void ativarAbrigo(Abrigo abrigo) {
        if (abrigo == null) throw new ValidacaoException("Abrigo invalido.");
        if (abrigos.contains(abrigo)) throw new ValidacaoException("Abrigo ja ativado nesta ocorrencia.");
        abrigos.add(abrigo);
    }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataHora() { return dataHora; }
    public int getSeveridade() { return severidade; }
    public Cidadao getCidadao() { return cidadao; }
    public List<Abrigo> getAbrigos() { return abrigos; }

    @Override
    public String toString() {
        return id + " | " + getTipo() + " | sev " + severidade + " | " + descricao;
    }
}
