package koreatech.in.domain.Callvan;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class Room {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @ApiModelProperty(notes = "방장을 맡은 유저의 user_id", example = "10")
    private Integer user_id;
    @NotNull(groups = ValidationGroups.Create.class, message = "출발 장소는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "출발 장소", example = "병천")
    private String departure_place;
    @NotNull(groups = ValidationGroups.Create.class, message = "출발 시간은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "출발 시간", example = "2018-03-18 11:09:38")
    private Date departure_datetime;
    @NotNull(groups = ValidationGroups.Create.class, message = "도착 장소는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "도착 장소", example = "야우리")
    private String arrival_place;
    @NotNull(groups = ValidationGroups.Create.class, message = "최대 인원은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "최대 인원", example = "5")
    private Integer maximum_people;
    @ApiModelProperty(notes = "현재 인원", example = "2")
    private Integer current_people;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    private List<Participant> participants;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getDeparture_place() {
        return departure_place;
    }

    public void setDeparture_place(String departure_place) {
        this.departure_place = departure_place;
    }

    public Date getDeparture_datetime() {
        return departure_datetime;
    }

    public void setDeparture_datetime(Date departure_datetime) {
        this.departure_datetime = departure_datetime;
    }

    public String getArrival_place() {
        return arrival_place;
    }

    public void setArrival_place(String arrival_place) {
        this.arrival_place = arrival_place;
    }

    public Integer getMaximum_people() {
        return maximum_people;
    }

    public void setMaximum_people(Integer maximum_people) {
        this.maximum_people = maximum_people;
    }

    public Integer getCurrent_people() {
        return current_people;
    }

    public void setCurrent_people(Integer current_people) {
        this.current_people = current_people;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", departure_place='" + departure_place + '\'' +
                ", departure_datetime=" + departure_datetime +
                ", arrival_place='" + arrival_place + '\'' +
                ", maximum_people=" + maximum_people +
                ", current_people=" + current_people +
                ", is_deleted=" + is_deleted +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }

    public void update(Room room) {
        if(room.departure_place != null) {
            this.departure_place = room.departure_place;
        }
        if(room.departure_datetime != null) {
            this.departure_datetime = room.departure_datetime;
        }
        if(room.arrival_place != null) {
            this.arrival_place = room.arrival_place;
        }
        if(room.maximum_people != null) {
            this.maximum_people = room.maximum_people;
        }
        if(room.current_people != null) {
            this.current_people = room.current_people;
        }
        if(room.is_deleted != null) {
            this.is_deleted = room.is_deleted;
        }
    }
}
