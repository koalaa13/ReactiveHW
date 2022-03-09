package repository;

import model.Currency;
import model.Product;
import mongodb.MongoDBProvider;
import rx.Observable;

import java.util.concurrent.atomic.AtomicReference;

import static com.mongodb.client.model.Filters.eq;

public class ProductRepository {
    private final static String dbKey = "products";
    private final MongoDBProvider provider;
    private final UserRepository userRepository;

    public ProductRepository(MongoDBProvider provider) {
        this.provider = provider;
        this.userRepository = new UserRepository(provider);
    }

    public Observable<String> getProductById(int id) {
        return provider.getDatabase()
                .getCollection(dbKey)
                .find(eq("id", id))
                .toObservable()
                .map(Product::new)
                .map(Product::toString);
    }

    public Observable<String> save(Product product) {
        return getProductById(product.getId()).isEmpty().flatMap(notFound -> {
            if (notFound) {
                return provider.getDatabase()
                        .getCollection(dbKey)
                        .insertOne(product.convertToDocument())
                        .map(suc -> "Product " + product + " was successfully added.");
            } else {
                return Observable.just("Product with such id " + product.getId() + " already exists");
            }
        });
    }

    public Observable<String> getAllByUserId(int userId) {
        var user = userRepository.getUserById(userId);
        return user.isEmpty().flatMap(notFound -> {
            if (notFound) {
                return Observable.just("No such user " + userId);
            } else {
                return user.flatMap(u -> getAllWithNeededCurrency(u.getCurrency()));
            }
        });
    }

    private Observable<String> getAllWithNeededCurrency(Currency currency) {
        return provider.getDatabase()
                .getCollection(dbKey)
                .find()
                .toObservable()
                .map(Product::new)
                .map(product ->
                        new Product(product.getId(),
                                product.getName(),
                                product.getDescription(),
                                product.getPrice() * currency.getCoef())
                                .toString()
                );
    }
}
