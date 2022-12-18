package tv.factory.execution.factory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tv.factory.execution.base.model.BaseResponse;
import tv.factory.execution.factory.service.FactoryService;

@RestController
@RequestMapping("api/factory/")
@RequiredArgsConstructor
public class FactoryController {
    private final FactoryService factoryService;

    @GetMapping("start")
    public ResponseEntity startProduction() {
        return ResponseEntity.ok(BaseResponse.success(factoryService.startProduction(), "Factory reached its goal!"));
    }
}
