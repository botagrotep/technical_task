package technikal.task.fishmarket.dtos.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotEmpty
        @Size(min = 4, max = 50)
        String username,
        @NotEmpty
        @Size(min = 4, max = 20)
        String password
) {
}
