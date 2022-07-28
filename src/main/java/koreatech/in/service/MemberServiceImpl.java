package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Homepage.Member;
import koreatech.in.domain.Homepage.Track;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.MemberMapper;
import koreatech.in.repository.TrackMapper;
import koreatech.in.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public List<Member> getMembersForAdmin() throws Exception {
        List<Member> members = memberMapper.getMembersForAdmin();

        if(members == null) {
            throw new NotFoundException(new ErrorMessage("Members not found.", 0));
        }

        return members;
    }

    @Override
    public Member getMemberForAdmin(int id) throws Exception {
        Member member = memberMapper.getMemberForAdmin(id);

        if(member == null) {
            throw new NotFoundException(new ErrorMessage("Member not found.", 0));
        }

        return member;
    }

    @Override
    public Member createMemberForAdmin(Member member) throws Exception {
        Track selectTrack = trackMapper.getTrackByNameForAdmin(member.getTrack());
        if(selectTrack == null) {
            throw new NotFoundException(new ErrorMessage("Track name not found.", 0));
        }

        if(member.getIs_deleted() == null)
            member.setIs_deleted(false);

        memberMapper.createMemberForAdmin(member);

        return member;
    }

    @Override
    public Member updateMemberForAdmin(Member member, int id) throws Exception {
        Member member_old = memberMapper.getMemberForAdmin(id);
        if(member_old == null) {
            throw new NotFoundException(new ErrorMessage("Member not found.", 0));
        }

        if(member.getTrack() != null) {
            Track selectTrack = trackMapper.getTrackByNameForAdmin(member.getTrack());
            if(selectTrack == null) {
                throw new NotFoundException(new ErrorMessage("Track name not found.", 0));
            }
        }

        member_old.update(member);
        memberMapper.updateMemberForAdmin(member_old);
        return member_old;
    }

    @Override
    public Map<String, Object> deleteMemberForAdmin(int id) throws Exception {
        Member selectMember = memberMapper.getMemberForAdmin(id);
        if(selectMember == null) {
            throw new NotFoundException(new ErrorMessage("Member not found.", 0));
        }

        memberMapper.deleteMemberForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public String uploadImage(MultipartFile multipartFile, Integer flag) throws Exception{
        if ( multipartFile == null){
            throw new Exception();
        }

        if ( flag == null){
            throw new Exception();
        }
        String directory = "bcsdlab_page_assets/" + "img/" + "people/";
        switch (flag){
            case 1:
                directory += "android";
                break;
            case 2:
                directory +="backend";
                break;
            case 3:
                directory +="frontend";
                break;
            case 4:
                directory += "game";
                break;
            case 5:
                directory += "HR";
                break;
            case 6:
                directory += "P&M";
                break;
            case 7:
                directory += "uiux";
                break;
            default:
                throw new Exception();
        }
        String url = uploadFileUtils.uploadFile(directory,multipartFile.getOriginalFilename(), multipartFile.getBytes(), multipartFile);


        return "https://static.koreatech.in/" + directory + url;
    }

}
