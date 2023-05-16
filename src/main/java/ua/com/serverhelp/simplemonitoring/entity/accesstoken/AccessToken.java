package ua.com.serverhelp.simplemonitoring.entity.accesstoken;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(nullable = false)
    private Organization organization;
}
