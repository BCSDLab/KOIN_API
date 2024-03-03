package koreatech.in.service;

import koreatech.in.domain.Homepage.Member;
import koreatech.in.dto.admin.member.request.CreateMemberRequest;
import koreatech.in.dto.admin.member.request.MembersCondition;
import koreatech.in.dto.admin.member.request.UpdateMemberRequest;
import koreatech.in.dto.admin.member.response.MemberResponse;
import koreatech.in.dto.admin.member.response.MembersResponse;

import java.util.List;

public interface MemberService {
    void createMemberForAdmin(CreateMemberRequest request) throws Exception;

    MemberResponse getMemberForAdmin(Integer memberId) throws Exception;

    MembersResponse getMembersForAdmin(MembersCondition condition) throws Exception;

    void updateMemberForAdmin(Integer memberId, UpdateMemberRequest request) throws Exception;

    void deleteMemberForAdmin(Integer memberId) throws Exception;

    void undeleteMemberForAdmin(Integer memberId) throws Exception;

    List<Member> getMembers() throws Exception;

    Member getMember(Integer memberId) throws Exception;
}
