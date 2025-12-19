package com.biblioteca.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitária para gerenciamento de conexões com o banco de dados MySQL.
 * 
 * <p>Implementa o padrão Singleton para garantir uma única instância
 * de configuração de conexão no sistema.</p>
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class DatabaseConnection {
    
    /** URL de conexão com o banco de dados */
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_digital?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true";
    
    /** Usuário do banco de dados */
    private static final String USER = "root";
    
    /** Senha do banco de dados */
    private static final String PASSWORD = "";
    
    /** Instância única da classe */
    private static DatabaseConnection instance;
    
    /** Conexão atual */
    private Connection connection;
    
    /**
     * Construtor privado para implementar Singleton.
     */
    private DatabaseConnection() {
        try {
            // Registrar o driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            LogManager.info("Driver MySQL carregado com sucesso.");
        } catch (ClassNotFoundException e) {
            LogManager.error("Driver MySQL não encontrado: " + e.getMessage());
            throw new RuntimeException("Driver MySQL não encontrado", e);
        }
    }
    
    /**
     * Retorna a instância única da classe DatabaseConnection.
     * 
     * @return Instância de DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Obtém uma nova conexão com o banco de dados.
     * 
     * @return Conexão ativa com o banco de dados
     * @throws SQLException Se houver erro na conexão
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                LogManager.info("Conexão com banco de dados estabelecida.");
            } catch (SQLException e) {
                LogManager.error("Erro ao conectar ao banco de dados: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Obtém uma nova conexão (sem reutilizar conexão existente).
     * 
     * @return Nova conexão com o banco de dados
     * @throws SQLException Se houver erro na conexão
     */
    public Connection getNewConnection() throws SQLException {
        try {
            Connection newConnection = DriverManager.getConnection(URL, USER, PASSWORD);
            LogManager.info("Nova conexão com banco de dados criada.");
            return newConnection;
        } catch (SQLException e) {
            LogManager.error("Erro ao criar nova conexão: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Fecha a conexão atual com o banco de dados.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    LogManager.info("Conexão com banco de dados fechada.");
                }
            } catch (SQLException e) {
                LogManager.error("Erro ao fechar conexão: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
    
    /**
     * Fecha uma conexão específica.
     * 
     * @param conn Conexão a ser fechada
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                LogManager.error("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
    
    /**
     * Testa a conexão com o banco de dados.
     * 
     * @return true se a conexão for bem-sucedida
     */
    public boolean testConnection() {
        try (Connection conn = getNewConnection()) {
            boolean valid = conn.isValid(5);
            LogManager.info("Teste de conexão: " + (valid ? "Sucesso" : "Falha"));
            return valid;
        } catch (SQLException e) {
            LogManager.error("Teste de conexão falhou: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retorna a URL de conexão configurada.
     * 
     * @return URL do banco de dados
     */
    public static String getUrl() {
        return URL;
    }
    
    /**
     * Retorna o usuário configurado.
     * 
     * @return Nome do usuário
     */
    public static String getUser() {
        return USER;
    }
}
