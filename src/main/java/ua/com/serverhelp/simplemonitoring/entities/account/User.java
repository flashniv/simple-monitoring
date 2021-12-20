package ua.com.serverhelp.simplemonitoring.entities.account;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE,region = "account")
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private boolean accountNonExpired;
    @Getter
    @Setter
    private boolean accountNonLocked;
    @Getter
    @Setter
    private boolean credentialsNonExpired;
    @Getter
    @Setter
    private boolean enabled;
    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Role> roles;
}
