package koreatech.in.domain.User.owner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class OwnerAttachments {
    private List<OwnerAttachment> attachments;

    public static OwnerAttachments from(List<OwnerAttachment> attachments) {
        return new OwnerAttachments(attachments);
    }

    public Set<String> getExistAttachmentUrls() {
        validateNonNullList();

        return new HashSet<>(getAttachmentUrls());
    }

    private void validateNonNullList() {
        if(getAttachments() == null) {
            throw new RuntimeException("DB에 존재하는 사장님의 첨부파일은 비어있을 수 없습니다.");
        }
    }

    private List<String> getAttachmentUrls() {
        return getAttachments()
                .stream()
                .map(OwnerAttachment::getFileUrl)
                .collect(Collectors.toList());
    }
    
    public OwnerAttachments selectToAdd(Set<String> existAttachmentUrls) {
        return OwnerAttachments.from(filteredAttachmentsBy(
                ownerAttachment -> !existAttachmentUrls.contains(ownerAttachment.getFileUrl()))
        );
    }


    public OwnerAttachments selectToDelete(Set<String> existAttachmentUrls) {
        return OwnerAttachments.from(filteredAttachmentsBy(
                ownerAttachment -> existAttachmentUrls.contains(ownerAttachment.getFileUrl())
        ));
    }

    private List<OwnerAttachment> filteredAttachmentsBy(Predicate<OwnerAttachment> filterPredicate) {
        validateNonNullList();

        return getAttachments()
                .stream()
                .filter(filterPredicate)
                .collect(Collectors.toList());
    }
}
