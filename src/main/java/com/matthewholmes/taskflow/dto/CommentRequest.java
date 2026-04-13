package com.matthewholmes.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {

	@NotBlank(message = "Comment content is required")
	private String content;
	
	@NotNull(message = "Task ID is required")
	private String taskId;
}
