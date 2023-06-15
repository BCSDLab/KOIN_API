package koreatech.in.mapstruct.admin.user;

import koreatech.in.domain.User.PageInfo;
import koreatech.in.dto.admin.user.request.NewOwnersCondition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    @Mappings({
            @Mapping(source = "source.page", target = "currentPage"),
            @Mapping(source = "source.limit", target = "limit"),
            @Mapping(source = "totalCount", target = "totalCount")
    })
    PageInfo toPageInfo(NewOwnersCondition source, Integer totalCount);
}
