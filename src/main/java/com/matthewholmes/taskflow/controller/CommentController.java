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

import com.matthewholmes.taskflow.dto.CommentRequest;
import com.matthewholmes.taskflow.dto.CommentResponse;
import com.matthewholmes.taskflow.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;
	
	@PostMapping
	public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest request) {
		return new ResponseEntity<>(commentService.createComment(request), HttpStatus.CREATED);
	}
	
	@GetMapping("/task/{taskId}")
	public ResponseEntity<List<CommentResponse>> getAllCommentsByTaskId(@PathVariable String taskId) {
		return ResponseEntity.ok(commentService.getAllCommentsByTaskId(taskId));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CommentResponse> getCommentById(@PathVariable String id) {
		return ResponseEntity.ok(commentService.getCommentById(id));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<CommentResponse> updateComment(@Valid @RequestBody CommentRequest request, @PathVariable String id) {
		return ResponseEntity.ok(commentService.updateComment(request, id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteComment(@PathVariable String id) {
		commentService.deleteComment(id);
		return ResponseEntity.noContent().build();
	}
	
}
