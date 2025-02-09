package classfit.example.classfit.drive.domain;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("SHARED")
public class SharedDrive extends Drive {

    @ManyToOne
    @JoinColumn(name = "academy_id")
    private Academy academy;

    public static Drive toEntity(
            String fileName,
            String folderPath,
            String originUrl,
            ObjectMetadata metadata,
            Member member
    ) {
        return SharedDrive.builder()
                .objectName(fileName)
                .objectPath(folderPath)
                .objectUrl(originUrl)
                .objectSize(DriveUtil.formatFileSize(metadata.getContentLength()))
                .objectType(metadata.getContentType())
                .uploadedBy(member.getName())
                .academy(member.getAcademy())
                .build();
    }
}
