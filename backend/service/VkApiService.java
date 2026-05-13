package com.oop.lab3.service;
import com.oop.lab3.model.VkApi;
import com.oop.lab3.model.VkPost;
import com.oop.lab3.repository.VkPostRepository;
import com.oop.lab3.repository.VkApiRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VkApiService {

    // репозиторий для операций с таблицей vk_apis
    private final VkApiRepository vkApiRepository;
    // репозиторий для операций с таблицей vk_posts
    private final VkPostRepository vkPostRepository;

    // фабрика entity manager для чтения идентификатора сохраненной сущности
    private final EntityManagerFactory entityManagerFactory;

    // конструктор для внедрения repository через Spring DI
    public VkApiService(VkApiRepository vkApiRepository, VkPostRepository vkPostRepository, EntityManagerFactory entityManagerFactory) {
        this.vkApiRepository = vkApiRepository;
        this.vkPostRepository = vkPostRepository;
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
        String safeVersion = (version == null || version.isBlank()) ? "5.199" : version;
        String safeToken = token == null ? "" : token;
        VkApi saved = vkApiRepository.save(new VkApi(safeVersion, safeToken));
        return (Long) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(saved);
    }

    // создать с токеном введенным
    public long createToken(String token) {
        String safeToken = token == null ? "" : token;
        VkApi saved = vkApiRepository.save(new VkApi(safeToken));
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

    // обновить версию и токен по id
    public Optional<VkApi> updateById(long id, String version, String token) {
        Optional<VkApi> apiOptional = vkApiRepository.findById(id);
        if (apiOptional.isEmpty()) {
            return Optional.empty();
        }
        VkApi api = apiOptional.get();
        // если прислали пустую версию, оставляем дефолт
        String safeVersion = (version == null || version.isBlank()) ? "5.199" : version;
        // null токен не сохраняем, чтоб не словить not null в бд
        String safeToken = token == null ? "" : token;
        // обновляем поля через предметный метод
        api.updateVkData(safeVersion, safeToken);
        return Optional.of(vkApiRepository.save(api));
    }

    // получить посты конкретного vk api
    public List<VkPost> getPosts(long id) {
        return vkPostRepository.findByVkApiId(id);
    }

    // добавить пост конкретному vk api
    public Optional<VkPost> addPost(long id, String text, int likes) {
        // ищем родительский vk api по id
        Optional<VkApi> apiOptional = vkApiRepository.findById(id);
        // если не нашли, возвращаем пустой результат
        if (apiOptional.isEmpty()) return Optional.empty();
        // подставляем безопасный текст, если пришла пустая строка
        String safeText = (text == null || text.isBlank()) ? "пустой пост" : text;
        // сохраняем пост и привязываем к найденному vk api
        VkPost saved = vkPostRepository.save(new VkPost(safeText, likes, apiOptional.get()));
        // возвращаем созданный пост
        return Optional.of(saved);
    }

    // удалить пост по его id
    public boolean deletePostById(long postId) {
        if (!vkPostRepository.existsById(postId)) return false;
        vkPostRepository.deleteById(postId);
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
