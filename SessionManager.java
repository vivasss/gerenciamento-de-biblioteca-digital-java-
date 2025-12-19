package com.biblioteca.controller;

import com.biblioteca.model.Usuario;

/**
 * Classe para gerenciar a sessão do usuário logado.
 */
public class SessionManager {
    private static Usuario usuarioLogado;
    
    public static void setUsuarioLogado(Usuario usuario) { usuarioLogado = usuario; }
    public static Usuario getUsuarioLogado() { return usuarioLogado; }
    public static boolean isLogado() { return usuarioLogado != null; }
    public static void logout() { usuarioLogado = null; }
    public static boolean isAdmin() { return usuarioLogado != null && usuarioLogado.isAdmin(); }
}
