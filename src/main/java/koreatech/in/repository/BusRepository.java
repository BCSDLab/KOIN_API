package koreatech.in.repository;

import koreatech.in.domain.Bus.SchoolBusArrivalInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends MongoRepository<SchoolBusArrivalInfo, Long> {

    @Query(value = "{}", fields = "{ '_id': false, 'courses': false }")
    List<SchoolBusArrivalInfo> findOnlyCourses();

    @Query(value = "{ 'bus_type': ?0, 'direction': ?1, 'region': ?2}")
    SchoolBusArrivalInfo findByCourse(String busType, String direction, String region);
}
