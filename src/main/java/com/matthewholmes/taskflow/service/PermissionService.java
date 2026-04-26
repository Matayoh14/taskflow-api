package com.matthewholmes.taskflow.service;

import org.springframework.stereotype.Service;

import com.matthewholmes.taskflow.model.Comment;
import com.matthewholmes.taskflow.model.Project;
import com.matthewholmes.taskflow.model.Task;
import com.matthewholmes.taskflow.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

	public boolean isAdmin(User user) {
		return user.getRole() == User.Role.ADMIN;
	}
	
	public boolean canAccessProject(User user, Project project) {
		// Universal admin access
		if(isAdmin(user)) {
			return true;
		}
		
		// Owner access
		if(project.getOwner().getId().equals(user.getId())) {
			return true;
		}
		
		// Involved user access
		return isInvolvedUser(user, project);
	}
	
	public boolean canModifyProject(User user, Project project) {
		// Universal admin modify
		if(isAdmin(user) ) {
			return true;
		}
		
		// Only owner modify
		return project.getOwner().getId().equals(user.getId());
	}
	
	public boolean canCreateTaskInProject(User user, Project project) {
		// Universal admin creation
		if(isAdmin(user)) {
			return true;
		}
		
		// Only owner creation
		return project.getOwner().getId().equals(user.getId());
	}
	
	public boolean canViewTask(User user, Task task) {
		// Universal admin viewing
		if(isAdmin(user)) {
			return true;
		}
		
		// Owner viewing
		if(task.getProject().getOwner().getId().equals(user.getId())) {
			return true;
		}
		
		// Creator viewing
		if(task.getCreator().getId().equals(user.getId())) {
			return true;
		}
		
		// Involved user viewing
		return isInvolvedUser(user, task.getProject());
	}
	
	public boolean canModifyTask(User user, Task task) {
		// Universal admin modifying
		if(isAdmin(user)) {
			return true;
		}
		
		// Owner modifying
		if(task.getProject().getOwner().getId().equals(user.getId())) {
			return true;
		}
		
		// Creator modifying
		if(task.getCreator().getId().equals(user.getId())) {
			return true;
		}
		
		// Assignee modifying
		return task.getAssignee().getId().equals(user.getId());
	}
	
	public boolean canChangeTaskAssignee(User user, Task task) {
		// Universal admin modifying
		if(isAdmin(user)) {
			return true;
		}
		
		// Owner modifying
		if(task.getProject().getOwner().getId().equals(user.getId())) {
			return true;
		}
		
		// Creator modifying
		return task.getCreator().getId().equals(user.getId());
	}
	
	public boolean canDeleteTask(User user, Task task) {
		// Universal admin delete
		if(isAdmin(user)) {
			return true;
		}
		
		// Owner delete
		if(task.getProject().getOwner().getId().equals(user.getId())) {
			return true;
		}
		
		// Creator delete
		return task.getCreator().getId().equals(user.getId());
	}
	
	public boolean canDeleteComment(User user, Comment comment) {
		// Universal admin delete
		if(isAdmin(user)) {
			return true;
		}
		
		// Owner delete
		if(comment.getTask().getProject().getOwner().getId().equals(user.getId())) {
			return true;
		}
		
		// Author delete
		return comment.getAuthor().getId().equals(user.getId());
	}
	
	private boolean isInvolvedUser(User user, Project project) {
		return project.getTasks().stream()
				.anyMatch(task -> task.getAssignee() != null &&
				task.getAssignee().getId().equals(user.getId()));
	}
}
