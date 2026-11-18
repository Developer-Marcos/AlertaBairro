package model;

import java.time.LocalDateTime;

public class Alagamento extends Ocorrencia {

    public Alagamento(int id, String descricao, LocalDateTime dataHora, int severidade, Cidadao cidadao) {
        super(id, descricao, dataHora, severidade, cidadao);
    }

    public Alagamento(String descricao, int severidade, Cidadao cidadao) {
        super(0, descricao, LocalDateTime.now(), severidade, cidadao);
    }

    @Override
    public String getTipo() { return "ALAGAMENTO"; }

    @Override
    public String gerarAlerta() {
        String bairro = getCidadao().getBairro();
        if (getSeveridade() >= 4) {
            return "ALERTA CRITICO: alagamento grave em " + bairro + ". Procure um abrigo e evite areas baixas.";
        }
        return "Atencao: alagamento em " + bairro + ". Evite transitar pela regiao.";
    }
}
