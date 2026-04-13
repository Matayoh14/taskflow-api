package com.matthewholmes.taskflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matthewholmes.taskflow.model.Project;
import com.matthewholmes.taskflow.model.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
	List<Project> findByOwner(User owner);
}
