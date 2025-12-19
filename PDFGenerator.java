package com.biblioteca.utils;

import com.biblioteca.model.Emprestimo;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Classe utilitária para geração de relatórios em PDF.
 * 
 * <p>Gera relatórios para:</p>
 * <ul>
 *   <li>Livros mais emprestados</li>
 *   <li>Usuários com mais empréstimos</li>
 *   <li>Empréstimos atrasados</li>
 * </ul>
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class PDFGenerator {
    
    /** Diretório para salvar os relatórios */
    private static final String REPORTS_DIR = "relatorios";
    
    /** Formato de data para os relatórios */
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /** Formato de data/hora para nomes de arquivo */
    private static final DateTimeFormatter FILE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    // Cores padrão
    private static final BaseColor HEADER_COLOR = new BaseColor(41, 128, 185);
    private static final BaseColor ALTERNATE_ROW_COLOR = new BaseColor(245, 245, 245);
    
    // Fontes
    private static Font TITLE_FONT;
    private static Font SUBTITLE_FONT;
    private static Font HEADER_FONT;
    private static Font CONTENT_FONT;
    private static Font FOOTER_FONT;
    
    static {
        try {
            TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, HEADER_COLOR);
            SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
            HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
            CONTENT_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
            FOOTER_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
        } catch (Exception e) {
            LogManager.error("Erro ao inicializar fontes PDF", e);
        }
        
        // Criar diretório de relatórios
        File dir = new File(REPORTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Construtor privado para evitar instanciação.
     */
    private PDFGenerator() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }
    
    /**
     * Gera relatório de livros mais emprestados.
     * 
     * @param dados Lista de mapas com titulo, autor e total_emprestimos
     * @return Caminho do arquivo gerado
     * @throws DocumentException Se houver erro na criação do PDF
     * @throws IOException Se houver erro de I/O
     */
    public static String gerarRelatorioLivrosMaisEmprestados(List<Map<String, Object>> dados) 
            throws DocumentException, IOException {
        
        String fileName = "livros_mais_emprestados_" + 
                LocalDateTime.now().format(FILE_FORMATTER) + ".pdf";
        String filePath = REPORTS_DIR + File.separator + fileName;
        
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        // Título
        addTitle(document, "Relatório de Livros Mais Emprestados");
        addSubtitle(document, "Gerado em: " + LocalDateTime.now().format(DATE_FORMATTER));
        document.add(Chunk.NEWLINE);
        
        // Tabela
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 4, 3, 2});
        
        // Cabeçalho
        addTableHeader(table, "Nº", "Título", "Autor", "Empréstimos");
        
        // Dados
        int i = 1;
        for (Map<String, Object> row : dados) {
            boolean alternate = i % 2 == 0;
            addTableCell(table, String.valueOf(i), alternate);
            addTableCell(table, (String) row.get("titulo"), alternate);
            addTableCell(table, (String) row.get("autor"), alternate);
            addTableCell(table, String.valueOf(row.get("total_emprestimos")), alternate);
            i++;
        }
        
        document.add(table);
        addFooter(document);
        document.close();
        
        LogManager.info("Relatório gerado: " + filePath);
        return filePath;
    }
    
    /**
     * Gera relatório de usuários com mais empréstimos.
     * 
     * @param dados Lista de mapas com nome, email, tipo e total_emprestimos
     * @return Caminho do arquivo gerado
     * @throws DocumentException Se houver erro na criação do PDF
     * @throws IOException Se houver erro de I/O
     */
    public static String gerarRelatorioUsuariosMaisEmprestimos(List<Map<String, Object>> dados) 
            throws DocumentException, IOException {
        
        String fileName = "usuarios_mais_emprestimos_" + 
                LocalDateTime.now().format(FILE_FORMATTER) + ".pdf";
        String filePath = REPORTS_DIR + File.separator + fileName;
        
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        // Título
        addTitle(document, "Relatório de Usuários com Mais Empréstimos");
        addSubtitle(document, "Gerado em: " + LocalDateTime.now().format(DATE_FORMATTER));
        document.add(Chunk.NEWLINE);
        
        // Tabela
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 4, 2, 2});
        
        // Cabeçalho
        addTableHeader(table, "Nº", "Nome", "E-mail", "Tipo", "Empréstimos");
        
        // Dados
        int i = 1;
        for (Map<String, Object> row : dados) {
            boolean alternate = i % 2 == 0;
            addTableCell(table, String.valueOf(i), alternate);
            addTableCell(table, (String) row.get("nome"), alternate);
            addTableCell(table, (String) row.get("email"), alternate);
            addTableCell(table, (String) row.get("tipo"), alternate);
            addTableCell(table, String.valueOf(row.get("total_emprestimos")), alternate);
            i++;
        }
        
        document.add(table);
        addFooter(document);
        document.close();
        
        LogManager.info("Relatório gerado: " + filePath);
        return filePath;
    }
    
    /**
     * Gera relatório de empréstimos atrasados.
     * 
     * @param emprestimos Lista de empréstimos atrasados
     * @return Caminho do arquivo gerado
     * @throws DocumentException Se houver erro na criação do PDF
     * @throws IOException Se houver erro de I/O
     */
    public static String gerarRelatorioEmprestimosAtrasados(List<Emprestimo> emprestimos) 
            throws DocumentException, IOException {
        
        String fileName = "emprestimos_atrasados_" + 
                LocalDateTime.now().format(FILE_FORMATTER) + ".pdf";
        String filePath = REPORTS_DIR + File.separator + fileName;
        
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        // Título
        addTitle(document, "Relatório de Empréstimos Atrasados");
        addSubtitle(document, "Gerado em: " + LocalDateTime.now().format(DATE_FORMATTER));
        
        // Resumo
        Paragraph resumo = new Paragraph();
        resumo.add(new Chunk("Total de empréstimos atrasados: ", SUBTITLE_FONT));
        resumo.add(new Chunk(String.valueOf(emprestimos.size()), 
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.RED)));
        document.add(resumo);
        document.add(Chunk.NEWLINE);
        
        // Tabela
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 3, 3, 2, 2, 1.5f});
        
        // Cabeçalho
        addTableHeader(table, "ID", "Usuário", "E-mail", "Livro", "Empréstimo", "Vencimento", "Dias Atraso");
        
        // Dados
        int i = 0;
        for (Emprestimo emp : emprestimos) {
            boolean alternate = i % 2 == 0;
            addTableCell(table, String.valueOf(emp.getId()), alternate);
            addTableCell(table, emp.getUsuarioNome(), alternate);
            addTableCell(table, emp.getUsuarioEmail(), alternate);
            addTableCell(table, emp.getLivroTitulo(), alternate);
            addTableCell(table, emp.getDataEmprestimo().format(DATE_FORMATTER), alternate);
            addTableCell(table, emp.getDataDevolucaoPrevista().format(DATE_FORMATTER), alternate);
            
            // Dias de atraso em vermelho
            PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(emp.getDiasAtraso()), 
                    new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.RED)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            if (alternate) {
                cell.setBackgroundColor(ALTERNATE_ROW_COLOR);
            }
            table.addCell(cell);
            i++;
        }
        
        document.add(table);
        addFooter(document);
        document.close();
        
        LogManager.info("Relatório gerado: " + filePath);
        return filePath;
    }
    
    /**
     * Adiciona um título ao documento.
     */
    private static void addTitle(Document document, String text) throws DocumentException {
        Paragraph title = new Paragraph(text, TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);
    }
    
    /**
     * Adiciona um subtítulo ao documento.
     */
    private static void addSubtitle(Document document, String text) throws DocumentException {
        Paragraph subtitle = new Paragraph(text, SUBTITLE_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);
    }
    
    /**
     * Adiciona cabeçalho à tabela.
     */
    private static void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }
    
    /**
     * Adiciona célula à tabela.
     */
    private static void addTableCell(PdfPTable table, String text, boolean alternate) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", CONTENT_FONT));
        cell.setPadding(5);
        if (alternate) {
            cell.setBackgroundColor(ALTERNATE_ROW_COLOR);
        }
        table.addCell(cell);
    }
    
    /**
     * Adiciona rodapé ao documento.
     */
    private static void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        Paragraph footer = new Paragraph(
            "Sistema de Biblioteca Digital - Relatório gerado automaticamente", FOOTER_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
    
    /**
     * Retorna o diretório de relatórios.
     * 
     * @return Caminho do diretório
     */
    public static String getReportsDir() {
        return REPORTS_DIR;
    }
    
    /**
     * Abre o arquivo PDF no visualizador padrão do sistema.
     * 
     * @param filePath Caminho do arquivo
     */
    public static void openPDF(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                java.awt.Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            LogManager.error("Erro ao abrir PDF", e);
        }
    }
}
