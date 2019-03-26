package com.info.email.web.rest;

import java.net.URI;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.info.email.model.JobDescriptor;
import com.info.email.services.JobService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class EmailResource {
	
	@Autowired
	private JobService jobService;

	/**
	 * POST /api/v1.0/groups/:group/jobs
	 * 
	 * @param group
	 * @param descriptor
	 * @return
	 */
	@PostMapping(path = "/groups/{group}/jobs")
	public ResponseEntity<Void> createJob(@PathVariable String group, @Valid @RequestBody JobDescriptor descriptor) {
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{job}").buildAndExpand(descriptor.getName()).toUri();
		jobService.createJob(group, descriptor);
		return ResponseEntity.created(location).build();
	}

	/**
	 * GET /api/v1.0/groups/:group/jobs
	 * 
	 * @param group
	 * @return
	 */
	// @CrossOrigin(origins = "*")
	@GetMapping(path = "/groups/{group}/jobs")
	public ResponseEntity<Set<JobDescriptor>> findGroupJobs(@PathVariable String group) {
		return ResponseEntity.ok(jobService.findGroupJobs(group));
	}

	/**
	 * GET /api/v1.0/jobs
	 * 
	 * @return
	 */
	@GetMapping(path = "/jobs")
	public ResponseEntity<Set<JobDescriptor>> findJobs() {
		return ResponseEntity.ok(jobService.findJobs());
	}

	/**
	 * GET /api/v1.0/groups/:group/jobs/:name
	 * 
	 * @param group
	 * @param name
	 * @return
	 */
	@GetMapping(path = "/groups/{group}/jobs/{name}")
	public ResponseEntity<JobDescriptor> findJob(@PathVariable String group, @PathVariable String name) {
		return jobService.findJob(group, name).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	/**
	 * PUT /api/v1.0/groups/:group/jobs/:name
	 * 
	 * @param group
	 * @param name
	 * @param descriptor
	 * @return
	 */
	@PutMapping(path = "/groups/{group}/jobs/{name}")
	public ResponseEntity<Void> updateJob(@PathVariable String group, @PathVariable String name,
			@Valid @RequestBody JobDescriptor descriptor) {
		jobService.updateJob(group, name, descriptor);
		return ResponseEntity.noContent().build();
	}

	/**
	 * DELETE /api/v1.0/groups/:group/jobs/:name
	 * 
	 * @param group
	 * @param name
	 * @return
	 */
	@DeleteMapping(path = "/groups/{group}/jobs/{name}")
	public ResponseEntity<Void> deleteJob(@PathVariable String group, @PathVariable String name) {
		jobService.deleteJob(group, name);
		return ResponseEntity.noContent().build();
	}

	/**
	 * PATCH /api/v1.0/groups/:group/jobs/:name/pause
	 * 
	 * @param group
	 * @param name
	 * @return
	 */
	@PatchMapping(path = "/groups/{group}/jobs/{name}/pause")
	public ResponseEntity<Void> pauseJob(@PathVariable String group, @PathVariable String name) {
		jobService.pauseJob(group, name);
		return ResponseEntity.noContent().build();
	}

	/**
	 * PATCH /api/v1.0/groups/:group/jobs/:name/resume
	 * 
	 * @param group
	 * @param name
	 * @return
	 */
	@PatchMapping(path = "/groups/{group}/jobs/{name}/resume")
	public ResponseEntity<Void> resumeJob(@PathVariable String group, @PathVariable String name) {
		jobService.resumeJob(group, name);
		return ResponseEntity.noContent().build();
	}
}
