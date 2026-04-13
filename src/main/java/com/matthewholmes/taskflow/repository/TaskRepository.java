package com.matthewholmes.taskflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matthewholmes.taskflow.model.Project;
import com.matthewholmes.taskflow.model.Task;
import com.matthewholmes.taskflow.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
	List<Task> findByProject(Project project);
	List<Task> findByAssignee(User assignee);
	List<Task> findByCreator(User creator);
	List<Task> findByProjectAndStatus(Project project, Task.TaskStatus status);
}
