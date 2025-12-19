package com.biblioteca.dao;

import com.biblioteca.model.Livro;
import com.biblioteca.utils.DatabaseConnection;
import com.biblioteca.utils.LogManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object para operações CRUD de livros.
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class LivroDAO {
    
    public boolean inserir(Livro livro) {
        String sql = "INSERT INTO livros (titulo, autor, isbn, categoria_id, quantidade_total, quantidade_disponivel) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setString(3, livro.getIsbn());
            stmt.setInt(4, livro.getCategoriaId());
            stmt.setInt(5, livro.getQuantidadeTotal());
            stmt.setInt(6, livro.getQuantidadeDisponivel());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) livro.setId(generatedKeys.getInt(1));
                }
                LogManager.info("Livro inserido: " + livro.getTitulo());
                return true;
            }
        } catch (SQLException e) { LogManager.error("Erro ao inserir livro", e); }
        return false;
    }
    
    public boolean atualizar(Livro livro) {
        String sql = "UPDATE livros SET titulo=?, autor=?, isbn=?, categoria_id=?, quantidade_total=?, quantidade_disponivel=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setString(3, livro.getIsbn());
            stmt.setInt(4, livro.getCategoriaId());
            stmt.setInt(5, livro.getQuantidadeTotal());
            stmt.setInt(6, livro.getQuantidadeDisponivel());
            stmt.setInt(7, livro.getId());
            if (stmt.executeUpdate() > 0) {
                LogManager.info("Livro atualizado: " + livro.getTitulo());
                return true;
            }
        } catch (SQLException e) { LogManager.error("Erro ao atualizar livro", e); }
        return false;
    }
    
    public boolean deletar(int id) {
        String sql = "DELETE FROM livros WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() > 0) {
                LogManager.info("Livro removido: ID " + id);
                return true;
            }
        } catch (SQLException e) { LogManager.error("Erro ao deletar livro", e); }
        return false;
    }
    
    public Livro buscarPorId(int id) {
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l INNER JOIN categorias c ON l.categoria_id = c.id WHERE l.id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToLivro(rs);
            }
        } catch (SQLException e) { LogManager.error("Erro ao buscar livro por ID", e); }
        return null;
    }
    
    public Livro buscarPorIsbn(String isbn) {
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l INNER JOIN categorias c ON l.categoria_id = c.id WHERE l.isbn = ?";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToLivro(rs);
            }
        } catch (SQLException e) { LogManager.error("Erro ao buscar livro por ISBN", e); }
        return null;
    }
    
    public List<Livro> listarTodos() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l INNER JOIN categorias c ON l.categoria_id = c.id ORDER BY l.titulo";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) livros.add(mapResultSetToLivro(rs));
        } catch (SQLException e) { LogManager.error("Erro ao listar livros", e); }
        return livros;
    }
    
    public List<Livro> listarDisponiveis() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l INNER JOIN categorias c ON l.categoria_id = c.id WHERE l.quantidade_disponivel > 0 ORDER BY l.titulo";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) livros.add(mapResultSetToLivro(rs));
        } catch (SQLException e) { LogManager.error("Erro ao listar livros disponíveis", e); }
        return livros;
    }
    
    public List<Livro> listarPorCategoria(int categoriaId) {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l INNER JOIN categorias c ON l.categoria_id = c.id WHERE l.categoria_id = ? ORDER BY l.titulo";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoriaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) livros.add(mapResultSetToLivro(rs));
            }
        } catch (SQLException e) { LogManager.error("Erro ao listar livros por categoria", e); }
        return livros;
    }
    
    public List<Livro> buscarPorTitulo(String titulo) {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.*, c.nome as categoria_nome FROM livros l INNER JOIN categorias c ON l.categoria_id = c.id WHERE l.titulo LIKE ? ORDER BY l.titulo";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + titulo + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) livros.add(mapResultSetToLivro(rs));
            }
        } catch (SQLException e) { LogManager.error("Erro ao buscar livros por título", e); }
        return livros;
    }
    
    public boolean decrementarDisponivel(int livroId) {
        String sql = "UPDATE livros SET quantidade_disponivel = quantidade_disponivel - 1 WHERE id = ? AND quantidade_disponivel > 0";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId);
            if (stmt.executeUpdate() > 0) return true;
        } catch (SQLException e) { LogManager.error("Erro ao decrementar quantidade", e); }
        return false;
    }
    
    public boolean incrementarDisponivel(int livroId) {
        String sql = "UPDATE livros SET quantidade_disponivel = quantidade_disponivel + 1 WHERE id = ? AND quantidade_disponivel < quantidade_total";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId);
            if (stmt.executeUpdate() > 0) return true;
        } catch (SQLException e) { LogManager.error("Erro ao incrementar quantidade", e); }
        return false;
    }
    
    public List<Map<String, Object>> livrosMaisEmprestados(int limite) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        String sql = "SELECT l.titulo, l.autor, COUNT(e.id) as total_emprestimos FROM livros l LEFT JOIN emprestimos e ON l.id = e.livro_id GROUP BY l.id ORDER BY total_emprestimos DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("titulo", rs.getString("titulo"));
                    row.put("autor", rs.getString("autor"));
                    row.put("total_emprestimos", rs.getInt("total_emprestimos"));
                    resultado.add(row);
                }
            }
        } catch (SQLException e) { LogManager.error("Erro ao buscar livros mais emprestados", e); }
        return resultado;
    }
    
    public boolean isbnExiste(String isbn) { return buscarPorIsbn(isbn) != null; }
    public int contarTotal() {
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM livros")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { LogManager.error("Erro ao contar livros", e); }
        return 0;
    }
    
    private Livro mapResultSetToLivro(ResultSet rs) throws SQLException {
        Livro livro = new Livro();
        livro.setId(rs.getInt("id"));
        livro.setTitulo(rs.getString("titulo"));
        livro.setAutor(rs.getString("autor"));
        livro.setIsbn(rs.getString("isbn"));
        livro.setCategoriaId(rs.getInt("categoria_id"));
        livro.setQuantidadeTotal(rs.getInt("quantidade_total"));
        livro.setQuantidadeDisponivel(rs.getInt("quantidade_disponivel"));
        try { livro.setCategoriaNome(rs.getString("categoria_nome")); } catch (SQLException e) {}
        Timestamp criadoEm = rs.getTimestamp("criado_em");
        if (criadoEm != null) livro.setCriadoEm(criadoEm.toLocalDateTime());
        return livro;
    }
}
