package com.biblioteca.model;

/**
 * Enumeração que representa os possíveis status de um empréstimo.
 * 
 * <p>Os status controlam o ciclo de vida de um empréstimo no sistema:</p>
 * <ul>
 *   <li><b>ATIVO</b> - Empréstimo em andamento, dentro do prazo</li>
 *   <li><b>DEVOLVIDO</b> - Livro devolvido com sucesso</li>
 *   <li><b>ATRASADO</b> - Prazo de devolução ultrapassado</li>
 * </ul>
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public enum StatusEmprestimo {
    
    /** Empréstimo ativo e dentro do prazo */
    ATIVO("Ativo", "green"),
    
    /** Livro devolvido com sucesso */
    DEVOLVIDO("Devolvido", "blue"),
    
    /** Empréstimo com prazo ultrapassado */
    ATRASADO("Atrasado", "red");
    
    private final String descricao;
    private final String cor;
    
    /**
     * Construtor do enum StatusEmprestimo.
     * 
     * @param descricao Descrição legível do status
     * @param cor Cor para exibição na interface
     */
    StatusEmprestimo(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }
    
    /**
     * Retorna a descrição legível do status.
     * 
     * @return Descrição do status
     */
    public String getDescricao() {
        return descricao;
    }
    
    /**
     * Retorna a cor associada ao status para exibição na interface.
     * 
     * @return Nome da cor CSS
     */
    public String getCor() {
        return cor;
    }
    
    /**
     * Retorna a descrição do status.
     * 
     * @return Descrição do status
     */
    @Override
    public String toString() {
        return descricao;
    }
}
