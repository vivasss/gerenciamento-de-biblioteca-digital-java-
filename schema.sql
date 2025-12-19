-- ============================================
-- SISTEMA DE BIBLIOTECA DIGITAL
-- Script de Criação do Banco de Dados MySQL
-- ============================================

-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS biblioteca_digital
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE biblioteca_digital;

-- ============================================
-- TABELA: categorias
-- Armazena as categorias de livros
-- ============================================
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================
-- TABELA: usuarios
-- Armazena os usuários do sistema
-- ============================================
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo ENUM('ALUNO', 'PROFESSOR', 'ADMINISTRADOR') NOT NULL DEFAULT 'ALUNO',
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB;

-- ============================================
-- TABELA: livros
-- Armazena o catálogo de livros
-- ============================================
CREATE TABLE IF NOT EXISTS livros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(300) NOT NULL,
    autor VARCHAR(200) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    categoria_id INT NOT NULL,
    quantidade_total INT NOT NULL DEFAULT 1,
    quantidade_disponivel INT NOT NULL DEFAULT 1,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_livro_categoria FOREIGN KEY (categoria_id) 
        REFERENCES categorias(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_isbn (isbn),
    INDEX idx_titulo (titulo),
    INDEX idx_autor (autor),
    CONSTRAINT chk_quantidade CHECK (quantidade_disponivel >= 0 AND quantidade_disponivel <= quantidade_total)
) ENGINE=InnoDB;

-- ============================================
-- TABELA: emprestimos
-- Armazena os empréstimos de livros
-- ============================================
CREATE TABLE IF NOT EXISTS emprestimos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    livro_id INT NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_devolucao_prevista DATE NOT NULL,
    data_devolucao_real DATE NULL,
    status ENUM('ATIVO', 'DEVOLVIDO', 'ATRASADO') NOT NULL DEFAULT 'ATIVO',
    observacoes TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_emprestimo_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_emprestimo_livro FOREIGN KEY (livro_id) 
        REFERENCES livros(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_usuario (usuario_id),
    INDEX idx_livro (livro_id),
    INDEX idx_status (status),
    INDEX idx_data_devolucao (data_devolucao_prevista)
) ENGINE=InnoDB;

-- ============================================
-- TABELA: logs_atividades
-- Armazena logs de atividades do sistema
-- ============================================
CREATE TABLE IF NOT EXISTS logs_atividades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NULL,
    acao VARCHAR(100) NOT NULL,
    descricao TEXT,
    ip_address VARCHAR(45),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_acao (acao),
    INDEX idx_criado_em (criado_em)
) ENGINE=InnoDB;

-- ============================================
-- DADOS INICIAIS
-- ============================================

-- Inserir categorias padrão
INSERT INTO categorias (nome, descricao) VALUES
    ('Ficção', 'Livros de ficção, romances e contos'),
    ('Não-Ficção', 'Biografias, história e documentários'),
    ('Ciência', 'Livros científicos e acadêmicos'),
    ('Tecnologia', 'Programação, computação e tecnologia'),
    ('Literatura', 'Clássicos da literatura mundial'),
    ('Infantil', 'Livros para crianças e jovens'),
    ('Didático', 'Material didático e educacional'),
    ('Autoajuda', 'Desenvolvimento pessoal e profissional');

-- Inserir usuário administrador padrão
-- Senha: admin123 (hash BCrypt)
INSERT INTO usuarios (nome, email, senha, tipo, ativo) VALUES
    ('Administrador', 'admin@biblioteca.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqP0.cFvYQC1LH/VKfYxdGZvRH6Vy', 'ADMINISTRADOR', TRUE);

-- Inserir alguns livros de exemplo
INSERT INTO livros (titulo, autor, isbn, categoria_id, quantidade_total, quantidade_disponivel) VALUES
    ('Dom Casmurro', 'Machado de Assis', '978-8535910663', 5, 5, 5),
    ('O Senhor dos Anéis', 'J.R.R. Tolkien', '978-8595084759', 1, 3, 3),
    ('Clean Code', 'Robert C. Martin', '978-0132350884', 4, 2, 2),
    ('Sapiens', 'Yuval Noah Harari', '978-8525432186', 2, 4, 4),
    ('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', '978-8595081512', 6, 6, 6);

-- ============================================
-- VIEWS ÚTEIS
-- ============================================

-- View: Livros com informações de categoria
CREATE OR REPLACE VIEW vw_livros_completos AS
SELECT 
    l.id,
    l.titulo,
    l.autor,
    l.isbn,
    c.nome AS categoria,
    l.quantidade_total,
    l.quantidade_disponivel,
    l.criado_em
FROM livros l
INNER JOIN categorias c ON l.categoria_id = c.id;

-- View: Empréstimos com informações completas
CREATE OR REPLACE VIEW vw_emprestimos_completos AS
SELECT 
    e.id,
    u.nome AS usuario_nome,
    u.email AS usuario_email,
    u.tipo AS usuario_tipo,
    l.titulo AS livro_titulo,
    l.autor AS livro_autor,
    e.data_emprestimo,
    e.data_devolucao_prevista,
    e.data_devolucao_real,
    e.status,
    CASE 
        WHEN e.status = 'ATIVO' AND e.data_devolucao_prevista < CURDATE() THEN 'ATRASADO'
        ELSE e.status
    END AS status_atual
FROM emprestimos e
INNER JOIN usuarios u ON e.usuario_id = u.id
INNER JOIN livros l ON e.livro_id = l.id;

-- View: Estatísticas de empréstimos por livro
CREATE OR REPLACE VIEW vw_livros_mais_emprestados AS
SELECT 
    l.id,
    l.titulo,
    l.autor,
    COUNT(e.id) AS total_emprestimos
FROM livros l
LEFT JOIN emprestimos e ON l.id = e.livro_id
GROUP BY l.id, l.titulo, l.autor
ORDER BY total_emprestimos DESC;

-- View: Estatísticas de empréstimos por usuário
CREATE OR REPLACE VIEW vw_usuarios_mais_emprestimos AS
SELECT 
    u.id,
    u.nome,
    u.email,
    u.tipo,
    COUNT(e.id) AS total_emprestimos
FROM usuarios u
LEFT JOIN emprestimos e ON u.id = e.usuario_id
GROUP BY u.id, u.nome, u.email, u.tipo
ORDER BY total_emprestimos DESC;

-- ============================================
-- STORED PROCEDURES
-- ============================================

DELIMITER //

-- Procedure: Realizar empréstimo
CREATE PROCEDURE sp_realizar_emprestimo(
    IN p_usuario_id INT,
    IN p_livro_id INT,
    IN p_dias_emprestimo INT
)
BEGIN
    DECLARE v_quantidade_disponivel INT;
    
    -- Verificar disponibilidade
    SELECT quantidade_disponivel INTO v_quantidade_disponivel
    FROM livros WHERE id = p_livro_id FOR UPDATE;
    
    IF v_quantidade_disponivel > 0 THEN
        -- Criar empréstimo
        INSERT INTO emprestimos (usuario_id, livro_id, data_emprestimo, data_devolucao_prevista, status)
        VALUES (p_usuario_id, p_livro_id, CURDATE(), DATE_ADD(CURDATE(), INTERVAL p_dias_emprestimo DAY), 'ATIVO');
        
        -- Atualizar quantidade disponível
        UPDATE livros SET quantidade_disponivel = quantidade_disponivel - 1 WHERE id = p_livro_id;
        
        SELECT 'SUCCESS' AS resultado, LAST_INSERT_ID() AS emprestimo_id;
    ELSE
        SELECT 'ERROR' AS resultado, 'Livro não disponível' AS mensagem;
    END IF;
END //

-- Procedure: Devolver livro
CREATE PROCEDURE sp_devolver_livro(
    IN p_emprestimo_id INT
)
BEGIN
    DECLARE v_livro_id INT;
    DECLARE v_status VARCHAR(20);
    
    -- Buscar informações do empréstimo
    SELECT livro_id, status INTO v_livro_id, v_status
    FROM emprestimos WHERE id = p_emprestimo_id;
    
    IF v_status = 'ATIVO' OR v_status = 'ATRASADO' THEN
        -- Atualizar empréstimo
        UPDATE emprestimos 
        SET data_devolucao_real = CURDATE(), status = 'DEVOLVIDO'
        WHERE id = p_emprestimo_id;
        
        -- Atualizar quantidade disponível
        UPDATE livros SET quantidade_disponivel = quantidade_disponivel + 1 WHERE id = v_livro_id;
        
        SELECT 'SUCCESS' AS resultado;
    ELSE
        SELECT 'ERROR' AS resultado, 'Empréstimo já devolvido' AS mensagem;
    END IF;
END //

-- Procedure: Atualizar status de empréstimos atrasados
CREATE PROCEDURE sp_atualizar_emprestimos_atrasados()
BEGIN
    UPDATE emprestimos 
    SET status = 'ATRASADO'
    WHERE status = 'ATIVO' AND data_devolucao_prevista < CURDATE();
    
    SELECT ROW_COUNT() AS emprestimos_atualizados;
END //

DELIMITER ;

-- ============================================
-- EVENT SCHEDULER (opcional - requer ativação)
-- ============================================
-- SET GLOBAL event_scheduler = ON;

-- CREATE EVENT IF NOT EXISTS evt_atualizar_atrasados
-- ON SCHEDULE EVERY 1 HOUR
-- DO CALL sp_atualizar_emprestimos_atrasados();

-- ============================================
-- FIM DO SCRIPT
-- ============================================
