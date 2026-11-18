package service;

import dao.AbrigoDAO;
import dao.CidadaoDAO;
import dao.OcorrenciaDAO;
import dao.RelatorioDAO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import model.Abrigo;
import model.Cidadao;
import model.Ocorrencia;
import util.ValidacaoException;

public class OcorrenciaService {
    private final OcorrenciaDAO dao = new OcorrenciaDAO();
    private final CidadaoDAO cidadaoDao = new CidadaoDAO();
    private final AbrigoDAO abrigoDao = new AbrigoDAO();
    private final RelatorioDAO relatorioDao = new RelatorioDAO();

    public Ocorrencia cadastrar(String tipo, String descricao, int severidade, int idCidadao) {
        Cidadao c = buscarCidadao(idCidadao);
        Ocorrencia o = Ocorrencia.nova(tipo, 0, descricao, LocalDateTime.now(), severidade, c);
        dao.inserir(o);
        return o;
    }

    public List<Ocorrencia> listar() {
        return dao.listar();
    }

    public Ocorrencia buscar(int id) {
        Ocorrencia o = dao.buscarPorId(id);
        if (o == null) throw new ValidacaoException("Ocorrencia nao encontrada.");
        return o;
    }

    public Ocorrencia atualizar(int id, String tipo, String descricao, int severidade, int idCidadao) {
        Ocorrencia atual = buscar(id);
        Cidadao c = buscarCidadao(idCidadao);
        Ocorrencia o = Ocorrencia.nova(tipo, id, descricao, atual.getDataHora(), severidade, c);
        dao.atualizar(o);
        return buscar(id);
    }

    public void excluir(int id) {
        if (!dao.excluir(id)) throw new ValidacaoException("Ocorrencia nao encontrada.");
    }

    public Ocorrencia ativarAbrigo(int idOcorrencia, int idAbrigo) {
        Ocorrencia o = buscar(idOcorrencia);
        Abrigo a = abrigoDao.buscarPorId(idAbrigo);
        if (a == null) throw new ValidacaoException("Abrigo nao encontrado.");
        if (a.getVagasDisponiveis() == 0) throw new ValidacaoException("Abrigo sem vagas disponiveis.");
        o.ativarAbrigo(a);
        dao.ativarAbrigo(idOcorrencia, idAbrigo);
        return buscar(idOcorrencia);
    }

    public Ocorrencia desativarAbrigo(int idOcorrencia, int idAbrigo) {
        buscar(idOcorrencia);
        if (!dao.desativarAbrigo(idOcorrencia, idAbrigo)) throw new ValidacaoException("Esse abrigo nao estava ativado nesta ocorrencia.");
        return buscar(idOcorrencia);
    }

    public List<Map<String, Object>> relatorioPorBairro() {
        return relatorioDao.ocorrenciasPorBairro();
    }

    public List<Map<String, Object>> relatorioAbrigos() {
        return relatorioDao.abrigosMaisAcionados();
    }

    public List<Map<String, Object>> relatorioPorTipo() {
        return relatorioDao.ocorrenciasPorTipo();
    }

    private Cidadao buscarCidadao(int id) {
        Cidadao c = cidadaoDao.buscarPorId(id);
        if (c == null) throw new ValidacaoException("Cidadao nao encontrado.");
        return c;
    }
}
