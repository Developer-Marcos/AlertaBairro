package service;

import dao.CidadaoDAO;
import java.util.List;
import model.Cidadao;
import util.ValidacaoException;

public class CidadaoService {
    private final CidadaoDAO dao = new CidadaoDAO();

    public Cidadao cadastrar(String nome, String email, String telefone, String endereco, String bairro) {
        Cidadao c = new Cidadao(nome, email, telefone, endereco, bairro);
        dao.inserir(c);
        return c;
    }

    public List<Cidadao> listar() {
        return dao.listar();
    }

    public Cidadao buscar(int id) {
        Cidadao c = dao.buscarPorId(id);
        if (c == null) throw new ValidacaoException("Cidadao nao encontrado.");
        return c;
    }

    public Cidadao atualizar(int id, String nome, String email, String telefone, String endereco, String bairro) {
        buscar(id);
        Cidadao c = new Cidadao(id, nome, email, telefone, endereco, bairro);
        dao.atualizar(c);
        return c;
    }

    public void excluir(int id) {
        if (!dao.excluir(id)) throw new ValidacaoException("Cidadao nao encontrado.");
    }
}
