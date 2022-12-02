package koreatech.in.service;

import koreatech.in.domain.Homepage.Member;
import koreatech.in.dto.member.admin.request.CreateMemberRequest;
import koreatech.in.dto.member.admin.request.MembersCondition;
import koreatech.in.dto.member.admin.request.UpdateMemberRequest;
import koreatech.in.dto.member.admin.response.MemberResponse;
import koreatech.in.dto.member.admin.response.MembersResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface MemberService {
    List<Member> getMembers() throws Exception;

    Member getMemberById(Integer memberId) throws Exception;

    // ===== ADMIN APIs =====
    MembersResponse getMembersForAdmin(MembersCondition condition) throws Exception;

    MemberResponse getMemberForAdmin(int id) throws Exception;

    Map<String, Object> createMemberForAdmin(CreateMemberRequest request) throws Exception;

    Map<String, Object> updateMemberForAdmin(int id, UpdateMemberRequest request) throws Exception;

    Map<String, Object> deleteMemberForAdmin(int id) throws Exception;

    Map<String, Object> undeleteMemberForAdmin(int id) throws Exception;

    String uploadImage(MultipartFile multipartFile, Integer flag) throws Exception;
}
