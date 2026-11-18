package model;

import java.time.LocalDateTime;

public class Deslizamento extends Ocorrencia {

      public Deslizamento(int id, String descricao, LocalDateTime dataHora, int severidade, Cidadao cidadao) {
            super(id, descricao, dataHora, severidade, cidadao);
      }

      public Deslizamento(String descricao, int severidade, Cidadao cidadao) {
            super(0, descricao, LocalDateTime.now(), severidade, cidadao);
      }

      @Override
      public String getTipo() { return "DESLIZAMENTO"; }

      @Override
      public String gerarAlerta() {
            String bairro = getCidadao().getBairro();
            if (getSeveridade() >= 4) {
                  return "ALERTA CRITICO: risco de deslizamento em " + bairro + ". Saia de encostas imediatamente.";
            }
            return "Atencao: sinais de deslizamento em " + bairro + ". Fique longe de barrancos.";
      }
}
