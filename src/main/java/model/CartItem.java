package model;

import lombok.Data;
import org.bson.Document;

@Data
public class CartItem implements Entity {
    private final int userId;
    private final int productId;

    public CartItem(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public CartItem(Document document) {
        this.userId = document.getInteger("user_id");
        this.productId = document.getInteger("product_id");
    }

    @Override
    public Document convertToDocument() {
        return new Document("user_id", userId)
                .append("product_id", productId);
    }
}
