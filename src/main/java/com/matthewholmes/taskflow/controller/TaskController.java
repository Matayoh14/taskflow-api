package com.matthewholmes.taskflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matthewholmes.taskflow.dto.TaskRequest;
import com.matthewholmes.taskflow.dto.TaskResponse;
import com.matthewholmes.taskflow.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;
	
	@PostMapping
	public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
		return new ResponseEntity<>(taskService.createTask(request), HttpStatus.CREATED);
	}
	
	@GetMapping("/project/{projectId}")
	public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable String projectId) {
		return ResponseEntity.ok(taskService.getTasksByProject(projectId));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
		return ResponseEntity.ok(taskService.getTaskById(id));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<TaskResponse> updateTask(@Valid @RequestBody TaskRequest request, @PathVariable String id) {
		return ResponseEntity.ok(taskService.updateTask(request, id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable String id) {
		taskService.deleteTask(id);
		return ResponseEntity.noContent().build();
	}
}
