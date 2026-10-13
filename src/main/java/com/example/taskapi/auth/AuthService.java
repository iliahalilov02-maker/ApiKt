package com.example.taskapi.auth;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    /** Хеш-таблица с закрытой адресацией — кэш логин → хеш пароля. */
    private final HashTable userCache = new HashTable();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Регистрация: хешируем пароль, кладём в БД и в хеш-таблицу. */
    public boolean register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return false;
        }
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash(password, salt);

        User user = new User(username, salt, hash);
        userRepository.save(user);

        // кладём в хеш-таблицу ключ=username, значение=hash
        userCache.put(username, hash);
        return true;
    }

    /** Логин: сначала ищем в хеш-таблице, при промахе — в БД. */
    public boolean login(String username, String password) {
        String cachedHash = userCache.get(username);
        String salt;

        if (cachedHash != null) {
            // хеш есть в памяти — берём соль из БД (она не секретна)
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) return false;
            salt = user.get().getSalt();
        } else {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) return false;
            salt = user.get().getSalt();
            cachedHash = user.get().getPasswordHash();
            userCache.put(username, cachedHash);
        }

        return PasswordHasher.verify(password, salt, cachedHash);
    }
}