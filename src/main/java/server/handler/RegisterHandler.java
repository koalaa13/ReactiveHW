package server.handler;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import model.Currency;
import model.User;
import mongodb.MongoDBProvider;
import repository.UserRepository;
import rx.Observable;

import java.util.Arrays;

public class RegisterHandler implements Handler {
    private final UserRepository userRepository;

    public RegisterHandler(MongoDBProvider provider) {
        userRepository = new UserRepository(provider);
    }

    @Override
    public Observable<String> handle(HttpServerRequest<ByteBuf> req) {
        User user;
        try {
            user = getUserFromURI(req);
        } catch (Exception e) {
            return Observable.just("Error happened while registering new user: " + e.getMessage());
        }
        return userRepository.save(user);
    }

    private static User getUserFromURI(HttpServerRequest<ByteBuf> req) throws Exception {
        var paramsMap = req.getQueryParameters();
        if (!paramsMap.containsKey("id") || !paramsMap.containsKey("username")
                || !paramsMap.containsKey("password") || !paramsMap.containsKey("currency")) {
            throw new Exception("Need all user fields");
        }
        int id;
        String username = paramsMap.get("username").get(0), password = paramsMap.get("password").get(0);
        Currency currency;
        try {
            currency = Currency.valueOf(paramsMap.get("currency").get(0));
        } catch (IllegalArgumentException e) {
            throw new Exception("Currency should be " + Arrays.toString(Currency.values()));
        }
        try {
            id = Integer.parseInt(paramsMap.get("id").get(0));
        } catch (NumberFormatException e) {
            throw new Exception("Id should be a number");
        }
        return new User(id, username, password, currency);
    }
}
