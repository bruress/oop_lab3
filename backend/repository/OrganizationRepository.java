package com.oop.lab3.repository;
import com.oop.lab3.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// для бд операций
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
