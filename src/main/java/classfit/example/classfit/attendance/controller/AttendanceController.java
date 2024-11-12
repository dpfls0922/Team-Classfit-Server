package classfit.example.classfit.attendance.controller;

import classfit.example.classfit.attendance.dto.request.StudentAttendanceUpdateRequestDTO;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponseDTO;
import classfit.example.classfit.attendance.service.AttendanceService;
import classfit.example.classfit.attendance.service.AttendanceUpdateService;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.domain.ClassStudent;
import classfit.example.classfit.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final AttendanceUpdateService attendanceUpdateService;

    @GetMapping("/")
    public ApiResponse<List<StudentAttendanceResponseDTO>> getAttendance() {
        List<LocalDate> weekRange = attendanceService.getWeeklyAttendanceRange();
        List<Student> students = attendanceService.getAllStudents();
        List<StudentAttendanceResponseDTO> studentAttendances = attendanceService.getStudentAttendance(students, weekRange);
        return ApiResponse.success(studentAttendances, 200, "SUCCESS");
    }

    @GetMapping("/{mainClassId}/{subClassId}")
    public ApiResponse<List<StudentAttendanceResponseDTO>> getClassAttendance(
            @PathVariable("mainClassId") Long mainClassId,
            @PathVariable("subClassId") Long subClassId) {
        List<LocalDate> weekRange = attendanceService.getWeeklyAttendanceRange();
        List<ClassStudent> students = attendanceService.getClassStudentsByMainClassAndSubClass(mainClassId, subClassId);
        List<StudentAttendanceResponseDTO> studentAttendances = attendanceService.getStudentAttendance(students, weekRange);
        return ApiResponse.success(studentAttendances, 200, "SUCCESS");
    }

    @PatchMapping("/")
    public ApiResponse<List<StudentAttendanceResponseDTO>> updateAttendance(@RequestBody List<StudentAttendanceUpdateRequestDTO> requestDTO) {
        List<StudentAttendanceResponseDTO> updatedStudents = attendanceUpdateService.updateStudentAttendances(requestDTO);
        return ApiResponse.success(updatedStudents, 200, "UPDATED");
    }
}
