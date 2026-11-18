package ui;

import util.Conexao;

public class Main {
    public static void main(String[] args) throws Exception {
        Conexao.inicializar();
        int porta = 8080;
        new Servidor().iniciar(porta);
        System.out.println("AlertaBairro rodando em http://localhost:" + porta);
        System.out.println("Para encerrar, feche esta janela ou aperte Ctrl+C.");
    }
}
