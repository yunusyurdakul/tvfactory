package tv.factory.execution.factory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tv.factory.execution.base.model.BaseResponse;
import tv.factory.execution.factory.service.FactoryService;

import java.util.concurrent.ForkJoinPool;

@RestController
@RequestMapping("api/factory/")
@RequiredArgsConstructor
public class FactoryController {
    private final FactoryService factoryService;

    @GetMapping("start")
    public ResponseEntity startProduction() {
        ForkJoinPool.commonPool().submit(() -> {
            factoryService.startProduction();
        });
        return ResponseEntity.ok(BaseResponse.response(null, "Factory is started!"));
    }

    @GetMapping("result")
    public ResponseEntity result() {
        return ResponseEntity.ok(factoryService.result());
    }

    @GetMapping("reset")
    public ResponseEntity reset() {
        return ResponseEntity.ok(factoryService.reset());
    }
}
