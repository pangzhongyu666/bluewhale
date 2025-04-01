package com.seecoder.BlueWhale.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "privilege")
public class Privilege {
				@Id
				@GeneratedValue(strategy = GenerationType.IDENTITY)
				@Column(name = "id")
				private Integer privilegeId;

				@Basic
				@Column(name = "privilege", unique = true, nullable = false)
				private String privilegeMethod;

				@Basic
				@Column(name = "description")
				private String description;

				@ManyToMany(mappedBy = "privileges", fetch = FetchType.LAZY)
				private Set<Role> roles;
}