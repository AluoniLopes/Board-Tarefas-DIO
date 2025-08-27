package edu.dio.ui;

import edu.dio.persistence.entity.BoardColumnEntity;
import edu.dio.persistence.entity.BoardColumnKindEnum;
import edu.dio.persistence.entity.BoardEntity;
import edu.dio.service.BoardQueryService;
import edu.dio.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static edu.dio.persistence.config.ConnectionConfig.getConnection;
import static edu.dio.persistence.entity.BoardColumnKindEnum.*;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    public void execute() throws SQLException {
        try{
            System.out.println("BEM VINDO AO GERENCIADOR DE BOARDS, ESCOLHA A OPÇÃO DESEJADA:");
            var option = -1;
            while (true){
                System.out.println("1 - Criar um novo board");
                System.out.println("2 - Selecionar um board existente");
                System.out.println("3 - Excluir um board");
                System.out.println("4 - Sair");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createBoard();
                    case 2 -> selectBoard();
                    case 3 -> deleteBoard();
                    case 4 -> System.exit(0);
                    default -> {
                        System.out.println("opção inválida");
                        option=-1;
                    }
                }
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
}

    private void createBoard(){
        var entity = new BoardEntity();
        System.out.println("Informe o nome do seu board");
        entity.setName(scanner.next());
        System.out.println(
                "Seu board terá colunas alem das 3 padrões?" +
                "se sim informe quantas, senão digite '0'");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna inicial");
        String initialColumnName = scanner.next();
        int order = 0;
        BoardColumnEntity initialColumn = createColumn(initialColumnName, INITIAL, order++);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Informe o nome da coluna pendente");
            BoardColumnEntity pendingColumn = createColumn(scanner.next(), PENDING, order++);
            columns.add(pendingColumn);
        }
        System.out.println("Informe o nome da coluna final");
        var finalColumn = createColumn(scanner.next(), FINAL, order++);
        columns.add(finalColumn);
        System.out.println("Informe o nome da coluna de cancelamento");
        var cancelColumn = createColumn(scanner.next(), CANCEL, order);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar");
        var id = scanner.nextLong();
        try(var connection = getConnection()) {
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("Não foi encontrado um board com id %s\n", id)
            );
        } catch (SQLException e){
            System.out.println("Erro 500");
        }
    }

    private void deleteBoard() throws SQLException{
        System.out.println("informe o ID do board que será excluído");
        Long id = scanner.nextLong();
        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            if (service.delete(id)){
                System.out.printf("O board %s foi excluído\n", id);
            } else {
                System.out.printf("Não foi encontrado um board com id %s\n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind,
                                           final int order){
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}