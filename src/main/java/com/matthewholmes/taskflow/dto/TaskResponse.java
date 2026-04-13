package com.matthewholmes.taskflow.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.matthewholmes.taskflow.model.Task.TaskPriority;
import com.matthewholmes.taskflow.model.Task.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

	private String id;
	private String title;
	private String description;
	private TaskStatus status;
	private TaskPriority priority;
	private String projectId;
	private String projectName;
	private String assigneeEmail;
	private String assigneeFullName;
	private String creatorEmail;
	private String creatorFullName;
	private List<CommentResponse> comments;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
