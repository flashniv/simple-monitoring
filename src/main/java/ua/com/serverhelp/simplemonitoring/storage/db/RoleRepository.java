package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.account.Role;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
