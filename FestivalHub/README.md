# FestivalHub — Banco de Dados + Java (JDBC)

Gabriel Leônidas · Alisson Paixão · UCSAL

## Conteúdo

```
sql/01_ddl.sql   -> cria o banco, tabelas, constraints, view, functions e procedures
sql/02_dml.sql   -> INSERT, UPDATE, DELETE e todas as consultas (JOIN, GROUP BY, HAVING, ORDER BY)
java/src/...     -> sistema em Java (menu de console) com JDBC
```

## 1. Banco (MySQL 8.0.16 ou superior)

No MySQL Workbench, abra e execute (raio ⚡) nesta ordem:

1. `sql/01_ddl.sql`
2. `sql/02_dml.sql`

Os dois scripts podem ser executados de novo a qualquer momento:
o `01_ddl.sql` apaga e recria o banco `festivalhub`.

Se aparecer o erro 1418 ao criar as functions, rode antes:
`SET GLOBAL log_bin_trust_function_creators = 1;`

## 2. Java

1. Baixe o driver **MySQL Connector/J** (arquivo `mysql-connector-j-8.x.x.jar`) em
   https://dev.mysql.com/downloads/connector/j/ (opção "Platform Independent").
2. Crie um projeto Java no NetBeans/IntelliJ/Eclipse/VS Code, copie a pasta
   `java/src/festivalhub` para dentro de `src` e adicione o `.jar` às bibliotecas do projeto.
3. Em `festivalhub/dao/ConexaoBD.java`, troque usuário e senha pelos do seu MySQL.
4. Rode a classe `festivalhub.Main`.

Pelo terminal (Windows, a partir da pasta `java`):

```
javac -d out -cp lib\mysql-connector-j-8.4.0.jar src\festivalhub\*.java src\festivalhub\dao\*.java src\festivalhub\model\*.java
java  -cp out;lib\mysql-connector-j-8.4.0.jar festivalhub.Main
```

(No Linux/Mac use `/` nas pastas e `:` no lugar de `;`.)

## Menu

| Opção | O que faz | Recurso demonstrado |
|---|---|---|
| 1–4 | Listar / inserir / atualizar / excluir artista | CRUD |
| 5–8 | Listar / inserir / atualizar / excluir participante | CRUD |
| 9 | Vender ingresso | Procedure `sp_vender_ingresso` (RN3) |
| 10 | Cancelar ingresso | UPDATE (RN4) |
| 11 | Ingressos de um festival | SELECT com JOIN |
| 12 | Agendar show | Procedure `sp_agendar_apresentacao` (RN1, RN2) |
| 13 | Programação do festival | View `vw_programacao` |
| 14 | Faturamento por festival | JOIN + GROUP BY + HAVING + ORDER BY |
| 15 | Artistas mais escalados | GROUP BY + HAVING + ORDER BY |
| 16 | Indicadores | Functions `fn_faturamento_festival` e `fn_percentual_ocupacao` |

## Demonstração rápida das regras

- Opção 9, participante 1, festival 3, pista, 50 → vende (sobra 1 vaga após o cancelamento do Henrique).
- Opção 9 de novo, participante 6, festival 3 → "Ingressos esgotados" (RN3).
- Opção 12, artista 3, palco 1, 2027-01-22, 21:00 → 22:30 → "Choque de horario" (RN1).
- Opção 12, artista 3, palco 1, 2027-02-01, 20:00 → 21:00 → "Data fora do periodo" (RN2).
- Opção 4, artista 1 → erro de FK: artista com shows não pode ser excluído.
