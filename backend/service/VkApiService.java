package com.oop.lab3.service;
import com.oop.lab3.model.VkApi;
import com.oop.lab3.model.VkPost;
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
        Optional<VkApi> apiOptional = vkApiRepository.findById(id);
        if (apiOptional.isEmpty()) return List.of();
        return apiOptional.get().postsList();
    }

    // добавить пост конкретному vk api
    public Optional<VkPost> addPost(long id, String text, int likes) {
        // ищем родительский vk api по id
        Optional<VkApi> apiOptional = vkApiRepository.findById(id);
        // если не нашли, возвращаем пустой результат
        if (apiOptional.isEmpty()) return Optional.empty();
        // подставляем безопасный текст, если пришла пустая строка
        String safeText = (text == null || text.isBlank()) ? "пустой пост" : text;
        // добавляем пост в список родителя и сохраняем родителя
        VkApi api = apiOptional.get();
        VkPost saved = api.addPostItem(safeText, likes);
        vkApiRepository.save(api);
        // возвращаем созданный пост
        return Optional.of(saved);
    }

    // удалить пост по его id
    public boolean deletePostById(long postId) {
        List<VkApi> all = vkApiRepository.findAll();
        for (VkApi api : all) {
            if (api.removePostById(postId)) {
                vkApiRepository.save(api);
                return true;
            }
        }
        return false;
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
