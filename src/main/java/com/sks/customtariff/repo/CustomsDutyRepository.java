package com.sks.customtariff.repo;

import com.sks.customtariff.entity.CustomsDuty;
import com.sks.customtariff.entity.CustomsDutyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomsDutyRepository extends JpaRepository<CustomsDuty, CustomsDutyId> {
}
