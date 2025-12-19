package com.biblioteca.controller;

import com.biblioteca.App;
import com.biblioteca.dao.UsuarioDAO;
import com.biblioteca.model.Usuario;
import com.biblioteca.utils.LogManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller da tela de login.
 */
public class LoginController {
    
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblMensagem;
    @FXML private Button btnLogin;
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    @FXML
    public void initialize() {
        lblMensagem.setText("");
    }
    
    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText();
        
        if (email.isEmpty() || senha.isEmpty()) {
            showError("Por favor, preencha todos os campos.");
            return;
        }
        
        btnLogin.setDisable(true);
        
        try {
            Usuario usuario = usuarioDAO.autenticar(email, senha);
            
            if (usuario != null) {
                SessionManager.setUsuarioLogado(usuario);
                LogManager.logUserAction(usuario.getId(), "LOGIN", "Usuário logado com sucesso");
                App.changeScene("main.fxml", "Menu Principal", 1200, 700);
            } else {
                showError("E-mail ou senha incorretos.");
                LogManager.warning("Tentativa de login falhou: " + email);
            }
        } catch (Exception e) {
            showError("Erro ao conectar. Verifique o banco de dados.");
            LogManager.error("Erro no login", e);
        } finally {
            btnLogin.setDisable(false);
        }
    }
    
    private void showError(String message) {
        lblMensagem.setText(message);
        lblMensagem.setStyle("-fx-text-fill: #e74c3c;");
    }
}
