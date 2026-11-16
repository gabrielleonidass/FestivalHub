package festivalhub;

import festivalhub.dao.*;
import festivalhub.model.Artista;
import festivalhub.model.Participante;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/** Menu de console do FestivalHub. */
public class Main {

    private static final Scanner in = new Scanner(System.in);
    private static final ArtistaDAO artistaDAO = new ArtistaDAO();
    private static final ParticipanteDAO participanteDAO = new ParticipanteDAO();
    private static final IngressoDAO ingressoDAO = new IngressoDAO();
    private static final ApresentacaoDAO apresentacaoDAO = new ApresentacaoDAO();
    private static final RelatorioDAO relatorioDAO = new RelatorioDAO();

    private static final String LINHA_DUPLA = "==================================================";
    private static final String LINHA       = "--------------------------------------------------";

    public static void main(String[] args) {
        int op;
        do {
            mostrarMenu();
            op = lerInt("Escolha uma opcao: ");
            try {
                executar(op);
            } catch (SQLException e) {
                // Mensagens das regras de negocio (SIGNAL 45000) e das constraints chegam aqui
                System.out.println();
                System.out.println("ERRO DO BANCO: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println();
                System.out.println("Valor invalido. Use apenas numeros (ex: 180.00).");
            }
            if (op != 0) pausar();
        } while (op != 0);
        System.out.println("Sistema encerrado. Ate mais!");
    }

    // =====================================================================
    // MENU
    // =====================================================================
    private static void mostrarMenu() {
        System.out.println();
        System.out.println(LINHA_DUPLA);
        System.out.println("        FESTIVALHUB - GESTAO DE FESTIVAIS");
        System.out.println(LINHA_DUPLA);
        System.out.println();
        System.out.println(" ARTISTAS");
        System.out.println("    1 - Listar artistas");
        System.out.println("    2 - Cadastrar artista");
        System.out.println("    3 - Atualizar artista");
        System.out.println("    4 - Excluir artista");
        System.out.println();
        System.out.println(" PARTICIPANTES");
        System.out.println("    5 - Listar participantes");
        System.out.println("    6 - Cadastrar participante");
        System.out.println("    7 - Atualizar participante");
        System.out.println("    8 - Excluir participante");
        System.out.println();
        System.out.println(" INGRESSOS");
        System.out.println("    9 - Vender ingresso");
        System.out.println("   10 - Cancelar ingresso");
        System.out.println("   11 - Listar ingressos de um festival");
        System.out.println();
        System.out.println(" SHOWS");
        System.out.println("   12 - Agendar show");
        System.out.println("   13 - Ver programacao de um festival");
        System.out.println();
        System.out.println(" RELATORIOS");
        System.out.println("   14 - Faturamento por festival");
        System.out.println("   15 - Artistas com mais shows");
        System.out.println("   16 - Faturamento e ocupacao dos festivais");
        System.out.println();
        System.out.println("    0 - Sair");
        System.out.println(LINHA);
    }

    // =====================================================================
    // EXECUCAO DAS OPCOES
    // =====================================================================
    private static void executar(int op) throws SQLException {
        switch (op) {

            // ---------------- ARTISTAS (CRUD) ----------------
            case 1 -> {
                titulo("LISTA DE ARTISTAS");
                System.out.println("Segue a lista de artistas cadastrados e seus generos musicais:");
                System.out.println();
                listarArtistas();
            }
            case 2 -> {
                titulo("CADASTRAR ARTISTA");
                Artista a = new Artista(0, lerTexto("Nome do artista: "), lerTexto("Genero musical: "));
                int id = artistaDAO.inserir(a);
                System.out.println();
                System.out.println("Artista \"" + a.getNome() + "\" cadastrado com sucesso! (ID " + id + ")");
            }
            case 3 -> {
                titulo("ATUALIZAR ARTISTA");
                System.out.println("Artistas cadastrados:");
                System.out.println();
                listarArtistas();
                System.out.println();
                int id = lerInt("Digite o ID do artista que deseja atualizar: ");
                Artista a = artistaDAO.buscarPorId(id);
                if (a == null) {
                    System.out.println("Nenhum artista encontrado com o ID " + id + ".");
                    return;
                }
                System.out.println("(Deixe em branco e aperte Enter para manter o valor atual)");
                a.setNome(lerTexto("Novo nome [" + a.getNome() + "]: ", a.getNome()));
                a.setGeneroMusical(lerTexto("Novo genero [" + a.getGeneroMusical() + "]: ", a.getGeneroMusical()));
                System.out.println();
                System.out.println(artistaDAO.atualizar(a)
                        ? "Artista atualizado com sucesso!"
                        : "Nenhuma alteracao realizada.");
            }
            case 4 -> {
                titulo("EXCLUIR ARTISTA");
                System.out.println("Artistas cadastrados:");
                System.out.println();
                listarArtistas();
                System.out.println();
                int id = lerInt("Digite o ID do artista que deseja excluir: ");
                System.out.println();
                System.out.println(artistaDAO.excluir(id)
                        ? "Artista excluido com sucesso!"
                        : "Nenhum artista encontrado com o ID " + id + ".");
            }

            // ---------------- PARTICIPANTES (CRUD) ----------------
            case 5 -> {
                titulo("LISTA DE PARTICIPANTES");
                System.out.println("Segue a lista de participantes cadastrados com CPF e e-mail:");
                System.out.println();
                listarParticipantes();
            }
            case 6 -> {
                titulo("CADASTRAR PARTICIPANTE");
                Participante p = new Participante(0, lerTexto("Nome completo: "),
                        lerTexto("CPF (somente os 11 numeros): "), lerTexto("E-mail: "));
                int id = participanteDAO.inserir(p);
                System.out.println();
                System.out.println("Participante \"" + p.getNome() + "\" cadastrado com sucesso! (ID " + id + ")");
            }
            case 7 -> {
                titulo("ATUALIZAR PARTICIPANTE");
                System.out.println("Participantes cadastrados:");
                System.out.println();
                listarParticipantes();
                System.out.println();
                Participante p = new Participante(
                        lerInt("Digite o ID do participante que deseja atualizar: "),
                        lerTexto("Novo nome: "), lerTexto("Novo CPF: "), lerTexto("Novo e-mail: "));
                System.out.println();
                System.out.println(participanteDAO.atualizar(p)
                        ? "Participante atualizado com sucesso!"
                        : "Nenhum participante encontrado com esse ID.");
            }
            case 8 -> {
                titulo("EXCLUIR PARTICIPANTE");
                System.out.println("Participantes cadastrados:");
                System.out.println();
                listarParticipantes();
                System.out.println();
                int id = lerInt("Digite o ID do participante que deseja excluir: ");
                System.out.println();
                System.out.println(participanteDAO.excluir(id)
                        ? "Participante excluido com sucesso!"
                        : "Nenhum participante encontrado com o ID " + id + ".");
            }

            // ---------------- INGRESSOS ----------------
            case 9 -> {   // usa a procedure sp_vender_ingresso (RN3 - capacidade)
                titulo("VENDER INGRESSO");
                System.out.println("Festivais: 1 - Festival Mare Alta | 2 - Rock na Concha | 3 - Sarau do Rio Vermelho");
                System.out.println();
                int idParticipante = lerInt("ID do participante: ");
                int idFestival     = lerInt("ID do festival: ");
                String tipo        = lerTexto("Tipo (pista / meia / vip / camarote): ").toLowerCase();
                BigDecimal preco   = new BigDecimal(lerTexto("Preco (ex: 180.00): ").replace(",", "."));
                int id = ingressoDAO.vender(idParticipante, idFestival, tipo, preco);
                System.out.println();
                System.out.println("Ingresso vendido com sucesso! Numero do ingresso: " + id);
            }
            case 10 -> {  // UPDATE de status (RN4 - cancelado sai do faturamento)
                titulo("CANCELAR INGRESSO");
                int id = lerInt("Numero do ingresso que deseja cancelar: ");
                System.out.println();
                System.out.println(ingressoDAO.cancelar(id)
                        ? "Ingresso " + id + " cancelado. Ele nao conta mais no faturamento."
                        : "Ingresso nao encontrado ou ja utilizado/cancelado.");
            }
            case 11 -> {  // SELECT com JOIN
                titulo("INGRESSOS POR FESTIVAL");
                int idFestival = lerInt("ID do festival (1, 2 ou 3): ");
                System.out.println();
                System.out.println("Segue a lista de ingressos vendidos para o festival, com comprador, tipo, preco e situacao:");
                System.out.println();
                ingressoDAO.listarPorFestival(idFestival);
            }

            // ---------------- SHOWS ----------------
            case 12 -> {  // usa a procedure sp_agendar_apresentacao (RN1 e RN2)
                titulo("AGENDAR SHOW");
                int idArtista = lerInt("ID do artista: ");
                int idPalco   = lerInt("ID do palco: ");
                String data   = lerTexto("Data do show (AAAA-MM-DD): ");
                String inicio = lerTexto("Hora de inicio (HH:MM): ");
                String fim    = lerTexto("Hora de termino (HH:MM): ");
                int id = apresentacaoDAO.agendar(idArtista, idPalco, data, inicio, fim);
                System.out.println();
                System.out.println("Show agendado com sucesso! (ID " + id + ")");
            }
            case 13 -> {  // usa a VIEW vw_programacao
                titulo("PROGRAMACAO DO FESTIVAL");
                int idFestival = lerInt("ID do festival (1, 2 ou 3): ");
                System.out.println();
                System.out.println("Segue a programacao completa, com data, horario, palco e artista de cada show:");
                System.out.println();
                apresentacaoDAO.listarProgramacao(idFestival);
            }

            // ---------------- RELATORIOS ----------------
            case 14 -> {  // JOIN + GROUP BY + HAVING + ORDER BY
                titulo("FATURAMENTO POR FESTIVAL");
                BigDecimal minimo = new BigDecimal(
                        lerTexto("Mostrar festivais com faturamento minimo de (ex: 500): ").replace(",", "."));
                System.out.println();
                System.out.println("Segue o faturamento dos festivais que atingiram pelo menos R$ " + minimo
                        + ", do maior para o menor (ingressos cancelados nao entram):");
                System.out.println();
                relatorioDAO.faturamentoPorFestival(minimo);
            }
            case 15 -> {  // GROUP BY + HAVING + ORDER BY
                titulo("ARTISTAS COM MAIS SHOWS");
                int minimo = lerInt("Mostrar artistas com no minimo quantos shows? ");
                System.out.println();
                System.out.println("Segue a lista de artistas com " + minimo + " ou mais shows, do mais escalado para o menos:");
                System.out.println();
                relatorioDAO.artistasMaisEscalados(minimo);
            }
            case 16 -> {  // FUNCTIONS fn_faturamento_festival e fn_percentual_ocupacao
                titulo("FATURAMENTO E OCUPACAO");
                System.out.println("Segue o faturamento e o percentual de ocupacao de cada festival:");
                System.out.println();
                relatorioDAO.indicadoresFestivais();
            }

            case 0 -> { }
            default -> System.out.println("Opcao invalida. Escolha um numero do menu.");
        }
    }

    // =====================================================================
    // LISTAGENS EM TABELA
    // =====================================================================
    private static void listarArtistas() throws SQLException {
        List<Artista> artistas = artistaDAO.listar();
        if (artistas.isEmpty()) {
            System.out.println("Nenhum artista cadastrado.");
            return;
        }
        System.out.printf(" %-4s | %-24s | %s%n", "ID", "ARTISTA", "GENERO MUSICAL");
        System.out.println("------+--------------------------+----------------");
        for (Artista a : artistas) {
            System.out.printf(" %-4d | %-24s | %s%n", a.getId(), a.getNome(), a.getGeneroMusical());
        }
        System.out.println();
        System.out.println("Total: " + artistas.size() + " artista(s).");
    }

    private static void listarParticipantes() throws SQLException {
        List<Participante> participantes = participanteDAO.listar();
        if (participantes.isEmpty()) {
            System.out.println("Nenhum participante cadastrado.");
            return;
        }
        System.out.printf(" %-4s | %-22s | %-14s | %s%n", "ID", "NOME", "CPF", "E-MAIL");
        System.out.println("------+------------------------+----------------+---------------------------");
        for (Participante p : participantes) {
            System.out.printf(" %-4d | %-22s | %-14s | %s%n",
                    p.getId(), p.getNome(), formatarCpf(p.getCpf()), p.getEmail());
        }
        System.out.println();
        System.out.println("Total: " + participantes.size() + " participante(s).");
    }

    // =====================================================================
    // UTILITARIOS
    // =====================================================================
    private static void titulo(String texto) {
        System.out.println();
        System.out.println(LINHA);
        System.out.println(" " + texto);
        System.out.println(LINHA);
    }

    private static String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private static void pausar() {
        System.out.println();
        lerTexto("Pressione Enter para voltar ao menu...");
    }

    private static String lerTexto(String rotulo) {
        System.out.print(rotulo);
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    private static String lerTexto(String rotulo, String padrao) {
        String s = lerTexto(rotulo);
        return s.isEmpty() ? padrao : s;
    }

    private static int lerInt(String rotulo) {
        while (true) {
            String s = lerTexto(rotulo);
            if (s.isEmpty() && !in.hasNextLine()) return 0;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Digite apenas numeros.");
            }
        }
    }
}