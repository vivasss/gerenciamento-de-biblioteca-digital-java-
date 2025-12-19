package com.biblioteca.dao;

import com.biblioteca.model.TipoUsuario;
import com.biblioteca.model.Usuario;
import com.biblioteca.utils.DatabaseConnection;
import com.biblioteca.utils.LogManager;
import com.biblioteca.utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para operações CRUD de usuários.
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class UsuarioDAO {
    
    /**
     * Insere um novo usuário no banco de dados.
     * 
     * @param usuario Usuário a ser inserido
     * @return true se inserido com sucesso
     */
    public boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha, tipo, ativo) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, PasswordUtils.hashPassword(usuario.getSenha()));
            stmt.setString(4, usuario.getTipo().name());
            stmt.setBoolean(5, usuario.isAtivo());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                    }
                }
                LogManager.info("Usuário inserido: " + usuario.getEmail());
                return true;
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao inserir usuário", e);
        }
        
        return false;
    }
    
    /**
     * Atualiza um usuário existente (sem alterar a senha).
     * 
     * @param usuario Usuário a ser atualizado
     * @return true se atualizado com sucesso
     */
    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, tipo = ?, ativo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTipo().name());
            stmt.setBoolean(4, usuario.isAtivo());
            stmt.setInt(5, usuario.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                LogManager.info("Usuário atualizado: " + usuario.getEmail());
                return true;
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao atualizar usuário", e);
        }
        
        return false;
    }
    
    /**
     * Atualiza a senha de um usuário.
     * 
     * @param usuarioId ID do usuário
     * @param novaSenha Nova senha em texto plano
     * @return true se atualizado com sucesso
     */
    public boolean atualizarSenha(int usuarioId, String novaSenha) {
        String sql = "UPDATE usuarios SET senha = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, PasswordUtils.hashPassword(novaSenha));
            stmt.setInt(2, usuarioId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                LogManager.info("Senha atualizada para usuário ID: " + usuarioId);
                return true;
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao atualizar senha", e);
        }
        
        return false;
    }
    
    /**
     * Remove um usuário pelo ID.
     * 
     * @param id ID do usuário
     * @return true se removido com sucesso
     */
    public boolean deletar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                LogManager.info("Usuário removido: ID " + id);
                return true;
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao deletar usuário", e);
        }
        
        return false;
    }
    
    /**
     * Desativa um usuário (soft delete).
     * 
     * @param id ID do usuário
     * @return true se desativado com sucesso
     */
    public boolean desativar(int id) {
        String sql = "UPDATE usuarios SET ativo = FALSE WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                LogManager.info("Usuário desativado: ID " + id);
                return true;
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao desativar usuário", e);
        }
        
        return false;
    }
    
    /**
     * Busca um usuário pelo ID.
     * 
     * @param id ID do usuário
     * @return Usuário encontrado ou null
     */
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao buscar usuário por ID", e);
        }
        
        return null;
    }
    
    /**
     * Busca um usuário pelo e-mail.
     * 
     * @param email E-mail do usuário
     * @return Usuário encontrado ou null
     */
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email.toLowerCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao buscar usuário por email", e);
        }
        
        return null;
    }
    
    /**
     * Autentica um usuário pelo e-mail e senha.
     * 
     * @param email E-mail do usuário
     * @param senha Senha em texto plano
     * @return Usuário autenticado ou null
     */
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);
        
        if (usuario == null) {
            LogManager.warning("Tentativa de login com email inexistente: " + email);
            return null;
        }
        
        if (!usuario.isAtivo()) {
            LogManager.warning("Tentativa de login com usuário desativado: " + email);
            return null;
        }
        
        if (PasswordUtils.checkPassword(senha, usuario.getSenha())) {
            LogManager.info("Login bem-sucedido: " + email);
            return usuario;
        }
        
        LogManager.warning("Tentativa de login com senha incorreta: " + email);
        return null;
    }
    
    /**
     * Lista todos os usuários.
     * 
     * @return Lista de usuários
     */
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao listar usuários", e);
        }
        
        return usuarios;
    }
    
    /**
     * Lista usuários ativos.
     * 
     * @return Lista de usuários ativos
     */
    public List<Usuario> listarAtivos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE ativo = TRUE ORDER BY nome";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao listar usuários ativos", e);
        }
        
        return usuarios;
    }
    
    /**
     * Lista usuários por tipo.
     * 
     * @param tipo Tipo de usuário
     * @return Lista de usuários do tipo especificado
     */
    public List<Usuario> listarPorTipo(TipoUsuario tipo) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE tipo = ? AND ativo = TRUE ORDER BY nome";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapResultSetToUsuario(rs));
                }
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao listar usuários por tipo", e);
        }
        
        return usuarios;
    }
    
    /**
     * Busca usuários por nome (parcial).
     * 
     * @param nome Nome ou parte do nome
     * @return Lista de usuários encontrados
     */
    public List<Usuario> buscarPorNome(String nome) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE nome LIKE ? ORDER BY nome";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nome + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapResultSetToUsuario(rs));
                }
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao buscar usuários por nome", e);
        }
        
        return usuarios;
    }
    
    /**
     * Verifica se um e-mail já está em uso.
     * 
     * @param email E-mail a verificar
     * @return true se já existe
     */
    public boolean emailExiste(String email) {
        return buscarPorEmail(email) != null;
    }
    
    /**
     * Conta o total de usuários.
     * 
     * @return Número de usuários
     */
    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM usuarios";
        
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            LogManager.error("Erro ao contar usuários", e);
        }
        
        return 0;
    }
    
    /**
     * Mapeia um ResultSet para objeto Usuario.
     * 
     * @param rs ResultSet posicionado
     * @return Objeto Usuario
     * @throws SQLException Se houver erro no mapeamento
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setTipo(TipoUsuario.valueOf(rs.getString("tipo")));
        usuario.setAtivo(rs.getBoolean("ativo"));
        
        Timestamp criadoEm = rs.getTimestamp("criado_em");
        if (criadoEm != null) {
            usuario.setCriadoEm(criadoEm.toLocalDateTime());
        }
        
        Timestamp atualizadoEm = rs.getTimestamp("atualizado_em");
        if (atualizadoEm != null) {
            usuario.setAtualizadoEm(atualizadoEm.toLocalDateTime());
        }
        
        return usuario;
    }
}
