package com.biblioteca.model;

import java.time.LocalDateTime;

/**
 * Classe modelo que representa um usuário do sistema de biblioteca.
 * 
 * <p>Um usuário pode ser do tipo ALUNO, PROFESSOR ou ADMINISTRADOR,
 * cada um com diferentes permissões e prazos de empréstimo.</p>
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class Usuario {
    
    private int id;
    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipo;
    private boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    
    /**
     * Construtor padrão.
     */
    public Usuario() {
        this.tipo = TipoUsuario.ALUNO;
        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
    }
    
    /**
     * Construtor com parâmetros essenciais.
     * 
     * @param nome Nome completo do usuário
     * @param email E-mail do usuário (usado como login)
     * @param senha Senha do usuário (será armazenada como hash)
     * @param tipo Tipo do usuário (ALUNO, PROFESSOR, ADMINISTRADOR)
     */
    public Usuario(String nome, String email, String senha, TipoUsuario tipo) {
        this();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }
    
    /**
     * Construtor completo com todos os parâmetros.
     * 
     * @param id Identificador único do usuário
     * @param nome Nome completo do usuário
     * @param email E-mail do usuário
     * @param senha Senha do usuário
     * @param tipo Tipo do usuário
     * @param ativo Status de ativação
     * @param criadoEm Data de criação
     */
    public Usuario(int id, String nome, String email, String senha, 
                   TipoUsuario tipo, boolean ativo, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
    }
    
    // Getters e Setters
    
    /**
     * Retorna o ID do usuário.
     * @return ID do usuário
     */
    public int getId() {
        return id;
    }
    
    /**
     * Define o ID do usuário.
     * @param id ID do usuário
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Retorna o nome do usuário.
     * @return Nome do usuário
     */
    public String getNome() {
        return nome;
    }
    
    /**
     * Define o nome do usuário.
     * @param nome Nome do usuário
     * @throws IllegalArgumentException se o nome for nulo ou vazio
     */
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        this.nome = nome.trim();
    }
    
    /**
     * Retorna o e-mail do usuário.
     * @return E-mail do usuário
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Define o e-mail do usuário.
     * @param email E-mail do usuário
     * @throws IllegalArgumentException se o e-mail for inválido
     */
    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("E-mail inválido");
        }
        this.email = email.toLowerCase().trim();
    }
    
    /**
     * Retorna a senha do usuário (hash).
     * @return Senha do usuário
     */
    public String getSenha() {
        return senha;
    }
    
    /**
     * Define a senha do usuário.
     * @param senha Senha do usuário
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    /**
     * Retorna o tipo do usuário.
     * @return Tipo do usuário
     */
    public TipoUsuario getTipo() {
        return tipo;
    }
    
    /**
     * Define o tipo do usuário.
     * @param tipo Tipo do usuário
     */
    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }
    
    /**
     * Verifica se o usuário está ativo.
     * @return true se ativo, false caso contrário
     */
    public boolean isAtivo() {
        return ativo;
    }
    
    /**
     * Define o status de ativação do usuário.
     * @param ativo Status de ativação
     */
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    /**
     * Retorna a data de criação do usuário.
     * @return Data de criação
     */
    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
    
    /**
     * Define a data de criação do usuário.
     * @param criadoEm Data de criação
     */
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
    
    /**
     * Retorna a data de última atualização.
     * @return Data de atualização
     */
    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
    
    /**
     * Define a data de última atualização.
     * @param atualizadoEm Data de atualização
     */
    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
    
    /**
     * Verifica se o usuário é administrador.
     * @return true se for administrador
     */
    public boolean isAdmin() {
        return tipo == TipoUsuario.ADMINISTRADOR;
    }
    
    /**
     * Retorna o número de dias permitidos para empréstimo.
     * @return Número de dias de empréstimo
     */
    public int getDiasEmprestimo() {
        return tipo.getDiasEmprestimo();
    }
    
    @Override
    public String toString() {
        return String.format("Usuario{id=%d, nome='%s', email='%s', tipo=%s, ativo=%b}",
                id, nome, email, tipo, ativo);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return id == usuario.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
