
package vn.edu.hcmute.storemanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles", schema = "dbo")
public class AppRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true, length = 30)
    private String name;

    protected AppRole() {}

    public AppRole(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
