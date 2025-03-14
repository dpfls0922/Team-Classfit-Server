package classfit.example.classfit.course.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.course.domain.MainClass;
import classfit.example.classfit.course.dto.request.MainClassRequest;
import classfit.example.classfit.course.dto.response.AllMainClassResponse;
import classfit.example.classfit.course.dto.response.MainClassResponse;
import classfit.example.classfit.course.repository.MainClassRepository;
import classfit.example.classfit.member.domain.Member;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MainClassService {

    private final MainClassRepository mainClassRepository;

    @Transactional
    public MainClassResponse createMainClass(@AuthMember Member findMember, MainClassRequest req) {

        Academy academy = findMember.getAcademy();

        boolean exists = mainClassRepository.existsByAcademyAndMainClassName(academy,
                req.mainClassName());
        if (exists) {
            throw new ClassfitException(ErrorCode.MAIN_CLASS_ALREADY_EXISTS);
        }

        MainClass mainClass = new MainClass(req.mainClassName(), academy);
        mainClassRepository.save(mainClass);

        return MainClassResponse.from(mainClass);
    }

    @Transactional(readOnly = true)
    public List<AllMainClassResponse> showMainClass(@AuthMember Member findMember) {

        Academy academy = findMember.getAcademy();

        List<MainClass> mainClasses = mainClassRepository.findByAcademy(academy);

        return mainClasses.stream().map(mainClass -> new AllMainClassResponse(mainClass.getId(),
                mainClass.getMainClassName())).toList();
    }

    @Transactional
    public void deleteMainClass(@AuthMember Member findMember, Long mainClassId) {

        MainClass mainClass = mainClassRepository.findById(mainClassId).orElseThrow(
                () -> new ClassfitException(ErrorCode.MAIN_CLASS_NOT_FOUND));

        if (!Objects.equals(findMember.getAcademy(), mainClass.getAcademy())) {
            throw new ClassfitException(ErrorCode.ACADEMY_ACCESS_INVALID);
        }

        mainClassRepository.delete(mainClass);

    }

    @Transactional
    public MainClassResponse updateMainClass(@AuthMember Member findMember, Long mainClassId,
            MainClassRequest request) {

        MainClass mainClass = mainClassRepository.findById(mainClassId).orElseThrow(
                () -> new ClassfitException(ErrorCode.MAIN_CLASS_NOT_FOUND));

        if (!Objects.equals(findMember.getAcademy(), mainClass.getAcademy())) {
            throw new ClassfitException(ErrorCode.ACADEMY_ACCESS_INVALID);
        }
        mainClass.updateMainClassName(request.mainClassName());
        return new MainClassResponse(mainClass.getId(), mainClass.getMainClassName());
    }
}
