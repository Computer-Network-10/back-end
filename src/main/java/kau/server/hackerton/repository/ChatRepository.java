package kau.server.hackerton.repository;


import jakarta.persistence.EntityManager;
import kau.server.hackerton.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChatRepository{

    private final EntityManager em;

    public void addMember(Member member){
        em.persist(member);
    }

    public Optional<Member> findMemberByName(String name) {
        return Optional.of(em.createQuery("select m from Member m where m.nickname=:name", Member.class)
                .setMaxResults(1)
                .setParameter("name", name)
                .getSingleResult());
    }

    public Optional<List<Member>> getMemberList() {
        return Optional.of(em.createQuery("select m from Member m").getResultList());
    }

    public void deleteMember(Member member){
        em.remove(member);
    }
}
