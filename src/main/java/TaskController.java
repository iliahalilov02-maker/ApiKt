package com.example.taskapi;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final List<Map<String, Object>> tasks = List.of(
        Map.of("id", 1, "title", "Изучить Docker", "done", true),
        Map.of("id", 2, "title", "Собрать образ",    "done", true),
        Map.of("id", 3, "title", "Запустить контейнер", "done", false)
    );

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/tasks")
    public List<Map<String, Object>> getTasks() {
        return tasks;
    }

    @GetMapping("/tasks/{id}")
    public Map<String, Object> getTask(@PathVariable int id) {
        return tasks.stream()
                .filter(t -> (int) t.get("id") == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
    }
}