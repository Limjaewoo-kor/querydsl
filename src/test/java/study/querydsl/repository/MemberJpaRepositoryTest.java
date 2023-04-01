package study.querydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest() {
        Member member = new Member("name1",10);
        memberJpaRepository.save(member);

        Member byId = memberJpaRepository.findById(member.getId()).get();
        assertThat(byId).isEqualTo(member);

        List<Member> all = memberJpaRepository.findAll();
        assertThat(all).containsExactly(member);

        List<Member> name1 = memberJpaRepository.findByUsername("name1");
        assertThat(name1).containsExactly(member);

        List<Member> all2 = memberJpaRepository.findAll_Querydsl();
        assertThat(all2).containsExactly(member);

        List<Member> name2 = memberJpaRepository.findByUsername_Querydsl("name1");
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

//
//        MemberSearchCondition condition =
//                new MemberSearchCondition("memberA");

        List<MemberTeamDto> memberTeamDtos = memberJpaRepository.search(condition);
        MemberTeamDto memberTeamDto = memberTeamDtos.get(0);

        assertThat(memberTeamDto.getMemberId()).isEqualTo(member.getId());
        assertThat(memberTeamDto.getUsername()).isEqualTo(member.getUsername());
        assertThat(memberTeamDto.getAge()).isEqualTo(member.getAge());
        assertThat(memberTeamDto.getTeamId()).isEqualTo(member.getTeam().getId());
        assertThat(memberTeamDto.getTeamName()).isEqualTo(member.getTeam().getName());

    }



}