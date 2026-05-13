package com.oop.lab3.repository;

import com.oop.lab3.model.VkPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VkPostRepository extends JpaRepository<VkPost, Long> {
    List<VkPost> findByVkApiId(Long vkApiId);
}
