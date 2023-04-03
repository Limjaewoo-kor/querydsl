package study.querydsl.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
@RequiredArgsConstructor
class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void basicTest() {
        Member member = new Member("name1",10);
        memberRepository.save(member);

        Member byId = memberRepository.findById(member.getId()).get();
        assertThat(byId).isEqualTo(member);

        List<Member> all = memberRepository.findAll();
        assertThat(all).containsExactly(member);

        List<Member> name1 = memberRepository.findByUsername("name1");
        assertThat(name1).containsExactly(member);

        List<Member> all2 = memberRepository.findAll();
        assertThat(all2).containsExactly(member);

        List<Member> name2 = memberRepository.findByUsername("name1");
        assertThat(name2).containsExactly(member);
    }

    @Test
    public void searchTest() {

        Team team = new Team("teamA");
        em.persist(team);
        Member member = new Member("memberA",15,team);
        em.persist(member);

        MemberSearchCondition condition =
                new MemberSearchCondition("memberA","teamA",10,20);


        List<MemberTeamDto> memberTeamDtos = memberRepository.search(condition);
        MemberTeamDto memberTeamDto = memberTeamDtos.get(0);

        assertThat(memberTeamDto.getMemberId()).isEqualTo(member.getId());
        assertThat(memberTeamDto.getUsername()).isEqualTo(member.getUsername());
        assertThat(memberTeamDto.getAge()).isEqualTo(member.getAge());
        assertThat(memberTeamDto.getTeamId()).isEqualTo(member.getTeam().getId());
        assertThat(memberTeamDto.getTeamName()).isEqualTo(member.getTeam().getName());

    }


    @Test
    public void searchPageSimple() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",20,teamA);
        Member member3 = new Member("member3",30,teamB);
        Member member4 = new Member("member4",40,teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageRequest);

        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).extracting("username").containsExactly("member1","member2","member3");

    }

    @Test
    public void querydslPredicateExecutorTest() {

        //querydslPredicateExecutor 이라는 쿼리를 간단하게 짜주는 기능이 있으나, 한계점이 명확함, 조인이 불가하다
        //조인X (묵시적 조인은 가능하지만 left join이 불가능하다.)
        //클라이언트가 Querydsl에 의존해야 한다. 서비스 클래스가 Querydsl이라는 구현 기술에 의존해야 한다. 복잡한 실무환경에서 사용하기에는 한계가 명확하다.


        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",20,teamA);
        Member member3 = new Member("member3",30,teamB);
        Member member4 = new Member("member4",40,teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);


        Iterable<Member> result = memberRepository.findAll(
                        member.age.between(20, 40)
                        .and(member.username.eq("member2")));
        for (Member findMember : result) {
            System.out.println("findMember = " + findMember);
        }
    }

    //추가 사항

    // 1.Querydsl Web
    //Querydsl Web 지원
    //공식 URL: https://docs.spring.io/spring-data/jpa/docs/2.2.3.RELEASE/reference/html/
    //#core.web.type-safe
    //한계점
    //단순한 조건만 가능
    //조건을 커스텀하는 기능이 복잡하고 명시적이지 않음 컨트롤러가 Querydsl에 의존
    //복잡한 실무환경에서 사용하기에는 한계가 명확


    // 2.QuerydslRepositorySupport
    //리포지토리 지원 -  QuerydslRepositorySupport

    //장점
    //getQuerydsl().applyPagination() 스프링 데이터가 제공하는 페이징을 Querydsl로 편리하게 변환 가능(단! Sort는 오류발생)
    //from() 으로 시작 가능(최근에는 QueryFactory를 사용해서 select() 로 시작하는 것이 더 명시적) EntityManager 제공

    //한계
    //Querydsl 3.x 버전을 대상으로 만듬
    //Querydsl 4.x에 나온 JPAQueryFactory로 시작할 수 없음
    //select로 시작할 수 없음 (from으로 시작해야함) QueryFactory 를 제공하지 않음
    //스프링 데이터 Sort 기능이 정상 동작하지 않음



}