package com.biblioteca.dao;

import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.StatusEmprestimo;
import com.biblioteca.utils.DatabaseConnection;
import com.biblioteca.utils.LogManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object para operações de empréstimos.
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class EmprestimoDAO {
    
    public boolean inserir(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimos (usuario_id, livro_id, data_emprestimo, data_devolucao_prevista, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, emprestimo.getUsuarioId());
            stmt.setInt(2, emprestimo.getLivroId());
            stmt.setDate(3, Date.valueOf(emprestimo.getDataEmprestimo()));
            stmt.setDate(4, Date.valueOf(emprestimo.getDataDevolucaoPrevista()));
            stmt.setString(5, emprestimo.getStatus().name());
            if (stmt.executeUpdate() > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) emprestimo.setId(keys.getInt(1));
                }
                LogManager.info("Empréstimo criado: ID " + emprestimo.getId());
                return true;
            }
        } catch (SQLException e) { LogManager.error("Erro ao inserir empréstimo", e); }
        return false;
    }
    
    public boolean devolver(int emprestimoId) {
        String sql = "UPDATE emprestimos SET data_devolucao_real = ?, status = 'DEVOLVIDO' WHERE id = ? AND status != 'DEVOLVIDO'";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, emprestimoId);
            if (stmt.executeUpdate() > 0) {
                LogManager.info("Empréstimo devolvido: ID " + emprestimoId);
                return true;
            }
        } catch (SQLException e) { LogManager.error("Erro ao devolver empréstimo", e); }
        return false;
    }
    
    public Emprestimo buscarPorId(int id) {
        String sql = "SELECT e.*, u.nome as usuario_nome, u.email as usuario_email, l.titulo as livro_titulo, l.autor as livro_autor " +
                     "FROM emprestimos e INNER JOIN usuarios u ON e.usuario_id = u.id INNER JOIN livros l ON e.livro_id = l.id WHERE e.id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (SQLException e) { LogManager.error("Erro ao buscar empréstimo", e); }
        return null;
    }
    
    public List<Emprestimo> listarTodos() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT e.*, u.nome as usuario_nome, u.email as usuario_email, l.titulo as livro_titulo, l.autor as livro_autor " +
                     "FROM emprestimos e INNER JOIN usuarios u ON e.usuario_id = u.id INNER JOIN livros l ON e.livro_id = l.id ORDER BY e.data_emprestimo DESC";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapResultSet(rs));
        } catch (SQLException e) { LogManager.error("Erro ao listar empréstimos", e); }
        return lista;
    }
    
    public List<Emprestimo> listarAtivos() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT e.*, u.nome as usuario_nome, u.email as usuario_email, l.titulo as livro_titulo, l.autor as livro_autor " +
                     "FROM emprestimos e INNER JOIN usuarios u ON e.usuario_id = u.id INNER JOIN livros l ON e.livro_id = l.id " +
                     "WHERE e.status IN ('ATIVO', 'ATRASADO') ORDER BY e.data_devolucao_prevista";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapResultSet(rs));
        } catch (SQLException e) { LogManager.error("Erro ao listar empréstimos ativos", e); }
        return lista;
    }
    
    public List<Emprestimo> listarAtrasados() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT e.*, u.nome as usuario_nome, u.email as usuario_email, l.titulo as livro_titulo, l.autor as livro_autor " +
                     "FROM emprestimos e INNER JOIN usuarios u ON e.usuario_id = u.id INNER JOIN livros l ON e.livro_id = l.id " +
                     "WHERE e.status != 'DEVOLVIDO' AND e.data_devolucao_prevista < CURDATE() ORDER BY e.data_devolucao_prevista";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapResultSet(rs));
        } catch (SQLException e) { LogManager.error("Erro ao listar empréstimos atrasados", e); }
        return lista;
    }
    
    public List<Emprestimo> listarProximosVencimento(int dias) {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT e.*, u.nome as usuario_nome, u.email as usuario_email, l.titulo as livro_titulo, l.autor as livro_autor " +
                     "FROM emprestimos e INNER JOIN usuarios u ON e.usuario_id = u.id INNER JOIN livros l ON e.livro_id = l.id " +
                     "WHERE e.status = 'ATIVO' AND e.data_devolucao_prevista BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY)";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dias);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapResultSet(rs));
            }
        } catch (SQLException e) { LogManager.error("Erro ao listar próximos vencimento", e); }
        return lista;
    }
    
    public List<Emprestimo> listarPorUsuario(int usuarioId) {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT e.*, u.nome as usuario_nome, u.email as usuario_email, l.titulo as livro_titulo, l.autor as livro_autor " +
                     "FROM emprestimos e INNER JOIN usuarios u ON e.usuario_id = u.id INNER JOIN livros l ON e.livro_id = l.id " +
                     "WHERE e.usuario_id = ? ORDER BY e.data_emprestimo DESC";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapResultSet(rs));
            }
        } catch (SQLException e) { LogManager.error("Erro ao listar empréstimos por usuário", e); }
        return lista;
    }
    
    public int atualizarStatusAtrasados() {
        String sql = "UPDATE emprestimos SET status = 'ATRASADO' WHERE status = 'ATIVO' AND data_devolucao_prevista < CURDATE()";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) { LogManager.error("Erro ao atualizar status", e); }
        return 0;
    }
    
    public List<Map<String, Object>> usuariosMaisEmprestimos(int limite) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        String sql = "SELECT u.nome, u.email, u.tipo, COUNT(e.id) as total_emprestimos FROM usuarios u " +
                     "LEFT JOIN emprestimos e ON u.id = e.usuario_id GROUP BY u.id ORDER BY total_emprestimos DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("nome", rs.getString("nome"));
                    row.put("email", rs.getString("email"));
                    row.put("tipo", rs.getString("tipo"));
                    row.put("total_emprestimos", rs.getInt("total_emprestimos"));
                    resultado.add(row);
                }
            }
        } catch (SQLException e) { LogManager.error("Erro ao buscar usuários mais empréstimos", e); }
        return resultado;
    }
    
    public int contarAtivos() {
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM emprestimos WHERE status IN ('ATIVO', 'ATRASADO')")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { LogManager.error("Erro ao contar ativos", e); }
        return 0;
    }
    
    private Emprestimo mapResultSet(ResultSet rs) throws SQLException {
        Emprestimo emp = new Emprestimo();
        emp.setId(rs.getInt("id"));
        emp.setUsuarioId(rs.getInt("usuario_id"));
        emp.setLivroId(rs.getInt("livro_id"));
        emp.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
        emp.setDataDevolucaoPrevista(rs.getDate("data_devolucao_prevista").toLocalDate());
        Date devolucaoReal = rs.getDate("data_devolucao_real");
        if (devolucaoReal != null) emp.setDataDevolucaoReal(devolucaoReal.toLocalDate());
        emp.setStatus(StatusEmprestimo.valueOf(rs.getString("status")));
        try {
            emp.setUsuarioNome(rs.getString("usuario_nome"));
            emp.setUsuarioEmail(rs.getString("usuario_email"));
            emp.setLivroTitulo(rs.getString("livro_titulo"));
            emp.setLivroAutor(rs.getString("livro_autor"));
        } catch (SQLException e) {}
        return emp;
    }
}
