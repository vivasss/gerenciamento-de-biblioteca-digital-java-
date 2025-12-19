package com.biblioteca.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Classe modelo que representa um empréstimo de livro.
 * 
 * <p>Controla o ciclo de vida do empréstimo, desde a retirada
 * até a devolução do livro.</p>
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class Emprestimo {
    
    private int id;
    private int usuarioId;
    private int livroId;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoReal;
    private StatusEmprestimo status;
    private String observacoes;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    
    // Campos para exibição (joins)
    private String usuarioNome;
    private String usuarioEmail;
    private String livroTitulo;
    private String livroAutor;
    
    /**
     * Construtor padrão.
     */
    public Emprestimo() {
        this.dataEmprestimo = LocalDate.now();
        this.status = StatusEmprestimo.ATIVO;
        this.criadoEm = LocalDateTime.now();
    }
    
    /**
     * Construtor com parâmetros essenciais.
     * 
     * @param usuarioId ID do usuário
     * @param livroId ID do livro
     * @param diasEmprestimo Número de dias para devolução
     */
    public Emprestimo(int usuarioId, int livroId, int diasEmprestimo) {
        this();
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataDevolucaoPrevista = dataEmprestimo.plusDays(diasEmprestimo);
    }
    
    /**
     * Construtor completo.
     * 
     * @param id Identificador único
     * @param usuarioId ID do usuário
     * @param livroId ID do livro
     * @param dataEmprestimo Data do empréstimo
     * @param dataDevolucaoPrevista Data prevista para devolução
     * @param dataDevolucaoReal Data real da devolução
     * @param status Status do empréstimo
     * @param criadoEm Data de criação
     */
    public Emprestimo(int id, int usuarioId, int livroId, LocalDate dataEmprestimo,
                      LocalDate dataDevolucaoPrevista, LocalDate dataDevolucaoReal,
                      StatusEmprestimo status, LocalDateTime criadoEm) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataDevolucaoReal = dataDevolucaoReal;
        this.status = status;
        this.criadoEm = criadoEm;
    }
    
    // Getters e Setters
    
    /**
     * Retorna o ID do empréstimo.
     * @return ID do empréstimo
     */
    public int getId() {
        return id;
    }
    
    /**
     * Define o ID do empréstimo.
     * @param id ID do empréstimo
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Retorna o ID do usuário.
     * @return ID do usuário
     */
    public int getUsuarioId() {
        return usuarioId;
    }
    
    /**
     * Define o ID do usuário.
     * @param usuarioId ID do usuário
     */
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    /**
     * Retorna o ID do livro.
     * @return ID do livro
     */
    public int getLivroId() {
        return livroId;
    }
    
    /**
     * Define o ID do livro.
     * @param livroId ID do livro
     */
    public void setLivroId(int livroId) {
        this.livroId = livroId;
    }
    
    /**
     * Retorna a data do empréstimo.
     * @return Data do empréstimo
     */
    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }
    
    /**
     * Define a data do empréstimo.
     * @param dataEmprestimo Data do empréstimo
     */
    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }
    
    /**
     * Retorna a data prevista para devolução.
     * @return Data prevista para devolução
     */
    public LocalDate getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }
    
    /**
     * Define a data prevista para devolução.
     * @param dataDevolucaoPrevista Data prevista
     */
    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }
    
    /**
     * Retorna a data real da devolução.
     * @return Data real da devolução (null se não devolvido)
     */
    public LocalDate getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }
    
    /**
     * Define a data real da devolução.
     * @param dataDevolucaoReal Data real da devolução
     */
    public void setDataDevolucaoReal(LocalDate dataDevolucaoReal) {
        this.dataDevolucaoReal = dataDevolucaoReal;
    }
    
    /**
     * Retorna o status do empréstimo.
     * @return Status do empréstimo
     */
    public StatusEmprestimo getStatus() {
        return status;
    }
    
    /**
     * Define o status do empréstimo.
     * @param status Status do empréstimo
     */
    public void setStatus(StatusEmprestimo status) {
        this.status = status;
    }
    
    /**
     * Retorna as observações do empréstimo.
     * @return Observações
     */
    public String getObservacoes() {
        return observacoes;
    }
    
    /**
     * Define as observações do empréstimo.
     * @param observacoes Observações
     */
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
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
    
    // Campos para exibição (joins)
    
    /**
     * Retorna o nome do usuário.
     * @return Nome do usuário
     */
    public String getUsuarioNome() {
        return usuarioNome;
    }
    
    /**
     * Define o nome do usuário.
     * @param usuarioNome Nome do usuário
     */
    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }
    
    /**
     * Retorna o e-mail do usuário.
     * @return E-mail do usuário
     */
    public String getUsuarioEmail() {
        return usuarioEmail;
    }
    
    /**
     * Define o e-mail do usuário.
     * @param usuarioEmail E-mail do usuário
     */
    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }
    
    /**
     * Retorna o título do livro.
     * @return Título do livro
     */
    public String getLivroTitulo() {
        return livroTitulo;
    }
    
    /**
     * Define o título do livro.
     * @param livroTitulo Título do livro
     */
    public void setLivroTitulo(String livroTitulo) {
        this.livroTitulo = livroTitulo;
    }
    
    /**
     * Retorna o autor do livro.
     * @return Autor do livro
     */
    public String getLivroAutor() {
        return livroAutor;
    }
    
    /**
     * Define o autor do livro.
     * @param livroAutor Autor do livro
     */
    public void setLivroAutor(String livroAutor) {
        this.livroAutor = livroAutor;
    }
    
    // Métodos de lógica de negócio
    
    /**
     * Verifica se o empréstimo está atrasado.
     * @return true se estiver atrasado
     */
    public boolean isAtrasado() {
        if (status == StatusEmprestimo.DEVOLVIDO) {
            return false;
        }
        return LocalDate.now().isAfter(dataDevolucaoPrevista);
    }
    
    /**
     * Retorna o status atual considerando atrasos.
     * @return Status atualizado
     */
    public StatusEmprestimo getStatusAtual() {
        if (status == StatusEmprestimo.DEVOLVIDO) {
            return StatusEmprestimo.DEVOLVIDO;
        }
        return isAtrasado() ? StatusEmprestimo.ATRASADO : StatusEmprestimo.ATIVO;
    }
    
    /**
     * Calcula os dias de atraso.
     * @return Número de dias de atraso (0 se não atrasado)
     */
    public long getDiasAtraso() {
        if (!isAtrasado()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dataDevolucaoPrevista, LocalDate.now());
    }
    
    /**
     * Calcula os dias restantes para devolução.
     * @return Número de dias restantes (negativo se atrasado)
     */
    public long getDiasRestantes() {
        if (status == StatusEmprestimo.DEVOLVIDO) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dataDevolucaoPrevista);
    }
    
    /**
     * Realiza a devolução do livro.
     */
    public void devolver() {
        this.dataDevolucaoReal = LocalDate.now();
        this.status = StatusEmprestimo.DEVOLVIDO;
        this.atualizadoEm = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return String.format("Emprestimo{id=%d, usuario=%d, livro=%d, status=%s, devolucao=%s}",
                id, usuarioId, livroId, getStatusAtual(), dataDevolucaoPrevista);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Emprestimo that = (Emprestimo) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
