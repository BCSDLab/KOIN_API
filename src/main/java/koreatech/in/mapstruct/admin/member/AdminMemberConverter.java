package koreatech.in.mapstruct.admin.member;

import koreatech.in.domain.Homepage.Member;
import koreatech.in.dto.admin.member.request.CreateMemberRequest;
import koreatech.in.dto.admin.member.response.MemberResponse;
import koreatech.in.dto.admin.member.response.MembersResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMemberConverter {
    AdminMemberConverter INSTANCE = Mappers.getMapper(AdminMemberConverter.class);

    Member toMember(CreateMemberRequest request);

    MemberResponse toMemberResponse(Member member);

    MembersResponse.Member toMembersResponse$Member(Member member);
}
