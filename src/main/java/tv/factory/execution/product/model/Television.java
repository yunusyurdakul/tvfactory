package tv.factory.execution.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Television {
    private final UUID panelSerialNumber;
    private final UUID mainBoardSerialNumber;
}
