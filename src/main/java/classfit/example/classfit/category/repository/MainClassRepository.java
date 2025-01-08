package classfit.example.classfit.category.repository;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainClassRepository extends JpaRepository<MainClass, Long> {
    List<MainClass> findAllByOrderByMainClassNameAsc();

    List<MainClass> findByMemberAcademy(Academy academy);

    boolean existsByMember_AcademyAndMainClassName(Academy academy, String mainClassName);
}
