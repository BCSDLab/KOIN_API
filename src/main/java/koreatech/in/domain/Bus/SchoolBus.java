package koreatech.in.domain.Bus;

import com.google.gson.reflect.TypeToken;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import koreatech.in.domain.Version.VersionTypeEnum;
import koreatech.in.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

public abstract class SchoolBus extends Bus {

    private final String[] daysStr = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    private static final Type arrivalInfoType = new TypeToken<SchoolBusArrivalInfo>() {
    }.getType();

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public BusRemainTime getNowAndNextBusRemainTime(String busType, String depart, String arrival) {
        BusRemainTime response = new BusRemainTime(busType);
        try {
            BusNodeEnum busNode = BusNodeEnum.valueOf(depart, arrival);
            LocalDateTime nowDateTime = LocalDateTime.now();
            String todayName = getDayName(nowDateTime);

            List<SchoolBusTimetable.ArrivalNode> targetNodes = new ArrayList<>();
            List<SchoolBusArrivalInfo> arrivalInfos = findForRealtimeBus(todayName, RegionEnum.천안.name(), busType);
            arrivalInfos.forEach(info -> {

                List<SchoolBusTimetable> timetables = info.getRoutes();
                timetables.forEach(timetable -> {

                    List<SchoolBusTimetable.ArrivalNode> arrivalNodes = timetable.getArrival_info();
                    int departIndex = arrivalNodes.size();
                    int arrivalIndex = 0;
                    for (int i = 0; i < arrivalNodes.size(); i++) {
                        SchoolBusTimetable.ArrivalNode node = arrivalNodes.get(i);
                        if (isWaypoint(node.getNode_name(), busNode.getDepart())) {
                            departIndex = Integer.min(departIndex, i);
                        } else if (isWaypoint(node.getNode_name(), busNode.getArrival())) {
                            arrivalIndex = Integer.max(arrivalIndex, i);
                        }
                    }

                    if (departIndex < arrivalIndex) {
                        targetNodes.add(arrivalNodes.get(departIndex));
                    }
                });
            });

            if (targetNodes.isEmpty()) {
                return response;
            }

            Collections.sort(targetNodes);

            final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            final Integer nowBusIndex = findClosestBus(targetNodes, nowDateTime);
            if (nowBusIndex == null) {
                return response;
            }
            int nextBusIndex = (nowBusIndex + 1) % targetNodes.size();

            final SchoolBusTimetable.ArrivalNode nowBusTime = targetNodes.get(nowBusIndex);
            SchoolBusTimetable.ArrivalNode nextBusTime = targetNodes.get(nextBusIndex);
            while (nowBusIndex != nextBusIndex && Objects.equals(nowBusTime.getArrival_time(), nextBusTime.getArrival_time())) {
                nextBusIndex = (nextBusIndex + 1) % targetNodes.size();
                nextBusTime = targetNodes.get(nextBusIndex);
            }

            LocalDateTime nowDepartureTime = LocalTime.parse(nowBusTime.getArrival_time(), timeFormatter).atDate(nowDateTime.toLocalDate());
            LocalDateTime nextDepartureTime = LocalTime.parse(nextBusTime.getArrival_time(), timeFormatter).atDate(nowDateTime.toLocalDate());

            if (nowBusIndex >= nextBusIndex) {
                return new BusRemainTime.Builder()
                        .busType(busType)
                        .nowRemainTime(
                                new BusRemainTime.RemainTime(null, (int) ChronoUnit.SECONDS.between(nowDateTime, nowDepartureTime))
                        )
                        .build();
            }
            return new BusRemainTime.Builder()
                    .busType(busType)
                    .nowRemainTime(
                            new BusRemainTime.RemainTime(null, (int) ChronoUnit.SECONDS.between(nowDateTime, nowDepartureTime))
                    )
                    .nextRemainTime(
                            new BusRemainTime.RemainTime(null, (int) ChronoUnit.SECONDS.between(nowDateTime, nextDepartureTime))
                    )
                    .build();


        } catch (NullPointerException | IllegalArgumentException e) {
            return response;
        }
    }

    private Integer findClosestBus(List<SchoolBusTimetable.ArrivalNode> arrivalInfos, LocalDateTime at) {
        final LocalTime nowTime = at.toLocalTime();
        for (int i = 0; i < arrivalInfos.size(); i++) {
            SchoolBusTimetable.ArrivalNode timetable = arrivalInfos.get(i);
            LocalTime departureTime = LocalTime.parse(timetable.getArrival_time());

            if (nowTime.isBefore(departureTime)) {
                return i;
            }
        }

        return null;
    }

    private boolean isWaypoint(String nodeName, MajorStationEnum waypoint) {
        final String waypointRegex = String.format("^(?:%s|%s).*$", waypoint.getKorName(), String.join("|", waypoint.getSynonyms()));
        return Pattern.matches(waypointRegex, nodeName);
    }

    private String getDayName(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        return daysStr[dayOfWeek.getValue() - 1];
    }

    private List<SchoolBusArrivalInfo> findForRealtimeBus(String targetDay, String region, String busType) {

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

        DBObject match = new BasicDBObject("$match", Criteria
                .where("region").is(region)
                .and("bus_type").is(busType)
                .and("routes").elemMatch(new Criteria().exists(true))
                .getCriteriaObject()
        );

        AggregationOutput results = mongoTemplate.getCollection("bus_timetables").aggregate(match, projection);
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
    public SingleBusTime searchBusTime(String busType, String depart, String arrival, LocalDateTime at) {
        BusNodeEnum busNode = BusNodeEnum.valueOf(depart, arrival);
        String todayName = getDayName(at);

        List<SchoolBusTimetable.ArrivalNode> targetNodes = new ArrayList<>();
        List<SchoolBusArrivalInfo> arrivalInfos = findForRealtimeBus(todayName, RegionEnum.천안.name(), busType);
        arrivalInfos.forEach(info -> {

            List<SchoolBusTimetable> timetables = info.getRoutes();
            timetables.forEach(timetable -> {

                List<SchoolBusTimetable.ArrivalNode> arrivalNodes = timetable.getArrival_info();
                int departIndex = arrivalNodes.size();
                int arrivalIndex = 0;
                for (int i = 0; i < arrivalNodes.size(); i++) {
                    SchoolBusTimetable.ArrivalNode node = arrivalNodes.get(i);
                    if (isWaypoint(node.getNode_name(), busNode.getDepart())) {
                        departIndex = Integer.min(departIndex, i);
                    } else if (isWaypoint(node.getNode_name(), busNode.getArrival())) {
                        arrivalIndex = Integer.max(arrivalIndex, i);
                    }
                }

                if (departIndex < arrivalIndex) {
                    targetNodes.add(arrivalNodes.get(departIndex));
                }
            });
        });

        if (targetNodes.isEmpty()) {
            return null;
        }

        Collections.sort(targetNodes);

        final Integer nowBusIndex = findClosestBus(targetNodes, at);
        final String arrivalTime = nowBusIndex == null ? null : targetNodes.get(nowBusIndex).getArrival_time();

        return new SingleBusTime(busType, arrivalTime);
    }

    @Override
    public void cacheBusArrivalInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VersionTypeEnum getVersionType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateVersion() {
        throw new UnsupportedOperationException();
    }
}
