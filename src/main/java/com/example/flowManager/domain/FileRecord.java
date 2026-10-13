package com.example.flowManager.domain;

import com.example.flowManager.enums.FileStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "file_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRecord {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private FileStatus status;

    @Column(name = "source_path", nullable = false, length = 500)
    private String sourcePath;

    @Column(name = "result_path", length = 500)
    private String resultPath;
}
