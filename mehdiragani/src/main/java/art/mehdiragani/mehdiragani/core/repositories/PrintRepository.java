package art.mehdiragani.mehdiragani.core.repositories;

import art.mehdiragani.mehdiragani.core.models.Print;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PrintRepository extends JpaRepository<Print, UUID>, JpaSpecificationExecutor<Print> {

} 