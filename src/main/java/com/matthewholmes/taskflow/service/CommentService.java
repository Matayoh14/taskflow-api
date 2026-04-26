package com.matthewholmes.taskflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matthewholmes.taskflow.dto.CommentRequest;
import com.matthewholmes.taskflow.dto.CommentResponse;
import com.matthewholmes.taskflow.model.Comment;
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
	private final PermissionService permissionService;
	
	@Transactional
	public CommentResponse createComment(CommentRequest request) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Task task = taskRepository.findById(request.getTaskId())
				.orElseThrow(() -> new RuntimeException("Task not found"));
		
		if(!permissionService.canViewTask(currentUser, task)) {
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
		
		if(!permissionService.canViewTask(currentUser, task)) {
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
		
		if(!permissionService.canViewTask(currentUser, comment.getTask())) {
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
		
		if(!comment.getAuthor().getId().equals(currentUser.getId())) {
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
		
		if(!permissionService.canDeleteComment(currentUser, comment)) {
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
	
}
