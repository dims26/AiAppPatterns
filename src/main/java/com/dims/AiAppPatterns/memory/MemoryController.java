package com.dims.AiAppPatterns.memory;

import com.dims.AiAppPatterns.routing.RoutedResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/memory")
public class MemoryController {

    private final MemoryIntentRouter router;

    public MemoryController(MemoryIntentRouter router) {
        this.router = router;
    }

    @PostMapping("/ask")
    public RoutedResponse ask(@RequestHeader("X-Session-Id") String sessionId, @RequestBody String message) {
        return router.route(sessionId, message);
    }
}