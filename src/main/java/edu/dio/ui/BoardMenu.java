package edu.dio.ui;

import edu.dio.persistence.dao.CardDAO;
import edu.dio.persistence.dto.BoardColumnInfoDTO;
import edu.dio.persistence.entity.BoardColumnEntity;
import edu.dio.persistence.entity.BoardEntity;
import edu.dio.persistence.entity.CardEntity;
import edu.dio.service.BoardColumnQueryService;
import edu.dio.service.BoardQueryService;
import edu.dio.service.CardQueryService;
import edu.dio.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static edu.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final BoardEntity entity;
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    public void execute() {
        System.out.printf("BEM-VINDO AO BOARD %s, selecione a operação desejada\n", entity.getName());

        var option = -1;
        try {

            while (option != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um board");
                System.out.println("4 - Desbloquear um board");
                System.out.println("5 - Cancelar um board");
                System.out.println("6 - Ver Board");
                System.out.println("7 - Ver colunas");
                System.out.println("8 - Ver card detalhado");
                System.out.println("9 - Voltar para o menu anterior");
                System.out.println("0 - Sair");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando ao menu inicial");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opção inválida");
                }
            }
        } catch (SQLException e){
            System.out.println("Erro ao Realizar uma busca no banco de dados");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createCard() throws SQLException {
        CardEntity card = new CardEntity();
        System.out.println("Insira um titulo para o card");
        card.setTitle(scanner.next());
        System.out.println("Insira uma descrição para o card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try (var connection = getConnection()){
            new CardService(connection).create(card);
        }

    }

    private void moveCardToNextColumn() throws SQLException{
        System.out.println("Informe o Id do card que deseja mover para a prox coluna");
        long cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()){
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() throws SQLException {
        System.out.println("Informe o id do Card que deseja bloquear");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do block");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
        new CardService(getConnection()).block(cardId, reason, boardColumnsInfo);
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Informe o id do Card que deseja desbloquear");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do unblock");
        var reason = scanner.next();
        try(var connection = getConnection()){
            new CardService(connection).unblock(cardId, reason);
        }
    }

    private void cancelCard() throws SQLException {
        System.out.println("Informe o Id do card que deseja mover para Cancelado");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()){
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }

    }

    private void showCard() throws SQLException{
        System.out.println("Informe o ID do card que deseja visualizar");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()){
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(c -> {
                        System.out.printf("Card %s - %s.\n", c.id(), c.title());
                        System.out.printf("Descrição %s\n", c.description());
                        System.out.println(c.blocked() ?
                                "Está bloqueado. Motivo " + c.blockReason() :
                                "Não está bloqueado");
                        System.out.printf("Ja foi bloqueado %s vezes\n", c.blocksAmount());
                        System.out.printf("Está no momento na coluna %s - %s", c.columnId(), c.columnName());
                    },
                        () -> System.out.printf("Nao existe um card com o ID %s", selectedCardId));
        }
    }

    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)){
            entity.getBoardColumns().forEach(c ->
                    System.out.printf(
                            "%s - %s [%s]\n",
                            c.getId(), c.getName(), c.getKind()));
            System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
            selectedColumn = scanner.nextLong();
        }
        try (var connection = getConnection()){
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca ->
                        System.out.printf("Card %s - %s\nDescrição: %s\n",
                        ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
        }

    private void showBoard() throws SQLException {
        try(var connection = getConnection()){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent( b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                System.out.println("|------------------------------------------|");
                b.columns().forEach(c ->
                        System.out.printf("Coluna [%s] tipo [%s] tem %s cards\n",
                        c.name(), c.kind(), c.cardsAmount()
                        )
                );
            });
            System.out.println("|------------------------------------------|");
        }
    }
}
