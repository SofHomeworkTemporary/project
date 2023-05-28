package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.internal.mapper.TaskMapper;
import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.model.UserBelong;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.bugtracking.internal.repository.UserBelongRepository;
import com.javarush.jira.bugtracking.to.ObjectType;
import com.javarush.jira.bugtracking.to.TaskTo;
import com.javarush.jira.login.internal.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class TaskService extends BugtrackingService<Task, TaskTo, TaskRepository> {
    private final UserBelongRepository userBRepo;
    private final UserRepository userRepository;
    public TaskService(TaskRepository repository, TaskMapper mapper, UserBelongRepository userBRepo, UserRepository userRepository) {
        super(repository, mapper);
        this.userBRepo = userBRepo;
        this.userRepository = userRepository;
    }

    public List<TaskTo> getAll() {
        return mapper.toToList(repository.getAll());
    }

    @Transactional
    public Task addTags(Long id, List<String> tags) {
        Task task = repository.getReferenceById(id);
        Set<String> taskTags = task.getTags();
        if ((tags.size() + taskTags.size()) <= 32 && tags.size() + taskTags.size() >= 2) {
            taskTags.addAll(tags);
        } else{
            throw new ValidationException("The number of tags should not exceed 32 or be lower than 2");
        }
        task.setTags(taskTags);
        return task;
    }

    public UserBelong subscribeToTask(Long taskId, Long userId, String role){
        UserBelong taskBelong= UserBelong.builder()
                .objectId(taskId)
                .userId(userId)
                .objectType(ObjectType.TASK)
                .userTypeCode(role)
                .build();
        return userBRepo.save(taskBelong);
    }
}
