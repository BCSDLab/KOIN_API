package koreatech.in.mapstruct.normal.dining;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import koreatech.in.domain.Dining.DiningMenu;
import koreatech.in.domain.Dining.DiningMenuDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DiningMenuConverter {

    DiningMenuConverter INSTANCE = Mappers.getMapper(DiningMenuConverter.class);

    @Mappings({
            @Mapping(source = "menu", target = "menu", qualifiedByName = "convertMenu")
    })
    DiningMenuDTO toDiningMenuDTO(DiningMenu diningMenu);

    @Named("convertMenu")
    default List<String> convertMenu(String menu) {
        return new Gson().fromJson(menu, new TypeToken<List<String>>() {
        }.getType());
    }
}
