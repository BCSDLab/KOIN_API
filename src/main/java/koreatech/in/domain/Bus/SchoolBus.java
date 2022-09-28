package koreatech.in.domain.Bus;

import com.google.gson.reflect.TypeToken;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import koreatech.in.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SchoolBus extends Bus {

    private final String[] daysStr = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

    private static final Type arrivalInfoType = new TypeToken<SchoolBusArrivalInfo>() {
    }.getType();

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public BusRemainTime getNowAndNextBusRemainTime(String depart, String arrival) {
        BusRemainTime response = new BusRemainTime();
        try {
            BusNodeEnum busNode = BusNodeEnum.valueOf(depart, arrival);
            String direction = busNode.getDirection();
            String todayName = getDayName(LocalDateTime.now());
            String waypoint = "to".equals(direction) ? busNode.getDepart().getKorName() : busNode.getArrival().getKorName();

            List<SchoolBusArrivalInfo> arrivalInfos = findForRealtimeBus(todayName, RegionEnum.천안.name(), direction, waypoint);
            arrivalInfos.forEach(info -> {
                List<SchoolBusTimetable> timetables = info.getRoutes();
                timetables
                        .forEach(System.out::println);
            });

        } catch (NullPointerException | IllegalArgumentException e) {
            return response;
        }

        return response;
    }

    private String getDayName(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        return daysStr[dayOfWeek.getValue() - 1];
    }

    private List<SchoolBusArrivalInfo> findForRealtimeBus(String targetDay, String region, String direction, String waypoint) {

        DBObject filterExpression = BasicDBObjectBuilder.start()
                .add("input", "$routes")
                .add("cond", new BasicDBObject("$in", new ArrayList<String>() {{
                            add(targetDay);
                            add("$$this.running_days");
                        }})
                )
                .get();

        DBObject projection = new BasicDBObject(
                "$project", BasicDBObjectBuilder.start()
                .add("_id", false)
                .add("region", true)
                .add("bus_type", true)
                .add("direction", true)
                .add("routes", new BasicDBObject("$filter", filterExpression))
                .get()
        );

        DBObject match = new BasicDBObject("$match", Criteria.where("region").is(region)
                .and("direction").is(direction)
                .and("routes").elemMatch(new Criteria().exists(true))
                .and("routes.arrival_info.node_name").regex(String.format("^%s", waypoint))
                .getCriteriaObject()
        );

        List<DBObject> pipeline = new ArrayList<DBObject>() {{
            add(projection);
            add(match);
        }};

        AggregationOutput results = mongoTemplate.getCollection("bus_timetables").aggregate(pipeline);
        List<SchoolBusArrivalInfo> arrivalInfos = new ArrayList<>();
        results.results().forEach(dbObject -> {
            arrivalInfos.add(gson.fromJson(dbObject.toString(), arrivalInfoType));
        });

        return arrivalInfos;
    }

    @Override
    public List<? extends BusTimetable> getTimetables(String busType, String direction, String region) {

        SchoolBusArrivalInfo arrivalInfo = busRepository.findByCourse(busType, direction, region);
        return Optional.ofNullable(arrivalInfo)
                .map(SchoolBusArrivalInfo::getRoutes)
                .orElseGet(ArrayList::new);
    }

    @Override
    public void cacheBusArrivalInfo() {
        throw new UnsupportedOperationException();
    }
}
