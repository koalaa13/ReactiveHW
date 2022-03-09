package server.handler;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import model.Product;
import mongodb.MongoDBProvider;
import repository.ProductRepository;
import rx.Observable;

public class AddProductHandler implements Handler {
    private final ProductRepository productRepository;

    public AddProductHandler(MongoDBProvider provider) {
        this.productRepository = new ProductRepository(provider);
    }

    @Override
    public Observable<String> handle(HttpServerRequest<ByteBuf> req) {
        Product product;
        try {
            product = getProductFromURI(req);
        } catch (Exception e) {
            return Observable.just("Error happened while adding new product: " + e.getMessage());
        }
        return productRepository.save(product);
    }

    private static Product getProductFromURI(HttpServerRequest<ByteBuf> req) throws Exception {
        var paramsMap = req.getQueryParameters();
        if (!paramsMap.containsKey("id") || !paramsMap.containsKey("name")
                || !paramsMap.containsKey("description") || !paramsMap.containsKey("price")) {
            throw new Exception("Need all product fields");
        }
        int id;
        String name = paramsMap.get("name").get(0), description = paramsMap.get("description").get(0);
        double price;
        try {
            price = Double.parseDouble(paramsMap.get("price").get(0));
        } catch (NumberFormatException ex) {
            throw new Exception("Price should be a float number");
        }
        try {
            id = Integer.parseInt(paramsMap.get("id").get(0));
        } catch (NumberFormatException e) {
            throw new Exception("Id should be a number");
        }
        return new Product(id, name, description, price);
    }
}
