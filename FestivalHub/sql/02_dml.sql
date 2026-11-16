-- =====================================================================
-- FestivalHub - Script DML (MySQL 8)
-- Executar depois do 01_ddl.sql
-- =====================================================================
USE festivalhub;

-- =====================================================================
-- 1) INSERT
-- =====================================================================
INSERT INTO festival (nome_festival, local, data_inicio, data_fim) VALUES
 ('Festival Mare Alta',    'Parque de Exposicoes - Salvador', '2027-01-22', '2027-01-24'),
 ('Rock na Concha',        'Concha Acustica - Salvador',      '2027-03-12', '2027-03-13'),
 ('Sarau do Rio Vermelho', 'Largo de Santana - Salvador',     '2027-04-10', '2027-04-10');

INSERT INTO palco (nome_palco, capacidade, id_festival) VALUES
 ('Palco Principal',  20000, 1),
 ('Palco Arena',       8000, 1),
 ('Palco Eletronico',  5000, 1),
 ('Palco Concha',      5000, 2),
 ('Palco Alternativo', 1500, 2),
 ('Palco Varanda',        5, 3);   -- capacidade pequena para demonstrar a RN3

INSERT INTO artista (nome_artista, genero_musical) VALUES
 ('Banda Mare Cheia',   'Axe'),          -- 1
 ('Tambores do Pelo',   'Samba-reggae'), -- 2
 ('Luna Ventura',       'MPB'),          -- 3
 ('Ferrugem Eletrica',  'Rock'),         -- 4
 ('Coletivo Dende',     'Eletronica'),   -- 5
 ('Trio Areia Branca',  'Forro'),        -- 6
 ('Nina Caetite',       'MPB'),          -- 7
 ('Pedra Bruta',        'Rock'),         -- 8
 ('DJ Farol',           'Eletronica'),   -- 9
 ('Quarteto Sem Palco', 'Jazz');         -- 10 (sem shows; usado no DELETE)

INSERT INTO participante (nome_participante, cpf, email) VALUES
 ('Ana Beatriz Souza', '11122233344', 'ana.souza@email.com'),       -- 1
 ('Bruno Lima Santos', '22233344455', 'bruno.santos@email.com'),    -- 2
 ('Carla Menezes',     '33344455566', 'carla.menezes@email.com'),   -- 3
 ('Diego Ferreira',    '44455566677', 'diego.ferreira@email.com'),  -- 4
 ('Eduarda Ribeiro',   '55566677788', 'eduarda.ribeiro@email.com'), -- 5
 ('Felipe Andrade',    '66677788899', 'felipe.andrade@email.com'),  -- 6
 ('Giovana Rocha',     '77788899900', 'giovana.rocha@email.com'),   -- 7
 ('Henrique Costa',    '88899900011', 'henrique.costa@email.com'),  -- 8
 ('Igor Teste',        '99900011122', 'igor.teste@email.com');      -- 9 (usado no DELETE)

-- Shows: inseridos pela procedure, que aplica RN1 (choque) e RN2 (datas)
-- Festival Mare Alta - Palco Principal (1)
CALL sp_agendar_apresentacao(1, 1, '2027-01-22', '20:00', '22:00', @id);
CALL sp_agendar_apresentacao(2, 1, '2027-01-22', '22:30', '23:59', @id);
CALL sp_agendar_apresentacao(3, 1, '2027-01-23', '19:00', '21:00', @id);
CALL sp_agendar_apresentacao(1, 1, '2027-01-23', '21:30', '23:30', @id);
CALL sp_agendar_apresentacao(7, 1, '2027-01-24', '19:00', '20:30', @id);
CALL sp_agendar_apresentacao(2, 1, '2027-01-24', '21:00', '23:00', @id);
-- Festival Mare Alta - Palco Arena (2)
CALL sp_agendar_apresentacao(6, 2, '2027-01-22', '18:00', '19:30', @id);
CALL sp_agendar_apresentacao(4, 2, '2027-01-23', '20:00', '22:00', @id);
CALL sp_agendar_apresentacao(6, 2, '2027-01-24', '18:00', '19:30', @id);
-- Festival Mare Alta - Palco Eletronico (3)
CALL sp_agendar_apresentacao(9, 3, '2027-01-22', '22:00', '23:59', @id);
CALL sp_agendar_apresentacao(5, 3, '2027-01-23', '22:00', '23:59', @id);
CALL sp_agendar_apresentacao(9, 3, '2027-01-24', '20:00', '22:00', @id);
-- Rock na Concha - Palco Concha (4) e Alternativo (5)
CALL sp_agendar_apresentacao(4, 4, '2027-03-12', '19:00', '21:00', @id);
CALL sp_agendar_apresentacao(8, 4, '2027-03-12', '21:30', '23:30', @id);
CALL sp_agendar_apresentacao(8, 4, '2027-03-13', '19:00', '21:00', @id);
CALL sp_agendar_apresentacao(4, 4, '2027-03-13', '21:30', '23:00', @id);
CALL sp_agendar_apresentacao(3, 5, '2027-03-12', '18:00', '19:30', @id);
-- Sarau do Rio Vermelho - Palco Varanda (6)
CALL sp_agendar_apresentacao(7, 6, '2027-04-10', '17:00', '18:30', @id);
CALL sp_agendar_apresentacao(3, 6, '2027-04-10', '19:00', '20:30', @id);

-- Ingressos: vendidos pela procedure (RN3 + registro em PARTICIPACAO)
-- Festival Mare Alta (1)
CALL sp_vender_ingresso(1, 1, 'pista',    180.00, @id);
CALL sp_vender_ingresso(1, 1, 'vip',      450.00, @id);
CALL sp_vender_ingresso(2, 1, 'pista',    180.00, @id);
CALL sp_vender_ingresso(2, 1, 'meia',      90.00, @id);
CALL sp_vender_ingresso(3, 1, 'vip',      450.00, @id);
CALL sp_vender_ingresso(4, 1, 'camarote', 800.00, @id);
CALL sp_vender_ingresso(5, 1, 'pista',    180.00, @id);
CALL sp_vender_ingresso(6, 1, 'meia',      90.00, @id);
CALL sp_vender_ingresso(7, 1, 'vip',      450.00, @id);
CALL sp_vender_ingresso(8, 1, 'pista',    180.00, @id);
-- Rock na Concha (2)
CALL sp_vender_ingresso(1, 2, 'pista',    120.00, @id);
CALL sp_vender_ingresso(2, 2, 'pista',    120.00, @id);
CALL sp_vender_ingresso(3, 2, 'meia',      60.00, @id);
CALL sp_vender_ingresso(4, 2, 'pista',    120.00, @id);
CALL sp_vender_ingresso(6, 2, 'vip',      300.00, @id);
CALL sp_vender_ingresso(8, 2, 'meia',      60.00, @id);
-- Sarau do Rio Vermelho (3) - capacidade 5: fica lotado
CALL sp_vender_ingresso(3, 3, 'pista',     50.00, @id);
CALL sp_vender_ingresso(5, 3, 'pista',     50.00, @id);
CALL sp_vender_ingresso(7, 3, 'meia',      25.00, @id);
CALL sp_vender_ingresso(8, 3, 'pista',     50.00, @id);
CALL sp_vender_ingresso(2, 3, 'pista',     50.00, @id);

-- ---------------------------------------------------------------------
-- Testes das regras de negocio (cada linha deve gerar ERRO 1644).
-- Descomente uma de cada vez para demonstrar na apresentacao.
-- ---------------------------------------------------------------------
-- RN1 - choque de horario no Palco Principal em 22/01 (20h-22h ja ocupado):
-- CALL sp_agendar_apresentacao(3, 1, '2027-01-22', '21:00', '22:30', @id);
-- RN2 - data fora do periodo do Festival Mare Alta (22 a 24/01):
-- CALL sp_agendar_apresentacao(3, 1, '2027-02-01', '20:00', '21:00', @id);
-- RN3 - Sarau do Rio Vermelho tem 5 lugares. Se rodar AGORA (5 vendidos) da erro.
--       Se rodar depois dos UPDATEs (Henrique cancela e libera 1 vaga),
--       a 1a chamada vende e a 2a da erro de "esgotado".
-- CALL sp_vender_ingresso(1, 3, 'pista', 50.00, @id);

-- =====================================================================
-- 2) UPDATE
-- =====================================================================
-- Participante entrou no festival: ingresso passa para 'utilizado'
UPDATE ingresso SET status = 'utilizado' WHERE id_ingresso IN (1, 3, 5);

-- Henrique cancelou o ingresso do Sarau (libera 1 vaga; sai do faturamento - RN4)
UPDATE ingresso SET status = 'cancelado'
 WHERE id_participante = 8 AND id_festival = 3;

-- Reajuste de 10% no preco dos ingressos VIP ainda ativos do Rock na Concha
UPDATE ingresso SET preco = ROUND(preco * 1.10, 2)
 WHERE id_festival = 2 AND tipo = 'vip' AND status = 'ativo';

-- Correcao cadastral
UPDATE participante SET email = 'bruno.lima@email.com' WHERE id_participante = 2;
UPDATE palco SET capacidade = 6000 WHERE id_palco = 3;

-- =====================================================================
-- 3) DELETE
-- =====================================================================
-- Artista sem shows pode ser removido (com shows, a FK RESTRICT impede)
DELETE FROM artista WHERE id_artista = 10;

-- Participante de teste sem ingressos
DELETE FROM participante WHERE id_participante = 9;

-- =====================================================================
-- 4) SELECT com JOIN (minimo 3)
-- =====================================================================
-- C1 - Programacao completa: festival, palco, show e artista
SELECT f.nome_festival, p.nome_palco, a.data_apresentacao,
       a.hora_inicio, a.hora_fim, ar.nome_artista
FROM apresentacao a
JOIN palco    p  ON p.id_palco    = a.id_palco
JOIN festival f  ON f.id_festival = p.id_festival
JOIN artista  ar ON ar.id_artista = a.id_artista
ORDER BY a.data_apresentacao, p.nome_palco, a.hora_inicio;

-- C2 - Ingressos vendidos com o nome do comprador e do festival
SELECT i.id_ingresso, pa.nome_participante, f.nome_festival,
       i.tipo, i.preco, i.status
FROM ingresso i
JOIN participante pa ON pa.id_participante = i.id_participante
JOIN festival     f  ON f.id_festival      = i.id_festival
ORDER BY f.nome_festival, pa.nome_participante;

-- C3 - Lista de participantes de cada festival (via tabela associativa)
SELECT f.nome_festival, pa.nome_participante, pc.data_compra
FROM participacao pc
JOIN festival     f  ON f.id_festival      = pc.id_festival
JOIN participante pa ON pa.id_participante = pc.id_participante
ORDER BY f.nome_festival, pa.nome_participante;

-- =====================================================================
-- 5) GROUP BY, HAVING e ORDER BY
-- =====================================================================
-- C4 - GROUP BY + COUNT: quantidade de shows por palco
SELECT p.nome_palco, COUNT(a.id_apresentacao) AS total_shows
FROM palco p
LEFT JOIN apresentacao a ON a.id_palco = p.id_palco
GROUP BY p.id_palco, p.nome_palco
ORDER BY total_shows DESC;

-- C5 - CONSULTA INTEGRADA (JOIN + WHERE + GROUP BY + HAVING + ORDER BY)
-- Necessidade: relatorio financeiro. Mostra os festivais com faturamento
-- de pelo menos R$ 500,00, sem contar ingressos cancelados (RN4),
-- do maior para o menor faturamento.
SELECT f.nome_festival,
       COUNT(i.id_ingresso) AS ingressos_validos,
       SUM(i.preco)         AS faturamento,
       ROUND(AVG(i.preco),2) AS ticket_medio
FROM festival f
JOIN ingresso i ON i.id_festival = f.id_festival
WHERE i.status <> 'cancelado'
GROUP BY f.id_festival, f.nome_festival
HAVING SUM(i.preco) >= 500
ORDER BY faturamento DESC;

-- C6 - Artistas mais escalados: 2 ou mais shows
SELECT ar.nome_artista, ar.genero_musical, COUNT(a.id_apresentacao) AS total_shows
FROM artista ar
JOIN apresentacao a ON a.id_artista = ar.id_artista
GROUP BY ar.id_artista, ar.nome_artista, ar.genero_musical
HAVING COUNT(a.id_apresentacao) >= 2
ORDER BY total_shows DESC, ar.nome_artista;

-- C7 - Melhores clientes: participantes que gastaram R$ 400,00 ou mais
SELECT pa.nome_participante,
       COUNT(i.id_ingresso) AS qtd_ingressos,
       SUM(i.preco)         AS total_gasto
FROM participante pa
JOIN ingresso i ON i.id_participante = pa.id_participante
WHERE i.status <> 'cancelado'
GROUP BY pa.id_participante, pa.nome_participante
HAVING SUM(i.preco) >= 400
ORDER BY total_gasto DESC;

-- C8 - Generos musicais com 3 ou mais shows e duracao media (AVG)
SELECT ar.genero_musical,
       COUNT(*) AS total_shows,
       ROUND(AVG(TIMESTAMPDIFF(MINUTE, a.hora_inicio, a.hora_fim)), 0) AS duracao_media_min
FROM apresentacao a
JOIN artista ar ON ar.id_artista = a.id_artista
GROUP BY ar.genero_musical
HAVING COUNT(*) >= 3
ORDER BY total_shows DESC, duracao_media_min DESC;

-- =====================================================================
-- 6) Uso da VIEW e das FUNCTIONS
-- =====================================================================
SELECT * FROM vw_programacao
 WHERE nome_festival = 'Rock na Concha'
 ORDER BY data_apresentacao, hora_inicio;

SELECT nome_festival,
       fn_faturamento_festival(id_festival) AS faturamento,
       fn_percentual_ocupacao(id_festival)  AS ocupacao_pct
FROM festival
ORDER BY faturamento DESC;
