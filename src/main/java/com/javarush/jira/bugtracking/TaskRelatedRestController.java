package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.model.UserBelong;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Slf4j
public class TaskRelatedRestController {
    private final TaskService taskService;

    @PostMapping("/{id}")
    @Operation(summary = "DocId: Add tags", tags = "task",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tag were successfully added"),
                    @ApiResponse(responseCode = "400", description = "Bad parameters for request"),
                    @ApiResponse(responseCode = "500", description = "Some issues are appeared")
            })
    public ResponseEntity<Task> Task(@PathVariable(name = "id") Long id, @RequestBody List<String> tags) {
        return ResponseEntity.ok(taskService.addTags(id, tags));
    }

    @PostMapping("/subscribe")
    @Operation(summary = "DocId: Add subscription to task", tags = "task",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User subscribed to task"),
                    @ApiResponse(responseCode = "400", description = "Bad parameters for request"),
                    @ApiResponse(responseCode = "500", description = "Some issues are appeared")
            })
    public ResponseEntity<UserBelong> subscribeToTask(@RequestParam Long userId, @RequestParam Long taskId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GrantedAuthority role = authentication.getAuthorities().stream().findFirst().orElseThrow(() -> new RuntimeException("ex"));
        UserBelong userBelong = taskService.subscribeToTask(taskId, userId, role.toString());
        return ResponseEntity.ok(userBelong);

    }
}
