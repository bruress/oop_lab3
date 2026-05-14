package com.oop.lab3.service;

import com.oop.lab3.model.Organization;
import com.oop.lab3.model.TgApi;
import com.oop.lab3.model.VkApi;
import com.oop.lab3.repository.OrganizationRepository;
import com.oop.lab3.repository.TgApiRepository;
import com.oop.lab3.repository.VkApiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    // репозиторий организаций
    private final OrganizationRepository organizationRepository;
    // репозиторий vk api
    private final VkApiRepository vkApiRepository;
    // репозиторий tg api
    private final TgApiRepository tgApiRepository;

    // конструктор внедрения зависимостей
    public OrganizationService(OrganizationRepository organizationRepository,
                               VkApiRepository vkApiRepository,
                               TgApiRepository tgApiRepository) {
        this.organizationRepository = organizationRepository;
        this.vkApiRepository = vkApiRepository;
        this.tgApiRepository = tgApiRepository;
    }

    // создать организацию по имени
    public Organization create(String name) {
        String safeName = (name == null || name.isBlank()) ? "Новая организация" : name;
        return organizationRepository.save(new Organization(safeName));
    }

    // удалить организацию по id
    public boolean deleteById(long id) {
        if (!organizationRepository.existsById(id)) return false;
        organizationRepository.deleteById(id);
        return true;
    }

    // привязать vk api к организации
    public Optional<Organization> attachVkApi(long organizationId, long vkApiId) {
        Optional<Organization> orgOptional = organizationRepository.findById(organizationId);
        Optional<VkApi> apiOptional = vkApiRepository.findById(vkApiId);
        if (orgOptional.isEmpty() || apiOptional.isEmpty()) return Optional.empty();

        Organization org = orgOptional.get();
        VkApi api = apiOptional.get();
        org.addApi(api);

        vkApiRepository.save(api);
        return Optional.of(organizationRepository.save(org));
    }

    // привязать tg api к организации
    public Optional<Organization> attachTgApi(long organizationId, long tgApiId) {
        Optional<Organization> orgOptional = organizationRepository.findById(organizationId);
        Optional<TgApi> apiOptional = tgApiRepository.findById(tgApiId);
        if (orgOptional.isEmpty() || apiOptional.isEmpty()) return Optional.empty();

        Organization org = orgOptional.get();
        TgApi api = apiOptional.get();
        org.addApi(api);

        tgApiRepository.save(api);
        return Optional.of(organizationRepository.save(org));
    }
}
