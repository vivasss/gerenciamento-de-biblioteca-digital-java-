package com.biblioteca.model;

/**
 * Enumeração que representa os tipos de usuários do sistema de biblioteca.
 * 
 * <p>Define os diferentes perfis de acesso e suas permissões no sistema:</p>
 * <ul>
 *   <li><b>ALUNO</b> - Pode realizar empréstimos com prazo de 14 dias</li>
 *   <li><b>PROFESSOR</b> - Pode realizar empréstimos com prazo de 30 dias</li>
 *   <li><b>ADMINISTRADOR</b> - Acesso total ao sistema, incluindo gestão de usuários</li>
 * </ul>
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public enum TipoUsuario {
    
    /** Usuário do tipo aluno com prazo de empréstimo de 14 dias */
    ALUNO("Aluno", 14),
    
    /** Usuário do tipo professor com prazo de empréstimo de 30 dias */
    PROFESSOR("Professor", 30),
    
    /** Usuário administrador com acesso total ao sistema */
    ADMINISTRADOR("Administrador", 30);
    
    private final String descricao;
    private final int diasEmprestimo;
    
    /**
     * Construtor do enum TipoUsuario.
     * 
     * @param descricao Descrição legível do tipo de usuário
     * @param diasEmprestimo Número de dias permitidos para empréstimo
     */
    TipoUsuario(String descricao, int diasEmprestimo) {
        this.descricao = descricao;
        this.diasEmprestimo = diasEmprestimo;
    }
    
    /**
     * Retorna a descrição legível do tipo de usuário.
     * 
     * @return Descrição do tipo de usuário
     */
    public String getDescricao() {
        return descricao;
    }
    
    /**
     * Retorna o número de dias permitidos para empréstimo.
     * 
     * @return Número de dias de empréstimo
     */
    public int getDiasEmprestimo() {
        return diasEmprestimo;
    }
    
    /**
     * Retorna a descrição do tipo de usuário.
     * 
     * @return Descrição do tipo
     */
    @Override
    public String toString() {
        return descricao;
    }
}
