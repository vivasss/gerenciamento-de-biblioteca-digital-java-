package com.biblioteca.model;

import java.time.LocalDateTime;

/**
 * Classe modelo que representa um livro do acervo da biblioteca.
 * 
 * <p>Contém informações bibliográficas e controle de quantidade
 * disponível para empréstimo.</p>
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class Livro {
    
    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private int categoriaId;
    private String categoriaNome; // Para exibição
    private int quantidadeTotal;
    private int quantidadeDisponivel;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    
    /**
     * Construtor padrão.
     */
    public Livro() {
        this.quantidadeTotal = 1;
        this.quantidadeDisponivel = 1;
        this.criadoEm = LocalDateTime.now();
    }
    
    /**
     * Construtor com parâmetros essenciais.
     * 
     * @param titulo Título do livro
     * @param autor Autor do livro
     * @param isbn ISBN do livro
     * @param categoriaId ID da categoria
     * @param quantidadeTotal Quantidade total de exemplares
     */
    public Livro(String titulo, String autor, String isbn, int categoriaId, int quantidadeTotal) {
        this();
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoriaId = categoriaId;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeTotal;
    }
    
    /**
     * Construtor completo.
     * 
     * @param id Identificador único
     * @param titulo Título do livro
     * @param autor Autor do livro
     * @param isbn ISBN do livro
     * @param categoriaId ID da categoria
     * @param quantidadeTotal Quantidade total
     * @param quantidadeDisponivel Quantidade disponível
     * @param criadoEm Data de criação
     */
    public Livro(int id, String titulo, String autor, String isbn, int categoriaId,
                 int quantidadeTotal, int quantidadeDisponivel, LocalDateTime criadoEm) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoriaId = categoriaId;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.criadoEm = criadoEm;
    }
    
    // Getters e Setters
    
    /**
     * Retorna o ID do livro.
     * @return ID do livro
     */
    public int getId() {
        return id;
    }
    
    /**
     * Define o ID do livro.
     * @param id ID do livro
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Retorna o título do livro.
     * @return Título do livro
     */
    public String getTitulo() {
        return titulo;
    }
    
    /**
     * Define o título do livro.
     * @param titulo Título do livro
     * @throws IllegalArgumentException se o título for nulo ou vazio
     */
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }
        this.titulo = titulo.trim();
    }
    
    /**
     * Retorna o autor do livro.
     * @return Autor do livro
     */
    public String getAutor() {
        return autor;
    }
    
    /**
     * Define o autor do livro.
     * @param autor Autor do livro
     * @throws IllegalArgumentException se o autor for nulo ou vazio
     */
    public void setAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("Autor não pode ser vazio");
        }
        this.autor = autor.trim();
    }
    
    /**
     * Retorna o ISBN do livro.
     * @return ISBN do livro
     */
    public String getIsbn() {
        return isbn;
    }
    
    /**
     * Define o ISBN do livro.
     * @param isbn ISBN do livro
     * @throws IllegalArgumentException se o ISBN for nulo ou vazio
     */
    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN não pode ser vazio");
        }
        this.isbn = isbn.trim();
    }
    
    /**
     * Retorna o ID da categoria.
     * @return ID da categoria
     */
    public int getCategoriaId() {
        return categoriaId;
    }
    
    /**
     * Define o ID da categoria.
     * @param categoriaId ID da categoria
     */
    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }
    
    /**
     * Retorna o nome da categoria.
     * @return Nome da categoria
     */
    public String getCategoriaNome() {
        return categoriaNome;
    }
    
    /**
     * Define o nome da categoria (para exibição).
     * @param categoriaNome Nome da categoria
     */
    public void setCategoriaNome(String categoriaNome) {
        this.categoriaNome = categoriaNome;
    }
    
    /**
     * Retorna a quantidade total de exemplares.
     * @return Quantidade total
     */
    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }
    
    /**
     * Define a quantidade total de exemplares.
     * @param quantidadeTotal Quantidade total
     * @throws IllegalArgumentException se a quantidade for menor que 1
     */
    public void setQuantidadeTotal(int quantidadeTotal) {
        if (quantidadeTotal < 1) {
            throw new IllegalArgumentException("Quantidade total deve ser pelo menos 1");
        }
        this.quantidadeTotal = quantidadeTotal;
    }
    
    /**
     * Retorna a quantidade disponível para empréstimo.
     * @return Quantidade disponível
     */
    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }
    
    /**
     * Define a quantidade disponível para empréstimo.
     * @param quantidadeDisponivel Quantidade disponível
     */
    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
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
    
    /**
     * Retorna a data de atualização.
     * @return Data de atualização
     */
    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
    
    /**
     * Define a data de atualização.
     * @param atualizadoEm Data de atualização
     */
    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
    
    /**
     * Verifica se o livro está disponível para empréstimo.
     * @return true se disponível
     */
    public boolean isDisponivel() {
        return quantidadeDisponivel > 0;
    }
    
    /**
     * Realiza um empréstimo, decrementando a quantidade disponível.
     * @return true se o empréstimo foi realizado com sucesso
     */
    public boolean emprestar() {
        if (quantidadeDisponivel > 0) {
            quantidadeDisponivel--;
            return true;
        }
        return false;
    }
    
    /**
     * Realiza uma devolução, incrementando a quantidade disponível.
     * @return true se a devolução foi realizada com sucesso
     */
    public boolean devolver() {
        if (quantidadeDisponivel < quantidadeTotal) {
            quantidadeDisponivel++;
            return true;
        }
        return false;
    }
    
    /**
     * Retorna a quantidade de exemplares emprestados.
     * @return Quantidade emprestada
     */
    public int getQuantidadeEmprestada() {
        return quantidadeTotal - quantidadeDisponivel;
    }
    
    @Override
    public String toString() {
        return String.format("Livro{id=%d, titulo='%s', autor='%s', isbn='%s', disponivel=%d/%d}",
                id, titulo, autor, isbn, quantidadeDisponivel, quantidadeTotal);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Livro livro = (Livro) obj;
        return id == livro.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
