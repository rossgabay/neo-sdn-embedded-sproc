
package com.rgabay.embedded_sdn_sproc.domain;

import org.neo4j.ogm.annotation.*;

import java.util.*;

@NodeEntity(label="Person")
public class Person {

    private Person() {
        // Empty constructor required as of Neo4j API 2.0.5
    };

    public Person(String name) {
        this.name = name;
    }

	@GraphId
    private Long id;

    public Long getId() {
        return id;
    }

    @Property
	private String name;

	@Property
	List<Long> arbitraryLongs;

    public List<Long> getArbitraryLongs() {
        return arbitraryLongs;
    }

    public void setArbitraryLongs(List<Long> arbitraryLongs) {
        this.arbitraryLongs = arbitraryLongs;
    }

    public String toString() {

		return this.name + "'s arbitraryLong values:  => "
				+ Optional.ofNullable(this.arbitraryLongs).orElse(new ArrayList<Long>());

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
