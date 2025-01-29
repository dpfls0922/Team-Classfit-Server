package classfit.example.classfit.studentExam.dto.response;

import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamPeriod;
import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.studentExam.dto.process.ExamClassStudent;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ShowExamDetailResponse(
        ExamPeriod examPeriod,
        String examName,
        LocalDate examDate,
        String mainClassName,
        String subClassName,
        Integer lowestScore,
        Integer perfectScore,
        String average,
        Integer highestScore,
        List<String> examRange,
        Standard standard,
        List<ExamClassStudent> examClassStudents
) {

    public static ShowExamDetailResponse from(
            Exam exam,
            List<ExamClassStudent> examClassStudents) {
        return ShowExamDetailResponse.builder()
                .examPeriod(exam.getExamPeriod())
                .examName(exam.getExamName())
                .examDate(exam.getExamDate())
                .mainClassName(exam.getMainClass().getMainClassName())
                .subClassName(exam.getSubClass().getSubClassName())
                .lowestScore(exam.getLowestScore())
                .perfectScore(exam.getPerfectScore())
                .average(String.valueOf(exam.getAverage()))
                .highestScore(exam.getHighestScore())
                .examRange(List.of(exam.getExamRange()))
                .standard(exam.getStandard())
                .examClassStudents(examClassStudents)
                .build();
    }

}
