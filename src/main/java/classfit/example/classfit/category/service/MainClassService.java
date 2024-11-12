package classfit.example.classfit.category.service;

import classfit.example.classfit.category.dto.request.MainClassRequest;
import classfit.example.classfit.category.dto.response.MainClassResponse;
import classfit.example.classfit.category.repository.MainClassRespository;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.domain.MainClass;
import classfit.example.classfit.domain.Member;
import classfit.example.classfit.exception.ClassfitException;
import classfit.example.classfit.member.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainClassService {

    private final MainClassRespository mainClassRespository;
    private final MemberRepository memberRepository;

    // 메인 클래스 추가
    @Transactional
    public MainClassResponse addMainClass(Long memberId, MainClassRequest req) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClassfitException("회원을 찾을 수 없어요", HttpStatus.NOT_FOUND));

        MainClass mainClass = new MainClass(req.mainClassName(), findMember);

        mainClassRespository.save(mainClass);

        return new MainClassResponse(mainClass.getId(), mainClass.getMainClassName());
    }

    // 메인 클래스 수정
    @Transactional
    public MainClassResponse updateMainClass(Long memberId, Long mainClassId,
            MainClassRequest req) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClassfitException("회원을 찾을 수 없어요", HttpStatus.NOT_FOUND));

        MainClass findMainClass = mainClassRespository.findById(mainClassId)
                .orElseThrow(
                        () -> new ClassfitException("메인 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        checkMemberRelationMainClass(findMember, findMainClass);

        findMainClass.updateMainClassName(req.mainClassName());
        return new MainClassResponse(findMainClass.getId(),
                findMainClass.getMainClassName());
    }

    // 메인 클래스 삭제
    @Transactional
    public void deleteMainClass(Long memberId, Long mainClassId) {

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClassfitException("회원을 찾을 수 없어요", HttpStatus.NOT_FOUND));

        MainClass mainClass = mainClassRespository.findById(mainClassId)
                .orElseThrow(
                        () -> new ClassfitException("해당 메인 클래스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        mainClassRespository.delete(mainClass);

    }


    private static void checkMemberRelationMainClass(Member findMember, MainClass findMainClass) {
        if (!Objects.equals(findMember.getId(), findMainClass.getMember().getId())) {
            throw new ClassfitException("사용자와 클래스가 일치하지 않습니다.", HttpStatus.FORBIDDEN);
        }
    }
}
