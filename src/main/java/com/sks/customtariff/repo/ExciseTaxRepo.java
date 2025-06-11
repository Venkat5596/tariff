package com.sks.customtariff.repo;

import com.sks.customtariff.entity.ExciseTax;
import com.sks.customtariff.entity.ExciseTaxId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ExciseTaxRepo extends JpaRepository<ExciseTax, ExciseTaxId> {
}
