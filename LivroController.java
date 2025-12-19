package com.biblioteca.controller;

import com.biblioteca.dao.CategoriaDAO;
import com.biblioteca.dao.LivroDAO;
import com.biblioteca.model.Categoria;
import com.biblioteca.model.Livro;
import com.biblioteca.utils.LogManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller para gestão de livros.
 */
public class LivroController {
    
    @FXML private TableView<Livro> tableLivros;
    @FXML private TableColumn<Livro, Integer> colId;
    @FXML private TableColumn<Livro, String> colTitulo;
    @FXML private TableColumn<Livro, String> colAutor;
    @FXML private TableColumn<Livro, String> colIsbn;
    @FXML private TableColumn<Livro, String> colCategoria;
    @FXML private TableColumn<Livro, Integer> colDisponivel;
    
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtIsbn;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private Spinner<Integer> spnQuantidade;
    @FXML private TextField txtBusca;
    @FXML private Label lblMensagem;
    
    private LivroDAO livroDAO = new LivroDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private Livro livroSelecionado;
    
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoriaNome"));
        colDisponivel.setCellValueFactory(new PropertyValueFactory<>("quantidadeDisponivel"));
        
        cmbCategoria.setItems(FXCollections.observableArrayList(categoriaDAO.listarTodas()));
        if (!cmbCategoria.getItems().isEmpty()) cmbCategoria.setValue(cmbCategoria.getItems().get(0));
        
        spnQuantidade.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        
        tableLivros.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> {
            if (novo != null) preencherFormulario(novo);
        });
        
        carregarLivros();
    }
    
    private void carregarLivros() {
        tableLivros.setItems(FXCollections.observableArrayList(livroDAO.listarTodos()));
    }
    
    private void preencherFormulario(Livro l) {
        livroSelecionado = l;
        txtTitulo.setText(l.getTitulo());
        txtAutor.setText(l.getAutor());
        txtIsbn.setText(l.getIsbn());
        spnQuantidade.getValueFactory().setValue(l.getQuantidadeTotal());
        for (Categoria c : cmbCategoria.getItems()) {
            if (c.getId() == l.getCategoriaId()) { cmbCategoria.setValue(c); break; }
        }
    }
    
    @FXML
    private void handleSalvar() {
        if (!validarFormulario()) return;
        
        try {
            if (livroSelecionado == null) {
                Livro novo = new Livro(txtTitulo.getText(), txtAutor.getText(), txtIsbn.getText(), 
                                       cmbCategoria.getValue().getId(), spnQuantidade.getValue());
                if (livroDAO.inserir(novo)) { showSuccess("Livro cadastrado!"); limparFormulario(); carregarLivros(); }
            } else {
                livroSelecionado.setTitulo(txtTitulo.getText());
                livroSelecionado.setAutor(txtAutor.getText());
                livroSelecionado.setIsbn(txtIsbn.getText());
                livroSelecionado.setCategoriaId(cmbCategoria.getValue().getId());
                int diff = spnQuantidade.getValue() - livroSelecionado.getQuantidadeTotal();
                livroSelecionado.setQuantidadeTotal(spnQuantidade.getValue());
                livroSelecionado.setQuantidadeDisponivel(livroSelecionado.getQuantidadeDisponivel() + diff);
                if (livroDAO.atualizar(livroSelecionado)) { showSuccess("Livro atualizado!"); limparFormulario(); carregarLivros(); }
            }
        } catch (Exception e) { showError("Erro ao salvar."); LogManager.error("Erro ao salvar livro", e); }
    }
    
    @FXML
    private void handleExcluir() {
        if (livroSelecionado == null) { showError("Selecione um livro."); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Excluir este livro?");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK && livroDAO.deletar(livroSelecionado.getId())) {
                showSuccess("Livro excluído!");
                limparFormulario();
                carregarLivros();
            }
        });
    }
    
    @FXML
    private void handleBuscar() {
        String termo = txtBusca.getText().trim();
        if (termo.isEmpty()) carregarLivros();
        else tableLivros.setItems(FXCollections.observableArrayList(livroDAO.buscarPorTitulo(termo)));
    }
    
    @FXML private void handleLimpar() { limparFormulario(); }
    
    private void limparFormulario() {
        livroSelecionado = null;
        txtTitulo.clear(); txtAutor.clear(); txtIsbn.clear();
        spnQuantidade.getValueFactory().setValue(1);
        tableLivros.getSelectionModel().clearSelection();
        lblMensagem.setText("");
    }
    
    private boolean validarFormulario() {
        if (txtTitulo.getText().trim().isEmpty()) { showError("Título é obrigatório."); return false; }
        if (txtAutor.getText().trim().isEmpty()) { showError("Autor é obrigatório."); return false; }
        if (txtIsbn.getText().trim().isEmpty()) { showError("ISBN é obrigatório."); return false; }
        if (livroSelecionado == null && livroDAO.isbnExiste(txtIsbn.getText())) { showError("ISBN já cadastrado."); return false; }
        return true;
    }
    
    private void showError(String s) { lblMensagem.setText(s); lblMensagem.setStyle("-fx-text-fill: #e74c3c;"); }
    private void showSuccess(String s) { lblMensagem.setText(s); lblMensagem.setStyle("-fx-text-fill: #27ae60;"); }
}
