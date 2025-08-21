package edu.dio.service;

import edu.dio.persistence.dao.BoardColumnDAO;
import edu.dio.persistence.dao.BoardDAO;
import edu.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {
    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id ) throws SQLException{
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findByID(id);
        if(optional.isPresent()){
            var entity = optional.get();
            entity.setBoardColumns(boardColumnDAO.findById(entity.getId()));
            return Optional.of(entity);
        }
        return Optional.empty();
    }
}
