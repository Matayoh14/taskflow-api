package com.matthewholmes.taskflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matthewholmes.taskflow.model.Comment;
import com.matthewholmes.taskflow.model.Task;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
	List<Comment> findByTask(Task task);
}
