package com.example.flowManager.repository;

import com.example.flowManager.domain.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, UUID> {
}
