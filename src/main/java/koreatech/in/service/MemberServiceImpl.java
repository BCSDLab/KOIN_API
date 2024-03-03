package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Homepage.Member;
import koreatech.in.domain.Homepage.Track;
import koreatech.in.dto.admin.member.request.CreateMemberRequest;
import koreatech.in.dto.admin.member.request.MembersCondition;
import koreatech.in.dto.admin.member.request.UpdateMemberRequest;
import koreatech.in.dto.admin.member.response.MemberResponse;
import koreatech.in.dto.admin.member.response.MembersResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.mapstruct.admin.member.AdminMemberConverter;
import koreatech.in.repository.MemberMapper;
import koreatech.in.repository.TrackMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static koreatech.in.exception.ExceptionInformation.*;

@Service(value = "memberService")
@Transactional
public class MemberServiceImpl implements MemberService {
    @Resource(name = "memberMapper")
    private MemberMapper memberMapper;

    @Resource(name = "trackMapper")
    private TrackMapper trackMapper;

    @Override
    public void createMemberForAdmin(CreateMemberRequest request) throws Exception {
        getTrackByName(request.getTrack()); // 트랙 존재 여부 확인

        Member member = AdminMemberConverter.INSTANCE.toMember(request);
        memberMapper.createMemberForAdmin(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberForAdmin(Integer memberId) throws Exception {
        Member member = getMemberById(memberId);

        return AdminMemberConverter.INSTANCE.toMemberResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MembersResponse getMembersForAdmin(MembersCondition condition) throws Exception {
        Integer totalCount = memberMapper.getTotalCountByConditionForAdmin(condition);
        Integer totalPage = condition.extractTotalPage(totalCount);
        Integer currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new BaseException(PAGE_NOT_FOUND);
        }

        List<Member> members = memberMapper.getMembersByConditionForAdmin(condition.getCursor(), condition);

        return MembersResponse.of(totalCount, totalPage, currentPage, members);
    }

    @Override
    public void updateMemberForAdmin(Integer memberId, UpdateMemberRequest request) throws Exception {
        Member existingMember = getMemberById(memberId);

        getTrackByName(request.getTrack()); // 트랙 존재 여부 확인

        if (existingMember.needToUpdate(request)) {
            existingMember.update(request);
            memberMapper.updateMemberForAdmin(existingMember);
        }
    }

    @Override
    public void deleteMemberForAdmin(Integer memberId) throws Exception {
        Member member = getMemberById(memberId);

        if (member.isSoftDeleted()) {
            throw new BaseException(MEMBER_ALREADY_DELETED);
        }

        memberMapper.deleteMemberByIdForAdmin(memberId);
    }

    @Override
    public void undeleteMemberForAdmin(Integer memberId) throws Exception {
        Member member = getMemberById(memberId);

        if (!member.isSoftDeleted()) {
            throw new BaseException(MEMBER_NOT_DELETED);
        }

        memberMapper.undeleteMemberByIdForAdmin(memberId);
    }


    @Override
    public List<Member> getMembers() throws Exception {
        List<Member> members = memberMapper.getMembers();

        if(members == null) {
            throw new NotFoundException(new ErrorMessage("Members not found.", 0));
        }

        return members;
    }

    @Override
    public Member getMember(Integer memberId) throws Exception {
        Member member = memberMapper.getMemberById(memberId);

        if(member == null) {
            throw new NotFoundException(new ErrorMessage("Member not found.", 0));
        }

        return member;
    }

    private Member getMemberById(Integer memberId) throws Exception {
        return Optional.ofNullable(memberMapper.getMemberByIdForAdmin(memberId))
                .orElseThrow(() -> new BaseException(MEMBER_NOT_FOUND));
    }

    private Track getTrackByName(String name) throws Exception {
        return Optional.ofNullable(trackMapper.getTrackByNameForAdmin(name))
                .orElseThrow(() -> new BaseException(TRACK_NOT_FOUND));
    }
}
