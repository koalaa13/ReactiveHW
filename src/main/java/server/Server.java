package server;

import io.reactivex.netty.protocol.http.server.HttpServer;
import mongodb.MongoDBProvider;
import rx.Observable;
import server.handler.*;

import java.util.Map;

public class Server {
    private static Map<String, Handler> MAPPING;

    public static void main(String[] args) {
        MongoDBProvider provider = new MongoDBProvider("eshop", 27017);
        MAPPING = Map.of("/register", new RegisterHandler(provider),
                "/users", new UsersHandler(provider),
                "/user", new UserHandler(provider),
                "/add_product", new AddProductHandler(provider),
                "/products", new ProductsHandler(provider),
                "/add_to_cart", new AddToCartHandler(provider),
                "/cart", new UserCartHandler(provider),
                "/delete_from_cart", new DeleteFromCartHandler(provider),
                "/clear_cart", new ClearCartHandler(provider));

        HttpServer.newServer(8080)
                .start((req, resp) -> {
                    final String path = req.getDecodedPath();
                    Observable<String> res;
                    if (MAPPING.containsKey(path)) {
                        res = MAPPING.get(path).handle(req);
                    } else {
                        res = Observable.just("Unknown request " + path);
                    }
                    return resp.writeString(res);
                })
                .awaitShutdown();
    }
}
