package com.seecoder.BlueWhale.po;

import com.seecoder.BlueWhale.enums.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role {
				@Id
				@GeneratedValue(strategy = GenerationType.IDENTITY)
				@Column(name = "id")
				private Integer roleId;

				@Basic
				@Enumerated(EnumType.STRING)
				@Column(name = "role")
				private RoleEnum role;

				@ManyToMany(fetch = FetchType.LAZY)
				@JoinTable(name = "role_privilege",
												joinColumns = @JoinColumn(name = "role_id"),
												inverseJoinColumns = @JoinColumn(name = "privilege_id"))
				private Set<Privilege> privileges;


}