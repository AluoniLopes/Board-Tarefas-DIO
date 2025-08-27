package edu.dio.persistence.dto;

import java.time.OffsetDateTime;

public record CardDTO(Long id,
                      boolean locked,
                      OffsetDateTime blockedAt,
                      String blockReason,
                      int blocksAmount,
                      long columnId,
                      String columnName) {
}
