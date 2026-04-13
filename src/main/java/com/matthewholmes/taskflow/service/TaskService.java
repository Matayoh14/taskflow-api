package com.matthewholmes.taskflow.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matthewholmes.taskflow.dto.TaskRequest;
import com.matthewholmes.taskflow.dto.TaskResponse;
import com.matthewholmes.taskflow.model.Project;
import com.matthewholmes.taskflow.model.Task;
import com.matthewholmes.taskflow.model.User;
import com.matthewholmes.taskflow.repository.ProjectRepository;
import com.matthewholmes.taskflow.repository.TaskRepository;
import com.matthewholmes.taskflow.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepository;
	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;
	
	@Transactional
	public TaskResponse createTask(TaskRequest request) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Project project = projectRepository.findById(request.getProjectId())
				.orElseThrow(() -> new RuntimeException("Project not found"));
		
		// Access check
		// TODO: Add admin privilege
		if(!project.getOwner().getId().equals(currentUser.getId())) {
			throw new RuntimeException("Not authorized to access this project");
		}
		
		Task task = new Task();
		task.setCreator(currentUser);
		task.setTitle(request.getTitle());
		task.setProject(project);
		task.setDescription(request.getDescription());
		task.setPriority(request.getPriority());
		task.setStatus(request.getStatus());
		
		if(request.getAssigneeEmail() != null) {
			User assignee = userRepository.findByEmail(request.getAssigneeEmail())
				.orElseThrow(() -> new RuntimeException("Assignee not found"));
			task.setAssignee(assignee);
		}
		
		Task saved = taskRepository.save(task);
		return mapToResponse(saved);
	}
	
	@Transactional(readOnly = true)
	public List<TaskResponse> getTasksByProject(String projectId) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found"));
		
		// Check Access
		// TODO: Add admin privilege
		if(!isUserInvolvedWithProject(currentUser.getEmail(), project)) {
			throw new RuntimeException("Not authorized to view task");
		}
		
		return taskRepository.findByProject(project)
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());		
	}
	
	@Transactional(readOnly = true)
	public TaskResponse getTaskById(String id) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Task not found"));
		Project project = task.getProject();
	
		// Check Access
		// TODO: Add admin privilege
		if(!isUserInvolvedWithProject(currentUser.getEmail(), project)) {
			throw new RuntimeException("Not authorized to view task");
		}
		
		return mapToResponse(task);
	}
	
	@Transactional
	public TaskResponse updateTask(TaskRequest request, String id) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Task not found"));
		
		boolean isCreator = task.getCreator().getId().equals(currentUser.getId());
		boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId());
		
		// Access check
		// TODO: Add admin privilege
		if(!isCreator && !isAssignee) {
			throw new RuntimeException("Not authorized to update this task");
		}
		
		task.setTitle(request.getTitle());
		task.setPriority(request.getPriority());
		task.setStatus(request.getStatus());
		task.setDescription(request.getDescription());
		
		// Only owner can change assignee
		// TODO: Add admin privilege
		if(request.getAssigneeEmail() != null) {
			if(!task.getCreator().getId().equals(currentUser.getId())) {
				throw new RuntimeException("Only task creator can reassign task");
			}
			User assignee = userRepository.findByEmail(request.getAssigneeEmail())
					.orElseThrow(() -> new RuntimeException("Assignee not found"));
			task.setAssignee(assignee);
		}
		
		Task updated = taskRepository.save(task);
		return mapToResponse(updated);
	}
	
	@Transactional
	public void deleteTask(String id) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Task not found"));
	
		// Access check
		// TODO: Add admin privilege
		if(!task.getCreator().getId().equals(currentUser.getId())) {
			throw new RuntimeException("Not authorized to delete this task");
		}
		
		taskRepository.delete(task);
	}
	
	
	private TaskResponse mapToResponse(Task task) {
		return TaskResponse.builder()
				.id(task.getId())
				.title(task.getTitle())
				.description(task.getDescription())
				.status(task.getStatus())
				.priority(task.getPriority())
				.projectId(task.getProject().getId())
				.projectName(task.getProject().getName())
				.assigneeEmail(task.getAssignee() != null ? task.getAssignee().getEmail() : null)
				.assigneeFullName(task.getAssignee() != null ? task.getAssignee().getFullName() : null)
				.creatorEmail(task.getCreator().getEmail())
				.creatorFullName(task.getCreator().getFullName())
				.comments(new ArrayList<>())
				.createdAt(task.getCreatedAt())
				.updatedAt(task.getUpdatedAt())
				.build();
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
	
	private User getAuthenticatedUser() {
		   String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		   return userRepository.findByEmail(userEmail)
		           .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
	}
}
