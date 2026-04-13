package com.matthewholmes.taskflow.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

	private String id;
	private String content;
	private String taskId;
	private String authorEmail;
	private String authorFullName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
