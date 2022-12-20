package tv.factory.execution.factory.service;

import tv.factory.execution.base.model.BaseResponse;

public interface FactoryService {
    void startProduction();

    BaseResponse result();

    BaseResponse reset();
}
