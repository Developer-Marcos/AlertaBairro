package service;

import dao.AbrigoDAO;
import java.util.List;
import model.Abrigo;
import util.ValidacaoException;

public class AbrigoService {
    private final AbrigoDAO dao = new AbrigoDAO();

    public Abrigo cadastrar(String nome, int capacidade, int vagas, String logradouro, String bairro) {
        Abrigo a = new Abrigo(nome, capacidade, vagas, logradouro, bairro);
        dao.inserir(a);
        return a;
    }

    public List<Abrigo> listar() {
        return dao.listar();
    }

    public Abrigo buscar(int id) {
        Abrigo a = dao.buscarPorId(id);
        if (a == null) throw new ValidacaoException("Abrigo nao encontrado.");
        return a;
    }

    public Abrigo atualizar(int id, String nome, int capacidade, int vagas, String logradouro, String bairro) {
        buscar(id);
        Abrigo a = new Abrigo(id, nome, capacidade, vagas, logradouro, bairro);
        dao.atualizar(a);
        return a;
    }

    public Abrigo ocuparVagas(int id, int quantidade) {
        Abrigo a = buscar(id);
        a.ocuparVagas(quantidade);
        dao.atualizar(a);
        return a;
    }

    public Abrigo liberarVagas(int id, int quantidade) {
        Abrigo a = buscar(id);
        a.liberarVagas(quantidade);
        dao.atualizar(a);
        return a;
    }

    public void excluir(int id) {
        if (!dao.excluir(id)) throw new ValidacaoException("Abrigo nao encontrado.");
    }
}
