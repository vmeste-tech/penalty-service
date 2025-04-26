package ru.kolpakovee.penalty_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kolpakovee.penalty_service.records.TaskDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "${integration.services.task-service.name}",
        url = "${integration.services.task-service.url}")
public interface TaskServiceClient {

    @GetMapping("/api/v1/tasks/{apartmentId}/overdue")
    List<TaskDto> getOverdueTasks(@PathVariable UUID apartmentId);

    @PutMapping("/api/v1/tasks/{taskId}/create-penalty")
    TaskDto changePenaltyStatus(@PathVariable UUID taskId, @RequestParam boolean isPenaltyCreated);
}
