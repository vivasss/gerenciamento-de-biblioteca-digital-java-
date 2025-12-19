package com.biblioteca.model;

import java.time.LocalDateTime;

/**
 * Classe modelo que representa uma categoria de livros.
 * 
 * <p>As categorias são usadas para organizar e classificar os livros
 * do acervo da biblioteca.</p>
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class Categoria {
    
    private int id;
    private String nome;
    private String descricao;
    private LocalDateTime criadoEm;
    
    /**
     * Construtor padrão.
     */
    public Categoria() {
        this.criadoEm = LocalDateTime.now();
    }
    
    /**
     * Construtor com parâmetros essenciais.
     * 
     * @param nome Nome da categoria
     * @param descricao Descrição da categoria
     */
    public Categoria(String nome, String descricao) {
        this();
        this.nome = nome;
        this.descricao = descricao;
    }
    
    /**
     * Construtor completo.
     * 
     * @param id Identificador único
     * @param nome Nome da categoria
     * @param descricao Descrição da categoria
     * @param criadoEm Data de criação
     */
    public Categoria(int id, String nome, String descricao, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.criadoEm = criadoEm;
    }
    
    // Getters e Setters
    
    /**
     * Retorna o ID da categoria.
     * @return ID da categoria
     */
    public int getId() {
        return id;
    }
    
    /**
     * Define o ID da categoria.
     * @param id ID da categoria
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Retorna o nome da categoria.
     * @return Nome da categoria
     */
    public String getNome() {
        return nome;
    }
    
    /**
     * Define o nome da categoria.
     * @param nome Nome da categoria
     * @throws IllegalArgumentException se o nome for nulo ou vazio
     */
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria não pode ser vazio");
        }
        this.nome = nome.trim();
    }
    
    /**
     * Retorna a descrição da categoria.
     * @return Descrição da categoria
     */
    public String getDescricao() {
        return descricao;
    }
    
    /**
     * Define a descrição da categoria.
     * @param descricao Descrição da categoria
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    /**
     * Retorna a data de criação.
     * @return Data de criação
     */
    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
    
    /**
     * Define a data de criação.
     * @param criadoEm Data de criação
     */
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
    
    @Override
    public String toString() {
        return nome;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Categoria categoria = (Categoria) obj;
        return id == categoria.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
