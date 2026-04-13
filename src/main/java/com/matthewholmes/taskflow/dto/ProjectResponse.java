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
public class ProjectResponse {

	private String id;
	private String name;
	private String description;
	private String ownerEmail;
	private String ownerFullName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
