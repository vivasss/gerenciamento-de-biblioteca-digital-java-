package com.biblioteca.utils;

import com.biblioteca.dao.EmprestimoDAO;
import com.biblioteca.model.Emprestimo;
import java.util.List;

/**
 * Thread para verificação automática de empréstimos próximos do vencimento.
 * 
 * <p>Executa periodicamente para identificar e registrar notificações
 * de empréstimos que estão prestes a vencer ou já estão atrasados.</p>
 * 
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class NotificationThread extends Thread {
    
    /** Intervalo entre verificações em milissegundos (1 hora) */
    private static final long CHECK_INTERVAL = 60 * 60 * 1000; // 1 hora
    
    /** Flag para controlar a execução da thread */
    private volatile boolean running = true;
    
    /** Dias de antecedência para notificar */
    private static final int DIAS_ANTECEDENCIA = 2;
    
    /**
     * Construtor padrão.
     * Configura a thread como daemon para ser encerrada com a aplicação.
     */
    public NotificationThread() {
        setDaemon(true);
        setName("NotificationThread");
        setPriority(Thread.MIN_PRIORITY);
    }
    
    /**
     * Execução principal da thread.
     * Verifica periodicamente os empréstimos e gera notificações.
     */
    @Override
    public void run() {
        LogManager.info("Thread de notificações iniciada.");
        
        while (running) {
            try {
                // Aguardar intervalo
                Thread.sleep(CHECK_INTERVAL);
                
                // Verificar empréstimos
                if (running) {
                    verificarEmprestimos();
                }
                
            } catch (InterruptedException e) {
                LogManager.info("Thread de notificações interrompida.");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                LogManager.error("Erro na thread de notificações", e);
            }
        }
        
        LogManager.info("Thread de notificações finalizada.");
    }
    
    /**
     * Verifica os empréstimos e gera notificações apropriadas.
     */
    private void verificarEmprestimos() {
        LogManager.info("Iniciando verificação de empréstimos...");
        
        try {
            EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
            
            // Verificar empréstimos atrasados
            List<Emprestimo> atrasados = emprestimoDAO.listarAtrasados();
            if (!atrasados.isEmpty()) {
                LogManager.warning("Encontrados " + atrasados.size() + " empréstimos atrasados.");
                for (Emprestimo emp : atrasados) {
                    String msg = String.format(
                        "NOTIFICAÇÃO: Empréstimo ID %d atrasado - Usuário: %s, Livro: %s, Dias de atraso: %d",
                        emp.getId(), emp.getUsuarioNome(), emp.getLivroTitulo(), emp.getDiasAtraso());
                    LogManager.warning(msg);
                }
            }
            
            // Verificar empréstimos próximos do vencimento
            List<Emprestimo> proximosVencimento = emprestimoDAO.listarProximosVencimento(DIAS_ANTECEDENCIA);
            if (!proximosVencimento.isEmpty()) {
                LogManager.info("Encontrados " + proximosVencimento.size() + " empréstimos próximos do vencimento.");
                for (Emprestimo emp : proximosVencimento) {
                    String msg = String.format(
                        "NOTIFICAÇÃO: Empréstimo ID %d vence em %d dias - Usuário: %s, Livro: %s",
                        emp.getId(), emp.getDiasRestantes(), emp.getUsuarioNome(), emp.getLivroTitulo());
                    LogManager.info(msg);
                }
            }
            
            // Atualizar status de empréstimos atrasados no banco
            int atualizados = emprestimoDAO.atualizarStatusAtrasados();
            if (atualizados > 0) {
                LogManager.info("Atualizados " + atualizados + " empréstimos para status ATRASADO.");
            }
            
            LogManager.info("Verificação de empréstimos concluída.");
            
        } catch (Exception e) {
            LogManager.error("Erro ao verificar empréstimos", e);
        }
    }
    
    /**
     * Força uma verificação imediata dos empréstimos.
     */
    public void verificarAgora() {
        LogManager.info("Verificação manual de empréstimos solicitada.");
        verificarEmprestimos();
    }
    
    /**
     * Para a execução da thread de forma segura.
     */
    public void stopThread() {
        running = false;
        this.interrupt();
    }
    
    /**
     * Verifica se a thread está em execução.
     * 
     * @return true se estiver em execução
     */
    public boolean isRunning() {
        return running && isAlive();
    }
}
