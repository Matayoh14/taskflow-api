package com.matthewholmes.taskflow.repository;

import com.matthewholmes.taskflow.model.TestConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestConnectionRepository extends JpaRepository<TestConnection, Long> {
}
