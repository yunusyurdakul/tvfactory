package tv.factory.execution.factory.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FactoryResultDto {
    private int finalBalance;
    private int totalTvCount;
    private int robotCount;
}
