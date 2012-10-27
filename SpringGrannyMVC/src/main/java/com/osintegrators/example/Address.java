package com.osintegrators.example;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id; 
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({@NamedQuery(name="Address.findAll",query="select a from Address a"),
	           @NamedQuery(name="Address.findByName",query="select a from Address a where a.name = ?1")})
@Indexed
@AnalyzerDef(name = "customanalyzer",
    tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
    filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class),
        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
            @Parameter(name = "language", value = "English")
        })
    })
public class Address {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

    @Field(index=Index.TOKENIZED, store=Store.NO)
	private String name;
    @Field(index=Index.TOKENIZED, store=Store.NO)
	private String email;
    @Field(index=Index.TOKENIZED, store=Store.NO)
	private String phone;
    @Field(index=Index.TOKENIZED, store=Store.NO)
	private String address;
	
	public Address() {}

	public Address(String name, String address, String phone, String email) {
		this.name = name;
		this.address=address;
		this.phone=phone;
		this.email=email;
	}

	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public void setName(String name) {
		this.name=name;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
