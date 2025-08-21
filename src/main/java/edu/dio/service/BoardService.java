package edu.dio.service;

import edu.dio.persistence.dao.BoardColumnDAO;
import edu.dio.persistence.dao.BoardDAO;
import edu.dio.persistence.entity.BoardColumnEntity;
import edu.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService {

    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        try {
            dao.insert(entity);
            var columns = entity.getBoardColumns()
                    .stream()
                    .peek(c -> c.setBoard(entity))
                    .toList();
            for (var column: columns){
                boardColumnDAO.insert(column);
            }
            connection.commit();
            return entity;
        } catch (SQLException e){
            connection.rollback();
            throw e;
        }
    }
    public boolean delete(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        try{
            if(!dao.exists(id)){
                return false;
            }
            dao.delete(id);
            connection.commit();
            return true;
        } catch (SQLException e){
            connection.rollback();
            throw e;
        }
    }
}
