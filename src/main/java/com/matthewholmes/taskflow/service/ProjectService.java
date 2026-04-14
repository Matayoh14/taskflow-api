package com.matthewholmes.taskflow.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matthewholmes.taskflow.dto.ProjectRequest;
import com.matthewholmes.taskflow.dto.ProjectResponse;
import com.matthewholmes.taskflow.model.Project;
import com.matthewholmes.taskflow.model.Task;
import com.matthewholmes.taskflow.model.User;
import com.matthewholmes.taskflow.repository.ProjectRepository;
import com.matthewholmes.taskflow.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;
	
	@Transactional
	public ProjectResponse createProject(ProjectRequest request) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		
		Project project = new Project();
		project.setName(request.getName());
		project.setDescription(request.getDescription());
		project.setOwner(currentUser);
		
		Project saved = projectRepository.save(project);
		return mapToResponse(saved);
		
	}
	
	@Transactional(readOnly = true)
	public List<ProjectResponse> getAllProjects() {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		
		// Get owned projects
		List<Project> ownedProjects = projectRepository.findByOwner(currentUser);
		// Get involved projects
		List<Project> involvedProjects = projectRepository.findProjectsByAssigneeEmail(currentUser.getEmail());
		// Combine
		Set<Project> allProjects = new HashSet<>();
		allProjects.addAll(ownedProjects);
		allProjects.addAll(involvedProjects);
		
		return allProjects
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public ProjectResponse getProjectById(String id) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
		
		// Check access
		// TODO: Add admin privilege
		if(!isUserInvolvedWithProject(currentUser.getEmail(), project)) {
			throw new RuntimeException("Not authorized to access this project");
		}
		
		return mapToResponse(project);
	}
	
	@Transactional
	public ProjectResponse updateProject(String id, ProjectRequest request) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

		// Check access
		// TODO: Add admin privilege
		if(!project.getOwner().getId().equals(currentUser.getId())) {
			throw new RuntimeException("Not authorized to update this project");
		}
		
		project.setName(request.getName());
		project.setDescription(request.getDescription());
		
		Project updated = projectRepository.save(project);
		return mapToResponse(updated);
	}
	
	@Transactional
	public void deleteProject(String id) {
		// Get authenticated user
		User currentUser = getAuthenticatedUser();
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
		
		// Check access
		// TODO: Add admin privilege
		if(!project.getOwner().getId().equals(currentUser.getId())) {
			throw new RuntimeException("Not authorized to delete this project");
		}
		
		projectRepository.delete(project);
	}
	
	
	private ProjectResponse mapToResponse(Project project) {
		return ProjectResponse.builder()
				.id(project.getId())
				.name(project.getName())
				.description(project.getDescription())
				.ownerEmail(project.getOwner().getEmail())
				.ownerFullName(project.getOwner().getFullName())
				.createdAt(project.getCreatedAt())
				.updatedAt(project.getUpdatedAt())
				.build();
	}
	
	private boolean isUserInvolvedWithProject(String userEmail, Project project) {
		// Check if is owner
		if(project.getOwner().getEmail().equals((userEmail))) {
			return true;
		}
		
		// Check if user has any tasks assigned in this project
		List<Task> projectTasks = project.getTasks();
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
