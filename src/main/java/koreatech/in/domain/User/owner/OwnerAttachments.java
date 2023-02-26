package koreatech.in.domain.User.owner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        validateNonNullList(attachments);

        return new OwnerAttachments(attachments);
    }

    public boolean isEmpty() {
        return attachments.isEmpty();
    }

    // this 와 other 의 차집합을 반환
    public OwnerAttachments removeDuplicatesFrom(OwnerAttachments other) {
        validateNonNullList(getAttachments());

        Set<String> otherAttachments = other.makeAttachments();
        List<OwnerAttachment> attachmentsRemovedDuplicates = removeDuplicates(otherAttachments);

        return OwnerAttachments.from(attachmentsRemovedDuplicates);
    }

    private static void validateNonNullList(List<OwnerAttachment> attachments) {
        if(attachments == null) {
            throw new RuntimeException("DB에 존재하는 사장님의 첨부파일은 비어있을 수 없습니다.");
        }
    }

    private Set<String> makeAttachments() {
        validateNonNullList(getAttachments());

        return new HashSet<>(getAttachmentUrls());
    }

    private List<String> getAttachmentUrls() {
        return getAttachments()
                .stream()
                .map(OwnerAttachment::getFileUrl)
                .collect(Collectors.toList());
    }

    private List<OwnerAttachment> removeDuplicates(Set<String> otherAttachments) {
        return getAttachments()
                .stream()
                .filter(ownerAttachment -> !otherAttachments.contains(ownerAttachment.getFileUrl()))
                .collect(Collectors.toList());
    }
}
