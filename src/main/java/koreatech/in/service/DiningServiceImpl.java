package koreatech.in.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import koreatech.in.domain.DiningMenu;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.DiningMapper;
import org.apache.velocity.runtime.directive.Parse;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Type;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static koreatech.in.domain.DomainToMap.domainToMap;

@Service
public class DiningServiceImpl implements DiningService {
    @Inject
    DiningMapper diningMapper;

    @Override
    public List<Map<String, Object>> getDinings(String from) throws Exception {
        java.util.Date uDate = new java.util.Date();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
            dateFormat.setLenient(false);
            uDate = dateFormat.parse(from);
        } catch(ParseException e) {
            throw new PreconditionFailedException(new ErrorMessage("Date form is invalid", 0));
        }
        java.sql.Date date = new java.sql.Date(uDate.getTime());

        List<DiningMenu> dinings = diningMapper.getList(date);

        List<Map<String, Object>> menus = new ArrayList<Map<String, Object>>();

        JsonConstructor con = new JsonConstructor();
        for (DiningMenu dining : dinings) {
            Map<String, Object> convertMenu = domainToMap(dining);
            try {
                convertMenu.replace("menu", con.arrayStringParse(dining.getMenu()));
            } catch (Exception e) {
            }
            menus.add(convertMenu);
        }

        return menus.stream().filter(dining -> DiningMenu.isValidatedPlaces(dining.get("place").toString())).collect(Collectors.toList());
    }
}
