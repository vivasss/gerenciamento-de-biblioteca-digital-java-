package com.biblioteca.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Classe utilitária para operações com senhas.
 * 
 * <p>Utiliza BCrypt para hash seguro de senhas com salt automático.</p>
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class PasswordUtils {
    
    /** Fator de custo do BCrypt (recomendado: 10-12) */
    private static final int BCRYPT_ROUNDS = 10;
    
    /**
     * Construtor privado para evitar instanciação.
     */
    private PasswordUtils() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }
    
    /**
     * Gera um hash BCrypt para a senha fornecida.
     * 
     * @param plainPassword Senha em texto plano
     * @return Hash BCrypt da senha
     * @throws IllegalArgumentException se a senha for nula ou vazia
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        
        String salt = BCrypt.gensalt(BCRYPT_ROUNDS);
        return BCrypt.hashpw(plainPassword, salt);
    }
    
    /**
     * Verifica se uma senha em texto plano corresponde a um hash.
     * 
     * @param plainPassword Senha em texto plano
     * @param hashedPassword Hash BCrypt armazenado
     * @return true se a senha corresponder ao hash
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            LogManager.error("Erro ao verificar senha: formato de hash inválido");
            return false;
        }
    }
    
    /**
     * Valida se uma senha atende aos requisitos mínimos de segurança.
     * 
     * <p>Requisitos:</p>
     * <ul>
     *   <li>Mínimo de 6 caracteres</li>
     *   <li>Máximo de 100 caracteres</li>
     * </ul>
     * 
     * @param password Senha a ser validada
     * @return true se a senha for válida
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        
        int length = password.length();
        return length >= 6 && length <= 100;
    }
    
    /**
     * Retorna uma mensagem de erro para senhas inválidas.
     * 
     * @param password Senha a ser validada
     * @return Mensagem de erro ou null se válida
     */
    public static String getPasswordValidationError(String password) {
        if (password == null || password.isEmpty()) {
            return "Senha é obrigatória";
        }
        
        if (password.length() < 6) {
            return "Senha deve ter no mínimo 6 caracteres";
        }
        
        if (password.length() > 100) {
            return "Senha deve ter no máximo 100 caracteres";
        }
        
        return null; // Senha válida
    }
    
    /**
     * Gera uma senha aleatória com o comprimento especificado.
     * 
     * @param length Comprimento da senha
     * @return Senha aleatória
     */
    public static String generateRandomPassword(int length) {
        if (length < 6) {
            length = 6;
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        
        return password.toString();
    }
}
