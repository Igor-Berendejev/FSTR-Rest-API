package com.example.fstr.repository;

import com.example.fstr.model.Pass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PassRepository extends JpaRepository<Pass, Integer> {
    @Query("SELECT status FROM Pass WHERE id = ?1")
    String getStatusById(int id);
}
