package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Homepage.Member;
import koreatech.in.domain.Homepage.TechStack;
import koreatech.in.domain.Homepage.Track;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.repository.MemberMapper;
import koreatech.in.repository.TrackMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static koreatech.in.domain.DomainToMap.domainToMap;

@Service(value = "trackService")
public class TrackServiceImpl implements TrackService{
    @Resource(name = "trackMapper")
    private TrackMapper trackMapper;

    @Resource(name = "memberMapper")
    private MemberMapper memberMapper;

    @Override
    public List<Track> getTracks() throws Exception {
        List<Track> tracks = trackMapper.getTracks();

        if(tracks == null) {
            throw new NotFoundException(new ErrorMessage("Tracks not found.", 0));
        }

        return tracks;
    }

    @Override
    public Map<String, Object> getTrackInfo(Integer trackId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> members = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> techStacks = new ArrayList<Map<String, Object>>();

        Track track = trackMapper.getTrack(trackId);
        if(track == null || track.getName().isEmpty()) {
            throw new NotFoundException(new ErrorMessage("There's no such track", 0));
        }

        List<Member> memberList = memberMapper.getTrackMembers(trackId);
        List<TechStack> techStackList = trackMapper.getTrackTechStacks(trackId);
        if(memberList == null) { memberList = new ArrayList<Member>(); }
        if(techStackList == null) { techStackList = new ArrayList<TechStack>(); }

        for(Member member : memberList) {
            members.add(domainToMap(member));
        }
        for(TechStack techStack : techStackList) {
            techStacks.add(domainToMap(techStack));
        }

        map.put("TrackName", track.getName());
        map.put("Members", members);
        map.put("TechStacks", techStacks);

        return map;
    }

    // ===== ADMIN APIs =====
    @Override
    public List<Track> getTracksForAdmin() throws Exception {
        List<Track> tracks = trackMapper.getTracksForAdmin();

        if(tracks == null) {
            throw new NotFoundException(new ErrorMessage("Tracks not found.", 0));
        }

        return tracks;
    }

    @Override
    public Map<String, Object> getTrackInfoForAdmin(int id) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> members = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> techStacks = new ArrayList<Map<String, Object>>();

        Track track = trackMapper.getTrackForAdmin(id);
        if(track == null) {
            throw new NotFoundException(new ErrorMessage("There's no such track", 0));
        }

        List<Member> memberList = memberMapper.getTrackMembersForAdmin(id);
        List<TechStack> techStackList = trackMapper.getTrackTechStacksForAdmin(id);
        if(memberList == null) { memberList = new ArrayList<Member>(); }
        if(techStackList == null) { techStackList = new ArrayList<TechStack>(); }

        for(Member member : memberList) {
            members.add(domainToMap(member));
        }
        for(TechStack techStack : techStackList) {
            techStacks.add(domainToMap(techStack));
        }

        map.put("TrackName", track.getName());
        map.put("Members", members);
        map.put("TechStacks", techStacks);

        return map;
    }

    @Override
    public Track createTrackForAdmin(Track track) throws Exception {
        Track selectTrack = trackMapper.getTrackByNameForAdmin(track.getName());
        if(selectTrack != null) {
            throw new ConflictException(new ErrorMessage("Track name already exists", 0));
        }

        if(track.getIs_deleted() == null)
            track.setIs_deleted(false);

        trackMapper.createTrackForAdmin(track);

        return track;
    }

    @Override
    public Track updateTrackForAdmin(Track track, int id) throws Exception {
        Track track_old = trackMapper.getTrackForAdmin(id);
        if(track_old == null) {
            throw new NotFoundException(new ErrorMessage("There's no such track", 0));
        }

        if(track.getName() != null) {
            Track selectTrack = trackMapper.getTrackByNameForAdmin(track.getName());
            if(selectTrack != null) {
                throw new ConflictException(new ErrorMessage("Track name already exists", 0));
            }
        }

        track_old.update(track);
        trackMapper.updateTrackForAdmin(track_old);
        return track_old;
    }

    @Override
    public Map<String, Object> deleteTrackForAdmin(int id) throws Exception {
        Track selectTrack = trackMapper.getTrackForAdmin(id);
        if(selectTrack == null) {
            throw new NotFoundException(new ErrorMessage("There's no such track", 0));
        }

        trackMapper.deleteTrackForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public TechStack createTechStackForAdmin(TechStack techStack, String trackName) throws Exception {
        Track selectTrack = trackMapper.getTrackByNameForAdmin(trackName);
        if(selectTrack == null) {
            throw new ConflictException(new ErrorMessage("Track name does not exists", 0));
        }

        if(techStack.getIs_deleted() == null)
            techStack.setIs_deleted(false);

        techStack.setTrack_id(selectTrack.getId());
        trackMapper.createTechStackForAdmin(techStack);

        return techStack;
    }

    @Override
    public TechStack updateTechStackForAdmin(TechStack techStack, String trackName, int id) throws Exception {
        TechStack techStack_old = trackMapper.getTechStackForAdmin(id);
        if(techStack_old == null) {
            throw new NotFoundException(new ErrorMessage("TechStack not found.", 0));
        }

        if(trackName != null) {
            Track selectTrack = trackMapper.getTrackByNameForAdmin(trackName);
            if(selectTrack == null)
                throw new NotFoundException(new ErrorMessage("Track name not found.", 0));
            techStack.setTrack_id(selectTrack.getId());
        }
        techStack_old.update(techStack);
        trackMapper.updateTechStackForAdmin(techStack_old);
        return techStack_old;
    }

    @Override
    public Map<String, Object> deleteTechStackForAdmin(int id) throws Exception {
        TechStack selectTechStack = trackMapper.getTechStackForAdmin(id);
        if(selectTechStack == null) {
            throw new NotFoundException(new ErrorMessage("TechStack not found.", 0));
        }

        trackMapper.deleteTechStackForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }
}
