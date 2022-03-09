package server.handler;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import mongodb.MongoDBProvider;
import repository.CartRepository;
import rx.Observable;

public class UserCartHandler implements Handler {
    private final CartRepository cartRepository;

    public UserCartHandler(MongoDBProvider provider) {
        this.cartRepository = new CartRepository(provider);
    }

    @Override
    public Observable<String> handle(HttpServerRequest<ByteBuf> req) {
        var paramsMap = req.getQueryParameters();
        if (!paramsMap.containsKey("user_id")) {
            return Observable.just("Need user id");
        }
        int userId;
        try {
            userId = Integer.parseInt(paramsMap.get("user_id").get(0));
        } catch (NumberFormatException e) {
            return Observable.just("User id should be an integer number");
        }
        var cart = cartRepository.getUsersCart(userId);
        return cart.isEmpty().flatMap(empty -> {
            if (empty) {
                return Observable.just("Cart is empty");
            } else {
                return cart;
            }
        });
    }
}
