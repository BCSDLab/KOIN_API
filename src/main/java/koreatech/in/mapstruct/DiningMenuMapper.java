package koreatech.in.mapstruct;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Date;
import java.util.List;
import koreatech.in.domain.Dining.DiningMenu;
import koreatech.in.domain.Dining.DiningMenuDTO;
import koreatech.in.domain.Global.UpdatedAt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DiningMenuMapper {

    DiningMenuMapper INSTANCE = Mappers.getMapper(DiningMenuMapper.class);

    @Mappings({
            @Mapping(source = "menu", target = "menu", qualifiedByName = "convertMenu"),
            @Mapping(source = "updated_at", target = "updated_at", qualifiedByName = "convertUpdatedAt")
    })
    DiningMenuDTO toDiningMenuDTO(DiningMenu diningMenu);

    @Named("convertMenu")
    default List<String> convertMenu(String menu) {
        return new Gson().fromJson(menu, new TypeToken<List<String>>() {
        }.getType());
    }

    @Named("convertUpdatedAt")
    default String convertUpdatedAt(Date updated_at) {
        return UpdatedAt.from(updated_at).dateForm();
    }
}
