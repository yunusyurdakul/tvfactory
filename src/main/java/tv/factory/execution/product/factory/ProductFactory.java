package tv.factory.execution.product.factory;

import tv.factory.execution.product.enums.ProductType;
import tv.factory.execution.product.model.MainBoard;
import tv.factory.execution.product.model.Panel;
import tv.factory.execution.product.model.Product;

public class ProductFactory {

    public static Product produce(ProductType type) {
        switch (type) {
            case PANEL:
                return new Panel();
            case MAIN_BOARD:
                return new MainBoard();
            default:
                throw new IllegalStateException("Cannot produce type: " + type);
        }
    }
}
