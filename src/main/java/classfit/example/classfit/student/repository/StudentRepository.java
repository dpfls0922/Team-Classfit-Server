package classfit.example.classfit.student.repository;

import classfit.example.classfit.student.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findAll(Pageable pageable);

    @Query("SELECT cs.subClass.subClassName FROM ClassStudent cs " +
        "JOIN cs.student s " +
        "WHERE s.id = :studentId")
    List<String> findSubClassesByStudentId(@Param("studentId") Long studentId);

    Optional<List<Student>> findAllByName(String studentName);

    @Query("SELECT DISTINCT s FROM Student s " +
        "JOIN ClassStudent cs ON s.id = cs.student.id " +
        "JOIN SubClass sc ON cs.subClass.id = sc.id " +
        "JOIN MainClass mc ON sc.mainClass.id = mc.id " +
        "WHERE mc.academy.id = :academyId")
    List<Student> findStudentsByAcademyId(Long academyId);
}
