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

    @Query("SELECT s FROM Student s " +
        "JOIN s.classStudents cs " +
        "JOIN cs.subClass sc " +
        "WHERE s.id = :studentId " +
        "AND sc.mainClass.member.academy.id = :academyId ")
    Optional<Student> findByIdAndAcademyId(Long studentId, Long academyId);

    Optional<List<Student>> findAllByName(String studentName);

    @Query("SELECT s FROM Student s " +
        "JOIN s.classStudents cs " +
        "JOIN cs.subClass sc " +
        "WHERE sc.mainClass.member.academy.id = :academyId")
    Page<Student> findAllByAcademyId(@Param("academyId") Long academyId, Pageable pageable);
}
