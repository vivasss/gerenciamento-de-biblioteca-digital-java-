package com.biblioteca.controller;

import com.biblioteca.App;
import com.biblioteca.dao.*;
import com.biblioteca.utils.LogManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Controller da tela principal.
 */
public class MainController {
    
    @FXML private Label lblUsuario;
    @FXML private Label lblTipoUsuario;
    @FXML private StackPane contentArea;
    @FXML private Label lblTotalLivros;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblEmprestimosAtivos;
    @FXML private Label lblEmprestimosAtrasados;
    @FXML private Button btnUsuarios;
    
    @FXML
    public void initialize() {
        if (SessionManager.isLogado()) {
            lblUsuario.setText(SessionManager.getUsuarioLogado().getNome());
            lblTipoUsuario.setText(SessionManager.getUsuarioLogado().getTipo().getDescricao());
        }
        
        // Ocultar menu de usuários se não for admin
        if (!SessionManager.isAdmin() && btnUsuarios != null) {
            btnUsuarios.setVisible(false);
            btnUsuarios.setManaged(false);
        }
        
        atualizarEstatisticas();
    }
    
    private void atualizarEstatisticas() {
        try {
            lblTotalLivros.setText(String.valueOf(new LivroDAO().contarTotal()));
            lblTotalUsuarios.setText(String.valueOf(new UsuarioDAO().contarTotal()));
            lblEmprestimosAtivos.setText(String.valueOf(new EmprestimoDAO().contarAtivos()));
            lblEmprestimosAtrasados.setText(String.valueOf(new EmprestimoDAO().listarAtrasados().size()));
        } catch (Exception e) {
            LogManager.error("Erro ao carregar estatísticas", e);
        }
    }
    
    @FXML private void showDashboard() { atualizarEstatisticas(); loadContent("dashboard.fxml"); }
    @FXML private void showLivros() { loadContent("livros.fxml"); }
    @FXML private void showUsuarios() { if (SessionManager.isAdmin()) loadContent("usuarios.fxml"); }
    @FXML private void showEmprestimos() { loadContent("emprestimos.fxml"); }
    @FXML private void showRelatorios() { loadContent("relatorios.fxml"); }
    
    @FXML
    private void handleLogout() {
        LogManager.logUserAction(SessionManager.getUsuarioLogado().getId(), "LOGOUT", "Usuário deslogado");
        SessionManager.logout();
        App.changeScene("login.fxml", "Login", 400, 500);
    }
    
    private void loadContent(String fxml) {
        try {
            Parent content = FXMLLoader.load(getClass().getResource("/fxml/" + fxml));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (Exception e) {
            LogManager.error("Erro ao carregar conteúdo: " + fxml, e);
        }
    }
}
