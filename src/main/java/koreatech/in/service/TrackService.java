package koreatech.in.service;


import koreatech.in.domain.Homepage.TechStack;
import koreatech.in.domain.Homepage.Track;

import java.util.List;
import java.util.Map;

public interface TrackService {
    List<Track> getTracks() throws Exception;

    Map<String, Object> getTrackInfo(Integer trackId) throws Exception;

    // ===== ADMIN APIs =====
    List<Track> getTracksForAdmin() throws Exception;

    Map<String, Object> getTrackInfoForAdmin(int id) throws Exception;

    Track createTrackForAdmin(Track track) throws Exception;

    Track updateTrackForAdmin(Track track, int id) throws Exception;

    Map<String, Object> deleteTrackForAdmin(int id) throws Exception;

    TechStack createTechStackForAdmin(TechStack techStack, String trackName) throws Exception;

    TechStack updateTechStackForAdmin(TechStack techStack, String trackName, int id) throws Exception;

    Map<String, Object> deleteTechStackForAdmin(int id) throws Exception;
}
