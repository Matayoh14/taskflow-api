package com.matthewholmes.taskflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matthewholmes.taskflow.dto.CommentRequest;
import com.matthewholmes.taskflow.dto.CommentResponse;
import com.matthewholmes.taskflow.model.Comment;
import com.matthewholmes.taskflow.model.Project;
import com.matthewholmes.taskflow.model.Task;
import com.matthewholmes.taskflow.model.User;
import com.matthewholmes.taskflow.repository.CommentRepository;
import com.matthewholmes.taskflow.repository.TaskRepository;
import com.matthewholmes.taskflow.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final TaskRepository taskRepository;
	private final UserRepository userRepository;
	
	@Transactional
	public CommentResponse createComment(CommentRequest request) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Task task = taskRepository.findById(request.getTaskId())
				.orElseThrow(() -> new RuntimeException("Task not found"));
		
		// Check access
		// TODO: Add admin privilege
		if(!isUserInvolvedWithProject(currentUser.getEmail(),task.getProject())) {
			throw new RuntimeException("Not authorized to access this project");
		}
		
		Comment comment = new Comment();
		comment.setAuthor(currentUser);
		comment.setContent(request.getContent());
		comment.setTask(task);
		
		Comment saved = commentRepository.save(comment);
		return mapToResponse(saved);
	}
	
	@Transactional(readOnly = true)
	public List<CommentResponse> getAllCommentsByTaskId(String taskId) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new RuntimeException("Task not found"));
		
		// Check access
		// TODO: Add admin privilege
		if(!isUserInvolvedWithProject(currentUser.getEmail(), task.getProject())) {
			throw new RuntimeException("Not authorized to access this task");
		}
		
		return commentRepository.findByTask(task)
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public CommentResponse getCommentById(String id) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Comment not found"));
		
		// Check access
		// TODO: Add admin privilege
		if(!isUserInvolvedWithProject(currentUser.getEmail(), comment.getTask().getProject())) {
			throw new RuntimeException("Not authorized to access this task");
		}
		
		return mapToResponse(comment);
	}
	
	@Transactional
	public CommentResponse updateComment(CommentRequest request, String id) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Comment not found"));
		
		// Check access
		// TODO: Add admin privilege
		if(!currentUser.getId().equals(comment.getAuthor().getId())) {
			throw new RuntimeException("Not authorized to edit this comment");
		}
		
		comment.setContent(request.getContent());
		
		Comment updated = commentRepository.save(comment);
		return mapToResponse(updated);
	}
	
	@Transactional
	public void deleteComment(String id) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Comment not found"));
		
		boolean isAuthor = currentUser.getId().equals(comment.getAuthor().getId());
		boolean isProjectOwner = currentUser.getId().equals(comment.getTask().getProject().getOwner().getId());
		
		// Check access
		// TODO: Add admin privelege
		if(!isAuthor && !isProjectOwner) {
			throw new RuntimeException("Not authorized to delete this comment");
		}
		
		commentRepository.delete(comment);
	}
	
	private CommentResponse mapToResponse(Comment comment) {
		return CommentResponse.builder()
				.id(comment.getId())
				.authorEmail(comment.getAuthor().getEmail())
				.authorFullName(comment.getAuthor().getFullName())
				.content(comment.getContent())
				.taskId(comment.getTask().getId())
				.createdAt(comment.getCreatedAt())
				.updatedAt(comment.getUpdatedAt())
				.build();
	}
	
	private User getAuthenticatedUser() {
		   String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		   return userRepository.findByEmail(userEmail)
		           .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
	}
	
	private boolean isUserInvolvedWithProject(String userEmail, Project project) {
		// Check if is owner
		if(project.getOwner().getEmail().equals((userEmail))) {
			return true;
		}
		
		// Check if user has any tasks assigned in this project
		List<Task> projectTasks = taskRepository.findByProject(project);
		return projectTasks.stream()
				.anyMatch(task -> 	task.getAssignee() != null &&
									task.getAssignee().getEmail().equals(userEmail));
	}
}
