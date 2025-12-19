package com.biblioteca.controller;

import com.biblioteca.dao.*;
import com.biblioteca.model.*;
import com.biblioteca.utils.LogManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller para gestão de empréstimos.
 */
public class EmprestimoController {
    
    @FXML private TableView<Emprestimo> tableEmprestimos;
    @FXML private TableColumn<Emprestimo, Integer> colId;
    @FXML private TableColumn<Emprestimo, String> colUsuario;
    @FXML private TableColumn<Emprestimo, String> colLivro;
    @FXML private TableColumn<Emprestimo, String> colDataEmp;
    @FXML private TableColumn<Emprestimo, String> colDataDev;
    @FXML private TableColumn<Emprestimo, StatusEmprestimo> colStatus;
    
    @FXML private ComboBox<Usuario> cmbUsuario;
    @FXML private ComboBox<Livro> cmbLivro;
    @FXML private Label lblMensagem;
    @FXML private RadioButton rbTodos;
    @FXML private RadioButton rbAtivos;
    @FXML private RadioButton rbAtrasados;
    
    private EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private LivroDAO livroDAO = new LivroDAO();
    
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuarioNome"));
        colLivro.setCellValueFactory(new PropertyValueFactory<>("livroTitulo"));
        colDataEmp.setCellValueFactory(new PropertyValueFactory<>("dataEmprestimo"));
        colDataDev.setCellValueFactory(new PropertyValueFactory<>("dataDevolucaoPrevista"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        cmbUsuario.setItems(FXCollections.observableArrayList(usuarioDAO.listarAtivos()));
        cmbLivro.setItems(FXCollections.observableArrayList(livroDAO.listarDisponiveis()));
        
        ToggleGroup group = new ToggleGroup();
        rbTodos.setToggleGroup(group);
        rbAtivos.setToggleGroup(group);
        rbAtrasados.setToggleGroup(group);
        rbTodos.setSelected(true);
        
        group.selectedToggleProperty().addListener((obs, old, novo) -> handleFiltrar());
        
        carregarEmprestimos();
    }
    
    private void carregarEmprestimos() {
        tableEmprestimos.setItems(FXCollections.observableArrayList(emprestimoDAO.listarTodos()));
    }
    
    @FXML
    private void handleEmprestar() {
        if (cmbUsuario.getValue() == null) { showError("Selecione um usuário."); return; }
        if (cmbLivro.getValue() == null) { showError("Selecione um livro."); return; }
        
        try {
            Usuario usuario = cmbUsuario.getValue();
            Livro livro = cmbLivro.getValue();
            
            Emprestimo emp = new Emprestimo(usuario.getId(), livro.getId(), usuario.getDiasEmprestimo());
            
            if (emprestimoDAO.inserir(emp) && livroDAO.decrementarDisponivel(livro.getId())) {
                LogManager.logUserAction(SessionManager.getUsuarioLogado().getId(), "EMPRESTIMO", 
                    "Empréstimo realizado: " + livro.getTitulo() + " para " + usuario.getNome());
                showSuccess("Empréstimo realizado! Devolução: " + emp.getDataDevolucaoPrevista());
                cmbLivro.setItems(FXCollections.observableArrayList(livroDAO.listarDisponiveis()));
                carregarEmprestimos();
            }
        } catch (Exception e) { showError("Erro ao realizar empréstimo."); LogManager.error("Erro empréstimo", e); }
    }
    
    @FXML
    private void handleDevolver() {
        Emprestimo emp = tableEmprestimos.getSelectionModel().getSelectedItem();
        if (emp == null) { showError("Selecione um empréstimo."); return; }
        if (emp.getStatus() == StatusEmprestimo.DEVOLVIDO) { showError("Já devolvido."); return; }
        
        try {
            if (emprestimoDAO.devolver(emp.getId()) && livroDAO.incrementarDisponivel(emp.getLivroId())) {
                LogManager.logUserAction(SessionManager.getUsuarioLogado().getId(), "DEVOLUCAO", 
                    "Devolução: " + emp.getLivroTitulo());
                showSuccess("Devolução realizada!");
                cmbLivro.setItems(FXCollections.observableArrayList(livroDAO.listarDisponiveis()));
                carregarEmprestimos();
            }
        } catch (Exception e) { showError("Erro ao devolver."); LogManager.error("Erro devolução", e); }
    }
    
    @FXML
    private void handleFiltrar() {
        if (rbAtivos.isSelected()) {
            tableEmprestimos.setItems(FXCollections.observableArrayList(emprestimoDAO.listarAtivos()));
        } else if (rbAtrasados.isSelected()) {
            tableEmprestimos.setItems(FXCollections.observableArrayList(emprestimoDAO.listarAtrasados()));
        } else {
            carregarEmprestimos();
        }
    }
    
    private void showError(String s) { lblMensagem.setText(s); lblMensagem.setStyle("-fx-text-fill: #e74c3c;"); }
    private void showSuccess(String s) { lblMensagem.setText(s); lblMensagem.setStyle("-fx-text-fill: #27ae60;"); }
}
