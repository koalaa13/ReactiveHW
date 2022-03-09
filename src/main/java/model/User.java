package model;

import lombok.Data;
import org.bson.Document;

@Data
public class User implements Entity {
    private final int id;
    private final String username;
    private final String password;
    private final Currency currency;

    public User(int id, String username, String password, Currency currency) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.currency = currency;
    }

    @Override
    public Document convertToDocument() {
        return new Document("id", id)
                .append("username", username)
                .append("password", password)
                .append("currency", currency.toString());
    }

    public User(Document document) {
        this.id = document.getInteger("id");
        this.username = document.getString("username");
        this.password = document.getString("password");
        this.currency = Currency.valueOf(document.getString("currency"));
    }
}
