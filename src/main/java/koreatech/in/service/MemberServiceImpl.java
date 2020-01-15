package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Homepage.Member;
import koreatech.in.domain.Homepage.Track;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.MemberMapper;
import koreatech.in.repository.TrackMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "memberService")
public class MemberServiceImpl implements MemberService{

    @Resource(name = "memberMapper")
    private MemberMapper memberMapper;

    @Resource(name = "trackMapper")
    private TrackMapper trackMapper;

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
}
