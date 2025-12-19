package com.biblioteca.controller;

import com.biblioteca.dao.CategoriaDAO;
import com.biblioteca.dao.UsuarioDAO;
import com.biblioteca.model.TipoUsuario;
import com.biblioteca.model.Usuario;
import com.biblioteca.utils.LogManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller para gestão de usuários.
 */
public class UsuarioController {
    
    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, TipoUsuario> colTipo;
    @FXML private TableColumn<Usuario, Boolean> colAtivo;
    
    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<TipoUsuario> cmbTipo;
    @FXML private CheckBox chkAtivo;
    @FXML private TextField txtBusca;
    @FXML private Label lblMensagem;
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuarioSelecionado;
    
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));
        
        cmbTipo.setItems(FXCollections.observableArrayList(TipoUsuario.values()));
        cmbTipo.setValue(TipoUsuario.ALUNO);
        chkAtivo.setSelected(true);
        
        tableUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> {
            if (novo != null) preencherFormulario(novo);
        });
        
        carregarUsuarios();
    }
    
    private void carregarUsuarios() {
        tableUsuarios.setItems(FXCollections.observableArrayList(usuarioDAO.listarTodos()));
    }
    
    private void preencherFormulario(Usuario u) {
        usuarioSelecionado = u;
        txtNome.setText(u.getNome());
        txtEmail.setText(u.getEmail());
        txtSenha.setText("");
        cmbTipo.setValue(u.getTipo());
        chkAtivo.setSelected(u.isAtivo());
    }
    
    @FXML
    private void handleSalvar() {
        if (!validarFormulario()) return;
        
        try {
            if (usuarioSelecionado == null) {
                Usuario novo = new Usuario(txtNome.getText(), txtEmail.getText(), txtSenha.getText(), cmbTipo.getValue());
                novo.setAtivo(chkAtivo.isSelected());
                if (usuarioDAO.inserir(novo)) {
                    showSuccess("Usuário cadastrado com sucesso!");
                    limparFormulario();
                    carregarUsuarios();
                }
            } else {
                usuarioSelecionado.setNome(txtNome.getText());
                usuarioSelecionado.setEmail(txtEmail.getText());
                usuarioSelecionado.setTipo(cmbTipo.getValue());
                usuarioSelecionado.setAtivo(chkAtivo.isSelected());
                if (usuarioDAO.atualizar(usuarioSelecionado)) {
                    if (!txtSenha.getText().isEmpty()) {
                        usuarioDAO.atualizarSenha(usuarioSelecionado.getId(), txtSenha.getText());
                    }
                    showSuccess("Usuário atualizado com sucesso!");
                    limparFormulario();
                    carregarUsuarios();
                }
            }
        } catch (Exception e) {
            showError("Erro ao salvar usuário.");
            LogManager.error("Erro ao salvar usuário", e);
        }
    }
    
    @FXML
    private void handleExcluir() {
        if (usuarioSelecionado == null) { showError("Selecione um usuário."); return; }
        if (usuarioSelecionado.getId() == SessionManager.getUsuarioLogado().getId()) {
            showError("Você não pode excluir seu próprio usuário.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente excluir este usuário?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (usuarioDAO.deletar(usuarioSelecionado.getId())) {
                    showSuccess("Usuário excluído!");
                    limparFormulario();
                    carregarUsuarios();
                }
            }
        });
    }
    
    @FXML
    private void handleBuscar() {
        String termo = txtBusca.getText().trim();
        if (termo.isEmpty()) carregarUsuarios();
        else tableUsuarios.setItems(FXCollections.observableArrayList(usuarioDAO.buscarPorNome(termo)));
    }
    
    @FXML
    private void handleLimpar() { limparFormulario(); }
    
    private void limparFormulario() {
        usuarioSelecionado = null;
        txtNome.clear(); txtEmail.clear(); txtSenha.clear();
        cmbTipo.setValue(TipoUsuario.ALUNO);
        chkAtivo.setSelected(true);
        tableUsuarios.getSelectionModel().clearSelection();
        lblMensagem.setText("");
    }
    
    private boolean validarFormulario() {
        if (txtNome.getText().trim().isEmpty()) { showError("Nome é obrigatório."); return false; }
        if (txtEmail.getText().trim().isEmpty()) { showError("E-mail é obrigatório."); return false; }
        if (usuarioSelecionado == null && txtSenha.getText().isEmpty()) { showError("Senha é obrigatória."); return false; }
        if (usuarioSelecionado == null && usuarioDAO.emailExiste(txtEmail.getText())) { showError("E-mail já cadastrado."); return false; }
        return true;
    }
    
    private void showError(String s) { lblMensagem.setText(s); lblMensagem.setStyle("-fx-text-fill: #e74c3c;"); }
    private void showSuccess(String s) { lblMensagem.setText(s); lblMensagem.setStyle("-fx-text-fill: #27ae60;"); }
}
