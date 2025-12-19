package com.biblioteca.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitária para gerenciamento de logs do sistema.
 * 
 * <p>Registra atividades em arquivo texto com diferentes níveis
 * de severidade: INFO, WARNING e ERROR.</p>
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class LogManager {
    
    /** Caminho do arquivo de log */
    private static final String LOG_FILE = "logs/biblioteca.log";
    
    /** Formato de data/hora para os logs */
    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /** Writer para escrita no arquivo */
    private static PrintWriter writer;
    
    /** Flag para indicar se o console deve receber os logs também */
    private static boolean logToConsole = true;
    
    // Inicialização estática
    static {
        initializeLogFile();
    }
    
    /**
     * Inicializa o arquivo de log, criando o diretório se necessário.
     */
    private static void initializeLogFile() {
        try {
            File logFile = new File(LOG_FILE);
            File logDir = logFile.getParentFile();
            
            // Criar diretório se não existir
            if (logDir != null && !logDir.exists()) {
                logDir.mkdirs();
            }
            
            // Criar writer com append mode
            writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(logFile, true)), true);
            
            info("Sistema de log inicializado.");
        } catch (IOException e) {
            System.err.println("Erro ao inicializar sistema de log: " + e.getMessage());
        }
    }
    
    /**
     * Registra uma mensagem de nível INFO.
     * 
     * @param message Mensagem a ser registrada
     */
    public static void info(String message) {
        log("INFO", message);
    }
    
    /**
     * Registra uma mensagem de nível WARNING.
     * 
     * @param message Mensagem a ser registrada
     */
    public static void warning(String message) {
        log("WARNING", message);
    }
    
    /**
     * Registra uma mensagem de nível ERROR.
     * 
     * @param message Mensagem a ser registrada
     */
    public static void error(String message) {
        log("ERROR", message);
    }
    
    /**
     * Registra uma mensagem de erro com exceção.
     * 
     * @param message Mensagem a ser registrada
     * @param throwable Exceção associada
     */
    public static void error(String message, Throwable throwable) {
        log("ERROR", message + " - " + throwable.getMessage());
        if (writer != null) {
            throwable.printStackTrace(writer);
        }
    }
    
    /**
     * Registra uma ação do usuário.
     * 
     * @param usuarioId ID do usuário
     * @param acao Ação realizada
     * @param descricao Descrição detalhada
     */
    public static void logUserAction(int usuarioId, String acao, String descricao) {
        String message = String.format("USUARIO[%d] - %s: %s", usuarioId, acao, descricao);
        log("ACTION", message);
    }
    
    /**
     * Registra uma ação do sistema.
     * 
     * @param acao Ação realizada
     * @param descricao Descrição detalhada
     */
    public static void logSystemAction(String acao, String descricao) {
        String message = String.format("SISTEMA - %s: %s", acao, descricao);
        log("ACTION", message);
    }
    
    /**
     * Método interno para registrar a mensagem.
     * 
     * @param level Nível do log
     * @param message Mensagem
     */
    private static synchronized void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = String.format("[%s] [%s] - %s", timestamp, level, message);
        
        // Escrever no arquivo
        if (writer != null) {
            writer.println(logEntry);
            writer.flush();
        }
        
        // Escrever no console se habilitado
        if (logToConsole) {
            if (level.equals("ERROR")) {
                System.err.println(logEntry);
            } else {
                System.out.println(logEntry);
            }
        }
    }
    
    /**
     * Habilita ou desabilita o log no console.
     * 
     * @param enabled true para habilitar
     */
    public static void setLogToConsole(boolean enabled) {
        logToConsole = enabled;
    }
    
    /**
     * Fecha o sistema de log.
     */
    public static void close() {
        if (writer != null) {
            info("Sistema de log finalizado.");
            writer.close();
            writer = null;
        }
    }
    
    /**
     * Retorna o caminho do arquivo de log.
     * 
     * @return Caminho do arquivo
     */
    public static String getLogFilePath() {
        return LOG_FILE;
    }
}
