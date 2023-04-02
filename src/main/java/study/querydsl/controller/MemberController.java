package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberJpaRepository;
import study.querydsl.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberJpaRepository memberJpaRepository;

    private final MemberRepository memberRepository;

    @GetMapping("v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition) {
        return memberJpaRepository.search(condition);
        //http://localhost:8080/v1/members?teamName=teamB&ageGoe=31&ageLoe=35&username=member33
    }

    @GetMapping("v2/members")
    public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchPageSimple(condition,pageable);
        //http://localhost:8080/v2/members?page=0&size=5
    }

    @GetMapping("v3/members")
    public Page<MemberTeamDto> searchMemberV3(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchPageComplex(condition,pageable);
        //http://localhost:8080/v3/members?page=1&size=5
        //http://localhost:8080/v3/members?page=0&size=200 size 파라미터를 데이터보다 많게 할 경우 카운트쿼리가 나갈필요가 없기에 안나감
    }

}
