package koreatech.in.repository;

import koreatech.in.domain.Bus.SchoolBusArrivalInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends MongoRepository<SchoolBusArrivalInfo, Long> {

    @Query(value = "{}", fields = "{ '_id': false, 'courses': false }")
    List<SchoolBusArrivalInfo> findAllCourses();
}
