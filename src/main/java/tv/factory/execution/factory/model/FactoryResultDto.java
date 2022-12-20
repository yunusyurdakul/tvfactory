package tv.factory.execution.factory.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FactoryResultDto {
    private int balance;
    private int televisionCount;
    private int robotCount;
    private int panelCount;
    private int mainBoardCount;
}
