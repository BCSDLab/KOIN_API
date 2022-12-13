package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Homepage.Member;
import koreatech.in.domain.Homepage.Track;
import koreatech.in.dto.SuccessCreateResponse;
import koreatech.in.dto.SuccessResponse;
import koreatech.in.dto.UploadImageResponse;
import koreatech.in.dto.member.admin.request.CreateMemberRequest;
import koreatech.in.dto.member.admin.request.MembersCondition;
import koreatech.in.dto.member.admin.request.UpdateMemberRequest;
import koreatech.in.dto.member.admin.response.MemberResponse;
import koreatech.in.dto.member.admin.response.MembersResponse;
import koreatech.in.exception.BadRequestException;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.repository.MemberMapper;
import koreatech.in.repository.TrackMapper;
import koreatech.in.util.UploadFileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.List;

import static koreatech.in.util.ExceptionMessage.*;

@Service(value = "memberService")
public class MemberServiceImpl implements MemberService {

    @Resource(name = "memberMapper")
    private MemberMapper memberMapper;

    @Resource(name = "trackMapper")
    private TrackMapper trackMapper;

    @Inject
    UploadFileUtils uploadFileUtils;

    @Override
    public List<Member> getMembers() throws Exception {
        List<Member> members = memberMapper.getMembers();

        if(members == null) {
            throw new NotFoundException(new ErrorMessage("Members not found.", 0));
        }

        return members;
    }

    @Override
    public Member getMemberById(Integer memberId) throws Exception {
        Member member = memberMapper.getMemberById(memberId);

        if(member == null) {
            throw new NotFoundException(new ErrorMessage("Member not found.", 0));
        }

        return member;
    }

    // ===== ADMIN APIs =====
    @Override
    public MembersResponse getMembersForAdmin(MembersCondition condition) throws Exception {
        if (condition.getQuery() != null && !StringUtils.hasText(condition.getQuery())) {
            throw new ConflictException(new ErrorMessage(SEARCH_QUERY_MUST_NOT_BE_BLANK));
        }

        Integer totalCount = memberMapper.getTotalCountByConditionForAdmin(condition);
        Integer totalPage = condition.extractTotalPage(totalCount);
        Integer currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new NotFoundException(new ErrorMessage(PAGE_NOT_FOUND));
        }

        List<MembersResponse.Member> members = memberMapper.getMembersByConditionForAdmin(condition.getCursor(), condition);

        return MembersResponse.builder()
                .total_count(totalCount)
                .total_page(totalPage)
                .current_count(members.size())
                .current_page(currentPage)
                .members(members)
                .build();
    }

    @Override
    public MemberResponse getMemberForAdmin(int id) throws Exception {
        Member member = memberMapper.getMemberForAdmin(id);

        if (member == null) {
            throw new NotFoundException(new ErrorMessage(MEMBER_NOT_FOUND));
        }

        return MemberResponse.builder()
                .member(MemberResponse.Member.builder()
                        .id(member.getId())
                        .name(member.getName())
                        .student_number(member.getStudent_number())
                        .track(member.getTrack())
                        .position(member.getPosition())
                        .email(member.getEmail())
                        .image_url(member.getImage_url())
                        .is_deleted(member.getIs_deleted())
                        .build()
                ).build();
    }

    @Override
    public SuccessCreateResponse createMemberForAdmin(CreateMemberRequest request) throws Exception {
        Track track = trackMapper.getTrackByNameForAdmin(request.getTrack());

        if (track == null) {
            throw new NotFoundException(new ErrorMessage(TRACK_NOT_FOUND));
        }

        Member member = new Member(request);
        memberMapper.createMemberForAdmin(member);

        return SuccessCreateResponse.builder()
                .id(member.getId())
                .build();
    }

    @Override
    public SuccessResponse updateMemberForAdmin(int id, UpdateMemberRequest request) throws Exception {
        Member existingMember = memberMapper.getMemberForAdmin(id);

        if (existingMember == null) {
            throw new NotFoundException(new ErrorMessage(MEMBER_NOT_FOUND));
        }

        Track track = trackMapper.getTrackByNameForAdmin(request.getTrack());
        if (track == null) {
            throw new NotFoundException(new ErrorMessage(TRACK_NOT_FOUND));
        }

        existingMember.update(request);
        memberMapper.updateMemberForAdmin(existingMember);

        return new SuccessResponse();
    }

    @Override
    public SuccessResponse deleteMemberForAdmin(int id) throws Exception {
        Member selectMember = memberMapper.getMemberForAdmin(id);

        if (selectMember == null) {
            throw new NotFoundException(new ErrorMessage(MEMBER_NOT_FOUND));
        }

        if (selectMember.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage(MEMBER_ALREADY_DELETED));
        }

        memberMapper.softDeleteMemberForAdmin(id);

        return new SuccessResponse();
    }

    @Override
    public SuccessResponse undeleteMemberForAdmin(int id) throws Exception {
        Member selectMember = memberMapper.getMemberForAdmin(id);

        if (selectMember == null) {
            throw new NotFoundException(new ErrorMessage(MEMBER_NOT_FOUND));
        }

        if (!selectMember.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage(MEMBER_NOT_DELETED));
        }

        memberMapper.undeleteMemberForAdmin(id);

        return new SuccessResponse();
    }

    @Override
    public UploadImageResponse uploadImage(MultipartFile image) throws Exception {
        if (image == null) {
            throw new BadRequestException(new ErrorMessage(FILE_TO_UPLOAD_NOT_EXIST));
        }

        String directory = "bcsdlab_page_assets/img/people";

        String url = uploadFileUtils.uploadFile(directory, image.getOriginalFilename(), image.getBytes(), image);
        String imageUrl = "https://" + uploadFileUtils.getDomain() + "/" + directory + url;

        return UploadImageResponse.builder()
                .image_url(imageUrl)
                .build();
    }
}
