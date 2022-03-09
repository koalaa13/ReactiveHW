package server.handler;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import mongodb.MongoDBProvider;
import repository.CartRepository;
import rx.Observable;

public class DeleteFromCartHandler implements Handler {
    private final CartRepository cartRepository;

    public DeleteFromCartHandler(MongoDBProvider provider) {
        this.cartRepository = new CartRepository(provider);
    }

    @Override
    public Observable<String> handle(HttpServerRequest<ByteBuf> req) {
        var paramsMap = req.getQueryParameters();
        if (!paramsMap.containsKey("user_id") || !paramsMap.containsKey("product_id")) {
            return Observable.just("Need user id and product id");
        }
        int userId, productId;
        try {
            userId = Integer.parseInt(paramsMap.get("user_id").get(0));
        } catch (NumberFormatException e) {
            return Observable.just("User id should be an integer number");
        }
        try {
            productId = Integer.parseInt(paramsMap.get("product_id").get(0));
        } catch (NumberFormatException e) {
            return Observable.just("Product id should be an integer number");
        }
        return cartRepository.deleteItemFromCart(userId, productId);
    }
}
