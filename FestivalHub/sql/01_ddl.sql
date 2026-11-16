-- =====================================================================
-- FestivalHub - Sistema de Gestao de Festivais de Musica
-- Script DDL (MySQL 8)
-- Gabriel Leonidas e Alisson Paixao - UCSAL - Banco de Dados
-- =====================================================================

DROP DATABASE IF EXISTS festivalhub;
CREATE DATABASE festivalhub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE festivalhub;

-- ---------------------------------------------------------------------
-- 1) TABELAS (CREATE TABLE + PK + FK + CONSTRAINTS)
-- ---------------------------------------------------------------------

CREATE TABLE festival (
    id_festival    INT          NOT NULL AUTO_INCREMENT,
    nome_festival  VARCHAR(100) NOT NULL,
    local          VARCHAR(100) NOT NULL,
    data_inicio    DATE         NOT NULL,
    data_fim       DATE         NOT NULL,
    CONSTRAINT pk_festival PRIMARY KEY (id_festival),
    CONSTRAINT uq_festival_nome UNIQUE (nome_festival, data_inicio),
    CONSTRAINT ck_festival_datas CHECK (data_fim >= data_inicio)
);

CREATE TABLE palco (
    id_palco     INT          NOT NULL AUTO_INCREMENT,
    nome_palco   VARCHAR(100) NOT NULL,
    capacidade   INT          NOT NULL,
    id_festival  INT          NOT NULL,
    CONSTRAINT pk_palco PRIMARY KEY (id_palco),
    CONSTRAINT fk_palco_festival FOREIGN KEY (id_festival)
        REFERENCES festival (id_festival) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uq_palco_nome UNIQUE (id_festival, nome_palco),
    CONSTRAINT ck_palco_capacidade CHECK (capacidade > 0)
);

CREATE TABLE artista (
    id_artista      INT          NOT NULL AUTO_INCREMENT,
    nome_artista    VARCHAR(100) NOT NULL,
    genero_musical  VARCHAR(50)  NOT NULL,
    CONSTRAINT pk_artista PRIMARY KEY (id_artista),
    CONSTRAINT uq_artista_nome UNIQUE (nome_artista)
);

CREATE TABLE apresentacao (
    id_apresentacao    INT  NOT NULL AUTO_INCREMENT,
    data_apresentacao  DATE NOT NULL,
    hora_inicio        TIME NOT NULL,
    hora_fim           TIME NOT NULL,
    id_artista         INT  NOT NULL,
    id_palco           INT  NOT NULL,
    CONSTRAINT pk_apresentacao PRIMARY KEY (id_apresentacao),
    CONSTRAINT fk_apresentacao_artista FOREIGN KEY (id_artista)
        REFERENCES artista (id_artista) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_apresentacao_palco FOREIGN KEY (id_palco)
        REFERENCES palco (id_palco) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_apresentacao_horario CHECK (hora_fim > hora_inicio)
);

CREATE TABLE participante (
    id_participante    INT          NOT NULL AUTO_INCREMENT,
    nome_participante  VARCHAR(100) NOT NULL,
    cpf                CHAR(11)     NOT NULL,
    email              VARCHAR(100) NOT NULL,
    CONSTRAINT pk_participante PRIMARY KEY (id_participante),
    CONSTRAINT uq_participante_cpf UNIQUE (cpf),
    CONSTRAINT uq_participante_email UNIQUE (email),
    CONSTRAINT ck_participante_cpf CHECK (cpf REGEXP '^[0-9]{11}$')
);

CREATE TABLE ingresso (
    id_ingresso      INT           NOT NULL AUTO_INCREMENT,
    tipo             VARCHAR(20)   NOT NULL,
    preco            DECIMAL(10,2) NOT NULL,
    status           VARCHAR(20)   NOT NULL DEFAULT 'ativo',
    id_participante  INT           NOT NULL,
    id_festival      INT           NOT NULL,
    CONSTRAINT pk_ingresso PRIMARY KEY (id_ingresso),
    CONSTRAINT fk_ingresso_participante FOREIGN KEY (id_participante)
        REFERENCES participante (id_participante) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_ingresso_festival FOREIGN KEY (id_festival)
        REFERENCES festival (id_festival) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_ingresso_tipo   CHECK (tipo IN ('pista', 'meia', 'vip', 'camarote')),
    CONSTRAINT ck_ingresso_preco  CHECK (preco >= 0),
    CONSTRAINT ck_ingresso_status CHECK (status IN ('ativo', 'utilizado', 'cancelado'))
);

-- Tabela associativa (resolve o N:N Festival x Participante)
CREATE TABLE participacao (
    id_festival      INT  NOT NULL,
    id_participante  INT  NOT NULL,
    data_compra      DATE NOT NULL,
    CONSTRAINT pk_participacao PRIMARY KEY (id_festival, id_participante),
    CONSTRAINT fk_participacao_festival FOREIGN KEY (id_festival)
        REFERENCES festival (id_festival) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_participacao_participante FOREIGN KEY (id_participante)
        REFERENCES participante (id_participante) ON UPDATE CASCADE ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 2) VIEW - programacao completa (festival + palco + show + artista)
-- ---------------------------------------------------------------------
CREATE VIEW vw_programacao AS
SELECT  f.id_festival,
        f.nome_festival,
        p.nome_palco,
        a.id_apresentacao,
        a.data_apresentacao,
        a.hora_inicio,
        a.hora_fim,
        ar.nome_artista,
        ar.genero_musical
FROM festival f
JOIN palco        p  ON p.id_festival = f.id_festival
JOIN apresentacao a  ON a.id_palco    = p.id_palco
JOIN artista      ar ON ar.id_artista = a.id_artista;

-- ---------------------------------------------------------------------
-- 3) FUNCTIONS (calculo real)
-- ---------------------------------------------------------------------
DELIMITER $$

-- Faturamento do festival: soma dos precos dos ingressos NAO cancelados (RN4)
CREATE FUNCTION fn_faturamento_festival(p_id_festival INT)
RETURNS DECIMAL(12,2)
READS SQL DATA
BEGIN
    DECLARE v_total DECIMAL(12,2);
    SELECT COALESCE(SUM(preco), 0) INTO v_total
      FROM ingresso
     WHERE id_festival = p_id_festival
       AND status <> 'cancelado';
    RETURN v_total;
END$$

-- Percentual de ocupacao: ingressos validos / capacidade total dos palcos * 100
CREATE FUNCTION fn_percentual_ocupacao(p_id_festival INT)
RETURNS DECIMAL(6,2)
READS SQL DATA
BEGIN
    DECLARE v_capacidade INT;
    DECLARE v_vendidos   INT;

    SELECT COALESCE(SUM(capacidade), 0) INTO v_capacidade
      FROM palco WHERE id_festival = p_id_festival;

    SELECT COUNT(*) INTO v_vendidos
      FROM ingresso
     WHERE id_festival = p_id_festival AND status <> 'cancelado';

    IF v_capacidade = 0 THEN
        RETURN 0;
    END IF;
    RETURN ROUND(v_vendidos * 100.0 / v_capacidade, 2);
END$$

-- ---------------------------------------------------------------------
-- 4) PROCEDURES (acoes relevantes com regras de negocio)
-- ---------------------------------------------------------------------

-- Vende um ingresso (RN3: nao pode passar da capacidade do festival)
-- e registra a participacao do comprador no festival.
CREATE PROCEDURE sp_vender_ingresso(
    IN  p_id_participante INT,
    IN  p_id_festival     INT,
    IN  p_tipo            VARCHAR(20),
    IN  p_preco           DECIMAL(10,2),
    OUT p_id_ingresso     INT
)
BEGIN
    DECLARE v_capacidade INT;
    DECLARE v_vendidos   INT;

    SELECT COALESCE(SUM(capacidade), 0) INTO v_capacidade
      FROM palco WHERE id_festival = p_id_festival;

    SELECT COUNT(*) INTO v_vendidos
      FROM ingresso
     WHERE id_festival = p_id_festival AND status <> 'cancelado';

    IF v_capacidade = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Festival sem palcos cadastrados: venda bloqueada.';
    END IF;

    IF v_vendidos >= v_capacidade THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ingressos esgotados: capacidade do festival atingida.';
    END IF;

    START TRANSACTION;
        INSERT INTO ingresso (tipo, preco, status, id_participante, id_festival)
        VALUES (p_tipo, p_preco, 'ativo', p_id_participante, p_id_festival);
        SET p_id_ingresso = LAST_INSERT_ID();

        INSERT IGNORE INTO participacao (id_festival, id_participante, data_compra)
        VALUES (p_id_festival, p_id_participante, CURRENT_DATE);
    COMMIT;
END$$

-- Agenda um show (RN1: sem choque de horario no mesmo palco;
--                 RN2: data dentro do periodo do festival)
CREATE PROCEDURE sp_agendar_apresentacao(
    IN  p_id_artista  INT,
    IN  p_id_palco    INT,
    IN  p_data        DATE,
    IN  p_hora_inicio TIME,
    IN  p_hora_fim    TIME,
    OUT p_id_apresentacao INT
)
BEGIN
    DECLARE v_inicio_fest DATE;
    DECLARE v_fim_fest    DATE;
    DECLARE v_conflitos   INT;

    IF p_hora_fim <= p_hora_inicio THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Hora de fim deve ser maior que a hora de inicio.';
    END IF;

    SELECT f.data_inicio, f.data_fim INTO v_inicio_fest, v_fim_fest
      FROM palco p JOIN festival f ON f.id_festival = p.id_festival
     WHERE p.id_palco = p_id_palco;

    IF v_inicio_fest IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Palco inexistente.';
    END IF;

    IF p_data NOT BETWEEN v_inicio_fest AND v_fim_fest THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Data fora do periodo do festival.';
    END IF;

    -- dois intervalos se sobrepoem quando um comeca antes do outro terminar
    SELECT COUNT(*) INTO v_conflitos
      FROM apresentacao
     WHERE id_palco = p_id_palco
       AND data_apresentacao = p_data
       AND p_hora_inicio < hora_fim
       AND p_hora_fim    > hora_inicio;

    IF v_conflitos > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Choque de horario: ja existe show neste palco no horario.';
    END IF;

    INSERT INTO apresentacao (data_apresentacao, hora_inicio, hora_fim, id_artista, id_palco)
    VALUES (p_data, p_hora_inicio, p_hora_fim, p_id_artista, p_id_palco);
    SET p_id_apresentacao = LAST_INSERT_ID();
END$$

DELIMITER ;
