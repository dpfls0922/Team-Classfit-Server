package classfit.example.classfit.drive.domain;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("PERSONAL")
public class PersonalDrive extends Drive {

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static Drive toEntity(
            String fileName,
            String originUrl,
            ObjectMetadata metadata,
            Member member,
            LocalDate dateTime
    ) {
        return PersonalDrive.builder()
                .objectName(fileName)
                .objectUrl(originUrl)
                .objectSize(DriveUtil.formatFileSize(metadata.getContentLength()))
                .objectType(metadata.getContentType())
                .uploadedBy(member.getName())
                .uploadedAt(dateTime)
                .isDeleted(false)
                .member(member)
                .build();
    }
}
