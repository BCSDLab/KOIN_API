package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Homepage.Member;
import koreatech.in.domain.Homepage.Track;
import koreatech.in.dto.member.admin.request.CreateMemberRequest;
import koreatech.in.dto.member.admin.request.MembersCondition;
import koreatech.in.dto.member.admin.request.UpdateMemberRequest;
import koreatech.in.dto.member.admin.response.MemberResponse;
import koreatech.in.dto.member.admin.response.MembersResponse;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.MemberMapper;
import koreatech.in.repository.TrackMapper;
import koreatech.in.util.UploadFileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "memberService")
public class MemberServiceImpl implements MemberService{

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
            throw new PreconditionFailedException(new ErrorMessage("공백으로는 검색할 수 없습니다.", 0));
        }

        Integer totalCount = memberMapper.getTotalCountByConditionForAdmin(condition);
        Integer totalPage = condition.extractTotalPage(totalCount);
        Integer currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new NotFoundException(new ErrorMessage("유효하지 않은 페이지입니다.", 0));
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
            throw new NotFoundException(new ErrorMessage("Member not found.", 0));
        }

        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .student_number(member.getStudent_number())
                .track(member.getTrack())
                .position(member.getPosition())
                .email(member.getEmail())
                .image_url(member.getImage_url())
                .is_deleted(member.getIs_deleted())
                .build();
    }

    @Override
    public Map<String, Object> createMemberForAdmin(CreateMemberRequest request) throws Exception {
        Track track = trackMapper.getTrackByNameForAdmin(request.getTrack());

        if (track == null) {
            throw new NotFoundException(new ErrorMessage("Track name not found.", 0));
        }

        Member member = new Member(request);
        memberMapper.createMemberForAdmin(member);

        return new HashMap<String, Object>() {{
            put("success", true);
            put("id", member.getId());
        }};
    }

    @Override
    public Map<String, Object> updateMemberForAdmin(int id, UpdateMemberRequest request) throws Exception {
        Member existingMember = memberMapper.getMemberForAdmin(id);

        if (existingMember == null) {
            throw new NotFoundException(new ErrorMessage("Member not found.", 0));
        }

        Track track = trackMapper.getTrackByNameForAdmin(request.getTrack());
        if (track == null) {
            throw new NotFoundException(new ErrorMessage("Track name not found.", 0));
        }

        existingMember.update(request);
        memberMapper.updateMemberForAdmin(existingMember);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Map<String, Object> deleteMemberForAdmin(int id) throws Exception {
        Member selectMember = memberMapper.getMemberForAdmin(id);

        if (selectMember == null) {
            throw new NotFoundException(new ErrorMessage("Member not found.", 0));
        }

        if (selectMember.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage("It has already been soft deleted.", 1));
        }

        memberMapper.softDeleteMemberForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Map<String, Object> undeleteMemberForAdmin(int id) throws Exception {
        Member selectMember = memberMapper.getMemberForAdmin(id);

        if (selectMember == null) {
            throw new NotFoundException(new ErrorMessage("Member not found.", 0));
        }

        if (!selectMember.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage("it is not soft deleted.", 1));
        }

        memberMapper.undeleteMemberForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Map<String, Object> uploadImage(MultipartFile image) throws Exception {
        if (image == null) {
            throw new PreconditionFailedException(new ErrorMessage("업로드할 파일이 없습니다.", 0));
        }

        String directory = "bcsdlab_page_assets/img/people";

        String url = uploadFileUtils.uploadFile(directory, image.getOriginalFilename(), image.getBytes(), image);
        String imageUrl = "https://" + uploadFileUtils.getDomain() + "/" + directory + url;

        return new HashMap<String, Object>() {{
            put("success", true);
            put("image_url", imageUrl);
        }};
    }
}
