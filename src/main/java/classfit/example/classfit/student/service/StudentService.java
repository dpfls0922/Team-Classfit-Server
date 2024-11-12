package classfit.example.classfit.student.service;

import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.class_student.repository.ClassStudentRepository;
import classfit.example.classfit.domain.ClassStudent;
import classfit.example.classfit.domain.Student;
import classfit.example.classfit.domain.SubClass;
import classfit.example.classfit.student.dto.request.StudentRequest;
import classfit.example.classfit.student.dto.response.StudentResponse;
import classfit.example.classfit.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final SubClassRepository subClassRepository;
    private final ClassStudentRepository classStudentRepository;

    @Transactional
    public StudentResponse registerStudent(StudentRequest req) {

        SubClass subClass = subClassRepository.findBySubClassName(req.subClass())
            .orElseThrow(() -> new ClassCastException("해당하는 정보를 찾을 수 없습니다."));

        Student student = req.toEntity(true);
        studentRepository.save(student);

        ClassStudent classStudent = new ClassStudent();
        classStudent.setStudent(student);
        classStudent.setSubClass(subClass);
        classStudentRepository.save(classStudent);

        return new StudentResponse(student.getId(), student.getName());
    }

    @Transactional
    public void deleteStudent(Long studentId) {

        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ClassCastException("해당하는 학생 정보를 찾을 수 없습니다."));

        classStudentRepository.deleteByStudentId(studentId); // student_id에 해당하는 모든 class_student 삭제
        studentRepository.delete(student);
    }
}
