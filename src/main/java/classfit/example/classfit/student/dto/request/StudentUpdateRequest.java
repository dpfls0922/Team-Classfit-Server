package classfit.example.classfit.student.dto.request;

import classfit.example.classfit.common.validation.EnumValue;
import classfit.example.classfit.common.validation.NotBlankNullable;
import classfit.example.classfit.student.domain.Gender;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record StudentUpdateRequest(
    @NotBlankNullable
    @Size(max = 30) String name,

    @NotBlankNullable
    @EnumValue(target = Gender.class, message = "존재하지 않는 성별입니다.", ignoreCase = true)
    String gender,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past LocalDate birth,

    @NotBlankNullable
    @Size(max = 14) @Pattern(regexp = "^[0-9\\-]+$") String studentNumber,

    @NotBlankNullable
    @Size(max = 14) @Pattern(regexp = "^[0-9\\-]+$") String parentNumber,

    @NotBlankNullable
    @Size(max = 10) String grade,

    @Size(max = 30) List<Long> subClassList,

    @Size(max = 30) String address,

    Boolean isStudent,

    String remark,

    String counselingLog
) {
}
