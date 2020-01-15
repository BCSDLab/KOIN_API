package koreatech.in.service;

import koreatech.in.domain.Version.Version;

import java.util.List;
import java.util.Map;

public interface VersionService {
    Version getVersion(String type) throws Exception;

    List<Version> getVersionsForAdmin() throws Exception;

    Version createVersionForAdmin(Version version) throws Exception;

    Version updateVersionForAdmin(Version version, String type) throws Exception;

    Map<String, Object> deleteVersionForAdmin(String type) throws Exception;
}
