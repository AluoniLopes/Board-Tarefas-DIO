package edu.dio.service;

import edu.dio.persistence.dao.CardDAO;
import edu.dio.persistence.dto.CardDetailsDTO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {
    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final long id) throws SQLException {
        var dao = new CardDAO(connection);
        return dao.findById(id);
    }
}
