package com.matthewholmes.taskflow.dto;

import com.matthewholmes.taskflow.model.Task.TaskPriority;
import com.matthewholmes.taskflow.model.Task.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequest {

	@NotBlank(message = "Task title is required")
	private String title;
	
	private String description;
	
	@NotNull(message = "Project ID is required")
	private String projectId;
	
	private String assigneeEmail;
	
	private TaskStatus status;
	
	private TaskPriority priority;
}
