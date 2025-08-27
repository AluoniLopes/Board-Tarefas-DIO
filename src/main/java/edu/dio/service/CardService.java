package edu.dio.service;

import edu.dio.exception.CardBlockedException;
import edu.dio.exception.CardFinishedException;
import edu.dio.exception.EntityNotFoundExeption;
import edu.dio.persistence.dao.BlockDAO;
import edu.dio.persistence.dao.CardDAO;
import edu.dio.persistence.dto.BoardColumnInfoDTO;
import edu.dio.persistence.dto.CardDetailsDTO;
import edu.dio.persistence.entity.BoardColumnKindEnum;
import edu.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static edu.dio.persistence.entity.BoardColumnKindEnum.CANCEL;
import static edu.dio.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor
public class CardService {
    private final Connection connection;

    public CardEntity create(final CardEntity entity) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void moveToNextColumn(final long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundExeption("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (dto.blocked()) {
                throw new CardBlockedException(
                        "O card %s está bloqueado, é necessário desbloquear para mover".formatted(dto.id()));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("O card informado pertence a outro board"));
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado");
            }
            var nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("O card está cancelado"));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundExeption("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (dto.blocked()) {
                throw new CardBlockedException(
                        "O card %s está bloqueado, é necessário desbloquear para mover".formatted(dto.id()));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("O card informado pertence a outro board"));
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado");
            }
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (RuntimeException e) {
            connection.rollback();
            System.out.println(e.getMessage());
        }
    }

    public void block(final Long cardId, final String reason, final List<BoardColumnInfoDTO> boardColumnInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundExeption("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (dto.blocked()) {
                throw new CardBlockedException(
                        "O card %s já está bloqueado".formatted(cardId));
            }
            var currentColumn = boardColumnInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow();
            if(currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                String message = "O Card está numa coluna do tipo [%s] que não pode ser cancelado"
                        .formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.block(cardId, reason);
            connection.commit();
        }catch (SQLException e) {
            connection.rollback();
        }catch (RuntimeException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

    public void unblock(final Long cardId, final String reason) throws SQLException{
        try{
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundExeption("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (!dto.blocked()) {
                throw new CardBlockedException(
                        "O card %s não está bloqueado".formatted(cardId));
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(cardId, reason);
            connection.commit();
        } catch (SQLException ex){
            connection.rollback();
        }
    }
}
