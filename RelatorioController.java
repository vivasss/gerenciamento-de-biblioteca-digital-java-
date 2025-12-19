package com.biblioteca.controller;

import com.biblioteca.dao.*;
import com.biblioteca.model.Emprestimo;
import com.biblioteca.utils.LogManager;
import com.biblioteca.utils.PDFGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
import java.util.Map;

/**
 * Controller para geração de relatórios.
 */
public class RelatorioController {
    
    @FXML private Spinner<Integer> spnLimite;
    @FXML private Label lblMensagem;
    @FXML private TextArea txtPreview;
    
    private LivroDAO livroDAO = new LivroDAO();
    private EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    
    @FXML
    public void initialize() {
        spnLimite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 10));
    }
    
    @FXML
    private void handleLivrosMaisEmprestados() {
        try {
            List<Map<String, Object>> dados = livroDAO.livrosMaisEmprestados(spnLimite.getValue());
            StringBuilder preview = new StringBuilder("=== LIVROS MAIS EMPRESTADOS ===\n\n");
            int i = 1;
            for (Map<String, Object> row : dados) {
                preview.append(String.format("%d. %s - %s (%d empréstimos)\n",
                        i++, row.get("titulo"), row.get("autor"), row.get("total_emprestimos")));
            }
            txtPreview.setText(preview.toString());
            
            String path = PDFGenerator.gerarRelatorioLivrosMaisEmprestados(dados);
            showSuccess("Relatório gerado: " + path);
            PDFGenerator.openPDF(path);
            LogManager.logUserAction(SessionManager.getUsuarioLogado().getId(), "RELATORIO", "Livros mais emprestados");
        } catch (Exception e) { showError("Erro ao gerar relatório."); LogManager.error("Erro relatório", e); }
    }
    
    @FXML
    private void handleUsuariosMaisEmprestimos() {
        try {
            List<Map<String, Object>> dados = emprestimoDAO.usuariosMaisEmprestimos(spnLimite.getValue());
            StringBuilder preview = new StringBuilder("=== USUÁRIOS COM MAIS EMPRÉSTIMOS ===\n\n");
            int i = 1;
            for (Map<String, Object> row : dados) {
                preview.append(String.format("%d. %s (%s) - %d empréstimos\n",
                        i++, row.get("nome"), row.get("tipo"), row.get("total_emprestimos")));
            }
            txtPreview.setText(preview.toString());
            
            String path = PDFGenerator.gerarRelatorioUsuariosMaisEmprestimos(dados);
            showSuccess("Relatório gerado: " + path);
            PDFGenerator.openPDF(path);
            LogManager.logUserAction(SessionManager.getUsuarioLogado().getId(), "RELATORIO", "Usuários mais empréstimos");
        } catch (Exception e) { showError("Erro ao gerar relatório."); LogManager.error("Erro relatório", e); }
    }
    
    @FXML
    private void handleEmprestimosAtrasados() {
        try {
            List<Emprestimo> dados = emprestimoDAO.listarAtrasados();
            StringBuilder preview = new StringBuilder("=== EMPRÉSTIMOS ATRASADOS ===\n\n");
            preview.append("Total: ").append(dados.size()).append(" empréstimos\n\n");
            for (Emprestimo e : dados) {
                preview.append(String.format("- %s: %s (%d dias de atraso)\n",
                        e.getUsuarioNome(), e.getLivroTitulo(), e.getDiasAtraso()));
            }
            txtPreview.setText(preview.toString());
            
            String path = PDFGenerator.gerarRelatorioEmprestimosAtrasados(dados);
            showSuccess("Relatório gerado: " + path);
            PDFGenerator.openPDF(path);
            LogManager.logUserAction(SessionManager.getUsuarioLogado().getId(), "RELATORIO", "Empréstimos atrasados");
        } catch (Exception e) { showError("Erro ao gerar relatório."); LogManager.error("Erro relatório", e); }
    }
    
    private void showError(String s) { lblMensagem.setText(s); lblMensagem.setStyle("-fx-text-fill: #e74c3c;"); }
    private void showSuccess(String s) { lblMensagem.setText(s); lblMensagem.setStyle("-fx-text-fill: #27ae60;"); }
}
