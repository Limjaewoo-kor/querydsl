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
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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



}