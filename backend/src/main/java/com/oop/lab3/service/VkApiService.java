package com.oop.lab3.service;
import com.oop.lab3.model.VkApi;
import com.oop.lab3.repository.VkApiRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VkApiService {

    // репозиторий для операций с таблицей vk_apis
    private final VkApiRepository vkApiRepository;

    // фабрика entity manager для чтения идентификатора сохраненной сущности
    private final EntityManagerFactory entityManagerFactory;

    // конструктор для внедрения repository через Spring DI
    public VkApiService(VkApiRepository vkApiRepository, EntityManagerFactory entityManagerFactory) {
        this.vkApiRepository = vkApiRepository;
        this.entityManagerFactory = entityManagerFactory;
    }

    // получение всех vkAPI
    public List<VkApi> getAll() {
        return vkApiRepository.findAll();
    }

    // по айди
    public Optional<VkApi> getById(long id) {
        return vkApiRepository.findById(id);
    }

    // создать через пустой генератор
    public long createFull(String version, String token) {
        VkApi saved = vkApiRepository.save(new VkApi(version, token));
        return (Long) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(saved);
    }

    // создать с токеном введенным
    public long createToken(String token) {
        VkApi saved = vkApiRepository.save(new VkApi(token));
        return (Long) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(saved);
    }

    // создать с дефолтными значениями
    public long createDefault() {
        VkApi saved = vkApiRepository.save(new VkApi("5.199", ""));
        return (Long) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(saved);
    }

    // удалить по id
    public boolean deleteById(long id) {
        if (!vkApiRepository.existsById(id)) {
            return false;
        }
        vkApiRepository.deleteById(id);
        return true;
    }

    // родительская инициализация
    public Optional<String> initialize(long id) {
        return vkApiRepository.findById(id).map(VkApi::initialize);
    }

    // родительская fetchData
    public Optional<String> fetchData(long id, String command) {
        return vkApiRepository.findById(id).map(api -> api.fetchData(command));
    }

    // вызвать предметный метод по команде
    // в плюсах у меня тут были if-ы
    public Optional<Object> run(long id, String command) {
        Optional<VkApi> apiOptional = vkApiRepository.findById(id);
        if (apiOptional.isEmpty()) return Optional.empty();
        VkApi api = apiOptional.get();
        // выбираем конкретный метод
        return switch (command) {
            case "getText" -> Optional.of(api.getText());
            case "getLike" -> Optional.of(api.getLike());
            default -> Optional.of("Команда не поддерживается");
        };
    }
}
