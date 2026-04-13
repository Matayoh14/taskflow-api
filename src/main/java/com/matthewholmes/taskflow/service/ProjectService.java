package com.matthewholmes.taskflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matthewholmes.taskflow.dto.ProjectRequest;
import com.matthewholmes.taskflow.dto.ProjectResponse;
import com.matthewholmes.taskflow.model.Project;
import com.matthewholmes.taskflow.model.User;
import com.matthewholmes.taskflow.repository.ProjectRepository;
import com.matthewholmes.taskflow.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;
	
	public ProjectResponse createProject(ProjectRequest request) {
		// Get authenticated user
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User owner = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new RuntimeException("Authenticated user not found"));
		
		Project project = new Project();
		project.setName(request.getName());
		project.setDescription(request.getDescription());
		project.setOwner(owner);
		
		Project saved = projectRepository.save(project);
		return mapToResponse(saved);
		
	}
	
	@Transactional(readOnly = true)
	public List<ProjectResponse> getAllProjects() {
		// Get authenticated user
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User currentUser = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		return projectRepository.findByOwner(currentUser)
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public ProjectResponse getProjectById(String id) {
		
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
		return mapToResponse(project);
	}
	
	public ProjectResponse updateProject(String id, ProjectRequest request) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User owner = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new RuntimeException("Authenticated user not found"));
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

		// Check access
		// TODO: Add admin privilege
		if(!project.getOwner().getId().equals(owner.getId())) {
			throw new RuntimeException("Not authorized to update this project");
		}
		
		project.setName(request.getName());
		project.setDescription(request.getDescription());
		
		Project updated = projectRepository.save(project);
		return mapToResponse(updated);
	}
	
	@Transactional
	public void deleteProject(String id) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User owner = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new RuntimeException("Authenticated user not found"));
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
		
		// Check access
		// TODO: Add admin privilege
		if(!project.getOwner().getId().equals(owner.getId())) {
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
}
