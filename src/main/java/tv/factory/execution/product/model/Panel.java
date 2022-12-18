package tv.factory.execution.product.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Panel extends Product{
    public Panel() {
        super(UUID.randomUUID());
    }
}
