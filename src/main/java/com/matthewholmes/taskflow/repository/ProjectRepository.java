package com.matthewholmes.taskflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.matthewholmes.taskflow.model.Project;
import com.matthewholmes.taskflow.model.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
	List<Project> findByOwner(User owner);
	@Query("SELECT DISTINCT t.project FROM Task t WHERE t.assignee.email = :email")
	List<Project> findProjectsByAssigneeEmail(@Param("email") String email);
}
