package classfit.example.classfit.member.service;

import static classfit.example.classfit.common.exception.ClassfitException.ACADEMY_MEMBERS_NOT_FOUND;
import static classfit.example.classfit.common.exception.ClassfitException.CATEGORY_NOT_FOUND;
import static classfit.example.classfit.common.exception.ClassfitException.INVALID_MEMBER_ACADEMY;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.util.EmailUtil;
import classfit.example.classfit.common.util.RedisUtil;
import classfit.example.classfit.mail.dto.request.EmailAuthPurpose;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.dto.request.MemberPasswordRequest;
import classfit.example.classfit.member.dto.request.MemberRequest;
import classfit.example.classfit.member.dto.response.AcademyMemberResponse;
import classfit.example.classfit.member.dto.response.MemberResponse;
import classfit.example.classfit.member.repository.MemberRepository;
import classfit.example.classfit.memberCalendar.service.MemberCalendarService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberCalendarService memberCalendarService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisUtil redisUtil;

    @Transactional
    public MemberResponse signIn(MemberRequest request) {

        String emailToken = redisUtil.getData(EmailAuthPurpose.SIGN_IN + ":" + EmailUtil.splitEmail(request.email()) + ":token");

        if (!emailToken.equals(request.emailToken())) {
            throw new ClassfitException("이메일 검증에 문제가 발생하였습니다. 이메일 인증을 다시 시도해 주세요", HttpStatus.NOT_FOUND);
        }

        if (memberRepository.existsByEmail(request.email())) {
            throw new ClassfitException("이미 사용 중인 이메일(ID) 입니다.", HttpStatus.CONFLICT);
        }

        if (!request.password().equals(request.passwordConfirm())) {
            throw new ClassfitException("비밀번호가 일치하지 않습니다.", HttpStatus.CONFLICT);
        }

        Member member = request.toEntity(bCryptPasswordEncoder);

        Member savedMember = memberRepository.save(member);
        createDefaultCalendars(savedMember);

        return MemberResponse.from(member);
    }

    private void createDefaultCalendars(Member member) {
        memberCalendarService.createPersonalCalendar(member);
        memberCalendarService.createSharedCalendar(member);
    }

    @Transactional
    public void updatePassword(MemberPasswordRequest request) {
        String emailToken = redisUtil.getData(EmailAuthPurpose.PASSWORD_RESET + ":" + EmailUtil.splitEmail(request.email()) + ":token");

        if (!emailToken.equals(request.emailToken())) {
            throw new ClassfitException("이메일 검증에 문제가 발생하였습니다. 이메일 인증을 다시 시도해 주세요", HttpStatus.NOT_FOUND);
        }

        if (!request.password().equals(request.passwordConfirm())) {
            throw new ClassfitException("비밀번호가 일치하지 않습니다.", HttpStatus.CONFLICT);
        }

        Member findMember = memberRepository.findByEmail(request.email()).orElseThrow(() -> new ClassfitException("존재하지 않는 계정입니다.", HttpStatus.NOT_FOUND));

        findMember.updatePassword(bCryptPasswordEncoder.encode(request.password()));
    }

    public List<AcademyMemberResponse> getMembersByLoggedInMemberAcademy(Member loggedInMember) {
        if (hasAcademy(loggedInMember)) {
            Long academyId = loggedInMember.getAcademy().getId();
            List<Member> academyMembers = getAcademyMembers(academyId);
            return mapToMemberResponse(academyMembers);
        }
        return new ArrayList<>();
    }

    private boolean hasAcademy(Member loggedInMember) {
        if (loggedInMember.getAcademy() == null) {
            throw new IllegalArgumentException(INVALID_MEMBER_ACADEMY);
        }
        return true;
    }

    private List<Member> getAcademyMembers(Long academyId) {
        return memberRepository.findByAcademyId(academyId)
            .orElseThrow(() -> new ClassfitException(ACADEMY_MEMBERS_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private List<AcademyMemberResponse> mapToMemberResponse(List<Member> members) {
        return members.stream()
            .map(AcademyMemberResponse::from)
            .collect(Collectors.toList());
    }
}
