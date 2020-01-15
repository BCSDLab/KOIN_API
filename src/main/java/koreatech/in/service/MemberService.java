package koreatech.in.service;

import koreatech.in.domain.Homepage.Member;

import java.util.List;
import java.util.Map;

public interface MemberService {
    List<Member> getMembers() throws Exception;

    Member getMemberById(Integer memberId) throws Exception;

    // ===== ADMIN APIs =====
    List<Member> getMembersForAdmin() throws Exception;

    Member getMemberForAdmin(int id) throws Exception;

    Member createMemberForAdmin(Member member) throws Exception;

    Member updateMemberForAdmin(Member member, int id) throws Exception;

    Map<String, Object> deleteMemberForAdmin(int id) throws Exception;
}
