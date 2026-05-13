package com.oop.lab3.service;
import com.oop.lab3.model.TgApi;
import com.oop.lab3.repository.TgApiRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TgApiService {

    // репозиторий для операций с таблицей tg_apis
    private final TgApiRepository tgApiRepository;

    // фабрика entity manager для чтения идентификатора сохраненной сущности
    private final EntityManagerFactory entityManagerFactory;

    // конструктор для внедрения repository через Spring DI
    public TgApiService(TgApiRepository tgApiRepository, EntityManagerFactory entityManagerFactory) {
        this.tgApiRepository = tgApiRepository;
        this.entityManagerFactory = entityManagerFactory;
    }

    // получить всех tgAPI
    public List<TgApi> getAll() {
        return tgApiRepository.findAll();
    }

    // по id
    public Optional<TgApi> getById(long id) {
        return tgApiRepository.findById(id);
    }

    // пустой конструктор
    public long createFull(String botToken, long chatId) {
        TgApi saved = tgApiRepository.save(new TgApi(botToken, chatId));
        return (Long) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(saved);
    }

    // нет токена
    public long createToken(String botToken) {
        TgApi saved = tgApiRepository.save(new TgApi(botToken));
        return (Long) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(saved);
    }

    // нет часа айди
    public long createChat(long chatId) {
        TgApi saved = tgApiRepository.save(new TgApi("NULL", chatId));
        return (Long) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(saved);
    }

    // удалить по id
    public boolean deleteById(long id) {
        if (!tgApiRepository.existsById(id)) {
            return false;
        }
        tgApiRepository.deleteById(id);
        return true;
    }

    // родительская инициализация
    public Optional<String> initialize(long id) {
        return tgApiRepository.findById(id).map(TgApi::initialize);
    }

    // родительская загрузка данных
    public Optional<String> fetchData(long id, String command) {
        return tgApiRepository.findById(id).map(api -> api.fetchData(command));
    }

    // вызвать предметный метод по команде
    // в плюсах у меня тут были if-ы
    public Optional<Object> run(long id, String command) {
        Optional<TgApi> apiOptional = tgApiRepository.findById(id);
        if (apiOptional.isEmpty()) return Optional.empty();
        TgApi api = apiOptional.get();
        // выбираем конкретный метод
        return switch (command) {
            case "sendMessage" -> Optional.of(api.sendMessage());
            case "sendPhoto" -> Optional.of(api.sendPhoto());
            default -> Optional.of("Команда не поддерживается");
        };
    }
}
