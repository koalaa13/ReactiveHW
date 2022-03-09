package repository;

import model.CartItem;
import mongodb.MongoDBProvider;
import rx.Observable;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class CartRepository {
    private final MongoDBProvider provider;
    private final static String dbKey = "carts";
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartRepository(MongoDBProvider provider) {
        this.provider = provider;
        this.userRepository = new UserRepository(provider);
        this.productRepository = new ProductRepository(provider);
    }

    public Observable<String> addToCart(int userId, int productId) {
        var user = userRepository.getUserById(userId);
        var product = productRepository.getProductById(productId);
        return user.isEmpty().flatMap(userNotFound -> {
            if (userNotFound) {
                return Observable.just("No such user with id " + userId);
            } else {
                return product.isEmpty().flatMap(productNotFound -> {
                    if (productNotFound) {
                        return Observable.just("No such product with id " + productId);
                    } else {
                        return provider.getDatabase()
                                .getCollection(dbKey)
                                .insertOne(new CartItem(userId, productId).convertToDocument())
                                .map(suc -> "Product with id " + productId + "was successfully added to cart of user " + userId);
                    }
                });
            }
        });
    }

    public Observable<String> getUsersCart(int userId) {
        var user = userRepository.getUserById(userId);
        return user.isEmpty().flatMap(userNotFound -> {
            if (userNotFound) {
                return Observable.just("No such user with id " + userId);
            } else {
                return provider.getDatabase()
                        .getCollection(dbKey)
                        .find(eq("user_id", userId))
                        .toObservable()
                        .map(CartItem::new)
                        .map(CartItem::toString);
            }
        });
    }

    public Observable<String> clearUsersCart(int userId) {
        var user = userRepository.getUserById(userId);
        return user.isEmpty().flatMap(userNotFound -> {
            if (userNotFound) {
                return Observable.just("No such user with id " + userId);
            } else {
                return provider.getDatabase()
                        .getCollection(dbKey)
                        .deleteOne(eq("user_id", userId))
                        .map(deleteResult -> "There were " + deleteResult.getDeletedCount() + " items in a cart");
            }
        });
    }

    public Observable<String> deleteItemFromCart(int userId, int productId) {
        var user = userRepository.getUserById(userId);
        var product = productRepository.getProductById(productId);
        return user.isEmpty().flatMap(userNotFound -> {
            if (userNotFound) {
                return Observable.just("No such user with id " + userId);
            } else {
                return product.isEmpty().flatMap(productNotFound -> {
                    if (productNotFound) {
                        return Observable.just("No such product with id " + productId);
                    } else {
                        return provider.getDatabase()
                                .getCollection(dbKey)
                                .deleteOne(and(eq("user_id", userId), eq("product_id", productId)))
                                .map(deleteResult -> {
                                    if (deleteResult.getDeletedCount() == 0) {
                                        return "Item not found in a cart";
                                    } else {
                                        return "Item " + productId + " was successfully deleted";
                                    }
                                });
                    }
                });
            }
        });
    }
}
