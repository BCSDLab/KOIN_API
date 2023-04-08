package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Version.Version;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.repository.VersionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("versionService")
public class VersionServiceImpl implements VersionService{
    @Resource(name="versionMapper")
    private VersionMapper versionMapper;

    @Autowired
    JwtValidator jwtValidator;

    @Override
    public Version getVersion(String type) {
        return Optional.ofNullable(versionMapper.getVersion(type))
                .orElseThrow(() -> new BaseException(ExceptionInformation.VERSION_NOT_FOUND));
    }

    @Override
    public List<Version> getVersionsForAdmin() throws Exception {
        return versionMapper.getVersionsForAdmin();
    }

    @Transactional
    @Override
    public Version createVersionForAdmin(Version version) throws Exception {
        Version check = versionMapper.getVersion(version.getType());

        if (check != null)
            throw new ConflictException(new ErrorMessage("There is already same type", 0));

        versionMapper.createVersionForAdmin(version);

        return version;
    }

    @Transactional
    @Override
    public Version updateVersionForAdmin(Version version, String type) throws Exception {
        Version version_old = versionMapper.getVersion(type);
        if (version_old == null) {
            throw new NotFoundException(new ErrorMessage("There is no version", 0));
        }

        if (version_old.getVersion().equals(version.getVersion()))
            throw new ConflictException(new ErrorMessage("Type has already same version", 0));

        version_old.update(version);
        versionMapper.updateVersionForAdmin(version_old);
        return version_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteVersionForAdmin(String type) throws Exception {
        Version version = versionMapper.getVersion(type);
        if (version == null)
            throw new NotFoundException(new ErrorMessage("There is no version", 0));

        versionMapper.deleteVersionForAdmin(type);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }
}
