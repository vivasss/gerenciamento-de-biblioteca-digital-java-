package com.biblioteca.dao;

import com.biblioteca.model.Categoria;
import com.biblioteca.utils.DatabaseConnection;
import com.biblioteca.utils.LogManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para operações CRUD de categorias.
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class CategoriaDAO {
    
    /**
     * Insere uma nova categoria no banco de dados.
     * 
     * @param categoria Categoria a ser inserida
     * @return true se inserido com sucesso
     */
    public boolean inserir(Categoria categoria) {
        String sql = "INSERT INTO categorias (nome, descricao) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categoria.setId(generatedKeys.getInt(1));
                    }
                }
                LogManager.info("Categoria inserida: " + categoria.getNome());
                return true;
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao inserir categoria", e);
        }
        
        return false;
    }
    
    /**
     * Atualiza uma categoria existente.
     * 
     * @param categoria Categoria a ser atualizada
     * @return true se atualizado com sucesso
     */
    public boolean atualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nome = ?, descricao = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setInt(3, categoria.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                LogManager.info("Categoria atualizada: " + categoria.getNome());
                return true;
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao atualizar categoria", e);
        }
        
        return false;
    }
    
    /**
     * Remove uma categoria pelo ID.
     * 
     * @param id ID da categoria
     * @return true se removido com sucesso
     */
    public boolean deletar(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                LogManager.info("Categoria removida: ID " + id);
                return true;
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao deletar categoria", e);
        }
        
        return false;
    }
    
    /**
     * Busca uma categoria pelo ID.
     * 
     * @param id ID da categoria
     * @return Categoria encontrada ou null
     */
    public Categoria buscarPorId(int id) {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategoria(rs);
                }
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao buscar categoria por ID", e);
        }
        
        return null;
    }
    
    /**
     * Busca uma categoria pelo nome.
     * 
     * @param nome Nome da categoria
     * @return Categoria encontrada ou null
     */
    public Categoria buscarPorNome(String nome) {
        String sql = "SELECT * FROM categorias WHERE nome = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategoria(rs);
                }
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao buscar categoria por nome", e);
        }
        
        return null;
    }
    
    /**
     * Lista todas as categorias.
     * 
     * @return Lista de categorias
     */
    public List<Categoria> listarTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nome";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categorias.add(mapResultSetToCategoria(rs));
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao listar categorias", e);
        }
        
        return categorias;
    }
    
    /**
     * Conta o número de livros em uma categoria.
     * 
     * @param categoriaId ID da categoria
     * @return Número de livros
     */
    public int contarLivros(int categoriaId) {
        String sql = "SELECT COUNT(*) FROM livros WHERE categoria_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoriaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao contar livros da categoria", e);
        }
        
        return 0;
    }
    
    /**
     * Mapeia um ResultSet para objeto Categoria.
     * 
     * @param rs ResultSet posicionado
     * @return Objeto Categoria
     * @throws SQLException Se houver erro no mapeamento
     */
    private Categoria mapResultSetToCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("id"));
        categoria.setNome(rs.getString("nome"));
        categoria.setDescricao(rs.getString("descricao"));
        
        Timestamp criadoEm = rs.getTimestamp("criado_em");
        if (criadoEm != null) {
            categoria.setCriadoEm(criadoEm.toLocalDateTime());
        }
        
        return categoria;
    }
}
