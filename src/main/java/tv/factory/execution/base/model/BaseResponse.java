package tv.factory.execution.base.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {
    private Object data;
    private String message;

    public static final BaseResponse response(Object data, String message) {
        return BaseResponse.builder()
                .data(data).
                message(message)
                .build();
    }

    public static final BaseResponse response(String message) {
        return BaseResponse.builder()
                .message(message)
                .build();
    }
}
