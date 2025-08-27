package edu.dio.persistence.dao;

import edu.dio.persistence.converter.OffsetDateTimeConverter;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static edu.dio.persistence.converter.OffsetDateTimeConverter.toTimestamp;

@AllArgsConstructor
public class BlockDAO {
    private final Connection connection;

    public void block(final Long cardId, final String reason) throws SQLException {
        var sql = "INSERT INTO blocks (blocked_at, block_reason, card_id) values (?,?,?)";
        try (var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    public void unblock(final Long cardId, final String reason) throws SQLException {
        var sql = "UPDATE blocks SET unblocked_at = ?, unblock_reason = ? WHERE card_id = ? AND unblock_reason IS NULL";
        try (var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }
}
