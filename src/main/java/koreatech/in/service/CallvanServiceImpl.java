package koreatech.in.service;

import koreatech.in.domain.Callvan.Company;
import koreatech.in.domain.Callvan.Participant;
import koreatech.in.domain.Callvan.Room;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.User.User;
import koreatech.in.exception.*;
import koreatech.in.repository.CallvanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CallvanServiceImpl implements CallvanService {
    @Inject
    CallvanMapper callvanMapper;

    @Inject
    JwtValidator jwtValidator;

    @Override
    public Company createCompanyForAdmin(Company company) {
        if (company.getPay_bank() == null) {
            company.setPay_bank(false);
        }

        if (company.getPay_card() == null) {
            company.setPay_card(false);
        }

        if (company.getIs_deleted() == null) {
            company.setIs_deleted(false);
        }

        Company selectCompany = callvanMapper.getCompanyByNameForAdmin(company.getName());
        if (selectCompany != null) {
            throw new ConflictException(new ErrorMessage("duplicate company name", 0));
        }

        callvanMapper.createCompanyForAdmin(company);

        return company;
    }

    @Override
    public Company updateCompanyForAdmin(Company company, int id) {
        if (company.getName() != null) {
            Company selectCompany = callvanMapper.getCompanyByNameForAdmin(company.getName());
            if (selectCompany != null && !selectCompany.getId().equals(id)) {
                throw new ConflictException(new ErrorMessage("duplicate company name", 0));
            }
        }

        Company selectCompany = callvanMapper.getCompanyForAdmin(id);
        if (selectCompany == null) {
            throw new NotFoundException(new ErrorMessage("No Company", 0));
        }

        selectCompany.update(company);
        callvanMapper.updateCompany(selectCompany);

        return selectCompany;
    }

    @Override
    public Map<String, Object> deleteCompanyForAdmin(int id) {
        Company selectCompany = callvanMapper.getCompanyForAdmin(id);
        if (selectCompany == null) {
            throw new NotFoundException(new ErrorMessage("No Company", 0));
        }

        callvanMapper.deleteCompanyForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public List<Room> getRooms() {
        List<Room> rooms = callvanMapper.getRooms();

        return rooms;
    }

    @Override
    public List<Company> getCompanies() {
        return callvanMapper.getCompanies();
    }

    @Override
    public Company getCompany(int id) {
        Company company = callvanMapper.getCompany(id);

        if (company == null) {
            throw new NotFoundException(new ErrorMessage("No Company", 0));
        }

        return company;
    }

    @Override
    public Company increaseCallCount(int id) {
        Company company = callvanMapper.getCompany(id);

        if (company == null) {
            throw new NotFoundException(new ErrorMessage("No Company", 0));
        }

        company.setHit(company.getHit() + 1);
        callvanMapper.updateCompany(company);

        return company;
    }

    @Transactional
    @Override
    public Room createRoom(Room room) {
        User user = jwtValidator.validate();

        if (room.getCurrent_people() == null) {
            room.setCurrent_people(1);
        }

        room.setUser_id(user.getId());

        callvanMapper.createRoom(room);
        Participant participant = new Participant();
        participant.setRoom_id(room.getId());
        participant.setUser_id(user.getId());
        callvanMapper.createParticipant(participant);

        return callvanMapper.getRoom(room.getId());
    }

    @Override
    public Room getRoom(int id) {
        Room room = callvanMapper.getRoom(id);

        if (room == null) {
            throw new NotFoundException(new ErrorMessage("No Room", 0));
        }

        List<Participant> participants = callvanMapper.getParticipantsInRoom(id);

        room.setParticipants(participants);

        return room;
    }

    @Override
    public Room updateRoom(Room room, int id) {
        Room selectRoom = callvanMapper.getRoom(id);

        if (selectRoom == null) {
            throw new NotFoundException(new ErrorMessage("No Room", 0));
        }

        selectRoom.update(room);

        callvanMapper.updateRoom(selectRoom);

        return selectRoom;
    }

    @Override
    public Map<String, Object> deleteRoom(int id) {
        Room room = callvanMapper.getRoom(id);

        if (room == null) {
            throw new NotFoundException(new ErrorMessage("No Room", 0));
        }

        room.setIs_deleted(true);

        callvanMapper.updateRoom(room);

        return new HashMap<String, Object>() {{
            put("success", "delete room");
        }};
    }

    @Transactional
    @Override
    public Room participateRoom(Participant participant) {
        User user = jwtValidator.validate();

        participant.setUser_id(user.getId());

        Participant selectParticipant = callvanMapper.getParticipantInRoom(participant.getRoom_id(), participant.getUser_id()).get(0);

        if (selectParticipant != null) {
            throw new ConflictException(new ErrorMessage("already participate in room", 0));
        }

        Room room = callvanMapper.getRoom(participant.getRoom_id());
        if (room == null) {
            throw new NotFoundException(new ErrorMessage("No Room", 0));
        }

        if (room.getCurrent_people() >= room.getMaximum_people()) {
            throw new NotFoundException(new ErrorMessage("Not Allow Enter Room", 1));
        }

        callvanMapper.createParticipant(participant);

        room.setCurrent_people(callvanMapper.getParticipantsInRoom(participant.getRoom_id()).size());

        return room;
    }

    @Transactional
    @Override
    public Room unParticipateRoom(Participant participant) {
        User user = jwtValidator.validate();

        participant.setUser_id(user.getId());

        Participant selectParticipant = callvanMapper.getParticipantInRoom(participant.getRoom_id(), participant.getUser_id()).get(0);

        if (selectParticipant == null) {
            throw new NotFoundException(new ErrorMessage("already participate in room", 0));
        }

        Room room = callvanMapper.getRoom(participant.getRoom_id());
        participant.setIs_deleted(true);
        callvanMapper.updateParticipant(participant);

        int cnt = room.getCurrent_people();
        room.setCurrent_people(cnt - 1);

        if (cnt == 1) {
            room.setIs_deleted(true);
        } else {
            if (room.getUser_id().equals(participant.getUser_id())) {
                Participant another = callvanMapper.getParticipantsInRoom(participant.getRoom_id()).get(0);
                room.setUser_id(another.getUser_id());
            }
        }

        callvanMapper.updateRoom(room);

        return room;
    }
}
