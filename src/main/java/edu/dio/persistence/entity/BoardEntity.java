package edu.dio.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static edu.dio.persistence.entity.BoardColumnKindEnum.CANCEL;
import static edu.dio.persistence.entity.BoardColumnKindEnum.INITIAL;

@Data
public class BoardEntity {
    private Long id;
    private String name;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();


    public BoardColumnEntity getInitialColumn(){
        return filter(INITIAL);

    };

    public BoardColumnEntity getCancelColumn(){
        return filter(CANCEL);
    };

    private BoardColumnEntity filter(final BoardColumnKindEnum kind){
        return boardColumns.stream()
                .filter(bc -> bc.getKind().equals(kind))
                .findFirst().orElseThrow();
    }
}
