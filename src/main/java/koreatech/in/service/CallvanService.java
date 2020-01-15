package koreatech.in.service;

import koreatech.in.domain.Callvan.Company;
import koreatech.in.domain.Callvan.Participant;
import koreatech.in.domain.Callvan.Room;

import java.util.List;
import java.util.Map;

public interface CallvanService {
    List<Company> getCompanies();
    Company getCompany(int id);
    Company increaseCallCount(int id);
    Company createCompanyForAdmin(Company company);
    Company updateCompanyForAdmin(Company company, int id);
    Map<String, Object> deleteCompanyForAdmin(int id);

    List<Room> getRooms();
    Room createRoom(Room room);
    Room getRoom(int id);
    Room updateRoom(Room room, int id);
    Map<String, Object> deleteRoom(int id);

    Room participateRoom(Participant participant);
    Room unParticipateRoom(Participant participant);
}
