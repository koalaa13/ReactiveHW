package repository;

import model.User;
import mongodb.MongoDBProvider;
import rx.Observable;

import static com.mongodb.client.model.Filters.eq;

public class UserRepository {
    private final static String dbKey = "users";
    private final MongoDBProvider provider;

    public UserRepository(MongoDBProvider provider) {
        this.provider = provider;
    }

    public Observable<User> getUserById(int id) {
        return provider.getDatabase()
                .getCollection(dbKey)
                .find(eq("id", id))
                .toObservable()
                .map(User::new);
    }

    public Observable<String> save(User user) {
        return getUserById(user.getId()).isEmpty().flatMap(notFound -> {
            if (notFound) {
                return provider.getDatabase()
                        .getCollection(dbKey)
                        .insertOne(user.convertToDocument())
                        .map(suc -> "User " + user + " was successfully added.");
            } else {
                return Observable.just("User with such id " + user.getId() + " already exists");
            }
        });
    }

    public Observable<String> getAll() {
        return provider.getDatabase()
                .getCollection(dbKey)
                .find()
                .toObservable()
                .map(User::new)
                .map(User::toString);
    }
}
