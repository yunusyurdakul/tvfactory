package tv.factory.execution.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MainBoard extends Product {
    public MainBoard() {
        super(UUID.randomUUID());
    }
}
