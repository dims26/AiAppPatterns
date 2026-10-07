package com.dims.AiAppPatterns.routing;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routing")
public class RoutingController {

    private final IntentRouter router;

    public RoutingController(IntentRouter router) {
        this.router = router;
    }

    @PostMapping("/ask")
    public RoutedResponse ask(@RequestBody String prompt) {
        return router.route(prompt);
    }
}
